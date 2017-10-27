/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xyz.aoiro27go.learn.securityapi;

import xyz.aoiro27go.learn.securityapi.response.OpenIdProviderMetadata;
import xyz.aoiro27go.learn.securityapi.response.JwksRoot;
import xyz.aoiro27go.learn.securityapi.response.JwksKeys;
import xyz.aoiro27go.learn.securityapi.response.IdToken;
import xyz.aoiro27go.learn.securityapi.response.TokenResponse;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.bind.JsonbBuilder;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.AutoApplySession;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author m28dev
 */
@AutoApplySession
@LoginToContinue(
        loginPage = "/redirect"
)
@ApplicationScoped
public class TestAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Inject
    private ClientAuthProperties clientAuthProperties;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {

        // コールバックをキャッチ
        if (clientAuthProperties.getRedirectUri().equals(request.getRequestURL().toString())) {

            // stateを確認
            String paramState = request.getParameter("state");
            
            HttpSession session = request.getSession();
            String sessionState = (String) session.getAttribute("state_param");
            
            session.removeAttribute("state_param");
            
            if (!paramState.equals(sessionState)) {
                return httpMessageContext.responseUnauthorized();
            }

            Client client = ClientBuilder.newClient();
            try {
                // Discovery
                OpenIdProviderMetadata openIdProviderMetadata = client.target(clientAuthProperties.getOpenidConfigurationURI())
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .get(OpenIdProviderMetadata.class);

                // Token EndpointにAuthorization Codeを送信する
                String clientId = clientAuthProperties.getClientId();
                String clientSecret = clientAuthProperties.getClientSecret();
                String redirectUri = clientAuthProperties.getRedirectUri();

                Form form = new Form();
                form.param("client_id", clientId);
                form.param("client_secret", clientSecret);
                form.param("grant_type", "authorization_code");
                form.param("code", request.getParameter("code"));
                form.param("redirect_uri", redirectUri);

                String tokenEndpoint = openIdProviderMetadata.getTokenEndpoint();
                WebTarget webTarget = client.target(tokenEndpoint);

                // トークンを取得
                TokenResponse tokenResponse = webTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED), TokenResponse.class);

                // ID Tokenを抽出
                String[] idTokenValues = tokenResponse.getIdToken().split("\\.");
                String payload = new String(Base64.getUrlDecoder().decode(idTokenValues[1]), StandardCharsets.UTF_8);
                IdToken idToken = JsonbBuilder.create().fromJson(payload, IdToken.class);

                // ID Tokenを検証
                if (!ValidateSignature(client, idTokenValues, openIdProviderMetadata)
                        || !IdTokenValidation(openIdProviderMetadata.getIssuer(), clientId, idToken)) {
                    return httpMessageContext.responseUnauthorized();
                }

                // このissuerのsubjectがユーザーとして登録されているか確認
                CredentialValidationResult result = identityStoreHandler.validate(new UsernamePasswordCredential(idToken.getIss(), idToken.getSub()));
                return httpMessageContext.notifyContainerAboutLogin(result.getCallerPrincipal(), result.getCallerGroups());

            } finally {
                client.close();
            }
        }

        return httpMessageContext.doNothing();
    }

    private boolean ValidateSignature(Client client, String[] idTokenValues, OpenIdProviderMetadata openIdProviderMetadata) {

        // ヘッダーを取得
        byte[] header = Base64.getUrlDecoder().decode(idTokenValues[0]);
        JsonReader reader = Json.createReader(new StringReader(new String(header, StandardCharsets.UTF_8)));
        JsonObject headerJson = reader.readObject();

        // アルゴリズムはRS256以外NG
        JsonString alg = (JsonString) headerJson.get("alg");
        if (!"RS256".equals(alg.getString())) {
            return false;
        }

        // JWKを特定
        JsonString kid = (JsonString) headerJson.get("kid");

        // JWK Setから使われたJWKを取得
        String jwksUri = openIdProviderMetadata.getJwksUri();
        JwksRoot jwksRoot = client.target(jwksUri).request().get(JwksRoot.class);
        JwksKeys key = jwksRoot.getKeys().stream()
                .filter(s -> s.getKid().equals(kid.getString()))
                .findFirst()
                .get();

        try {
            // 公開鍵を取得
            byte[] modulusB = Base64.getUrlDecoder().decode(key.getN());
            byte[] publicExponentB = Base64.getUrlDecoder().decode(key.getE());

            BigInteger modulus = new BigInteger(DatatypeConverter.printHexBinary(modulusB), 16);
            BigInteger pubExponent = new BigInteger(DatatypeConverter.printHexBinary(publicExponentB), 16);

            RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(modulus, pubExponent);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey pub = factory.generatePublic(publicSpec);

            // 正しく署名されていることを確認する
            String payload = idTokenValues[0] + "." + idTokenValues[1];
            byte[] signature = Base64.getUrlDecoder().decode(idTokenValues[2]);

            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(pub);
            verifier.update(payload.getBytes(StandardCharsets.UTF_8));

            // 検証NG
            if (!verifier.verify(signature)) {
                return false;
            }

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException ex) {
            Logger.getLogger(TestAuthenticationMechanism.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    private boolean IdTokenValidation(String issuer, String clientId, IdToken idToken) {

        // iss（issuer = ID Token発行者）とOPのIssuer Identifierが一致する
        if (!issuer.equals(idToken.getIss())) {
            return false;
        }
        // aud(audience = クライアント)とclient_idが一致する
        if (!clientId.equals(idToken.getAud())) {
            return false;
        }
        // 現在時刻が有効期限より後はダメ
        long createtime = (new Date().getTime()) / 1000;
        if (createtime > Long.parseLong(idToken.getExp())) {
            return false;
        }

        return true;
    }
}
