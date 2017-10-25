/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xyz.aoiro27go.learn.securityapi;

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

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {

        // とりあえずコールバックをキャッチ
        // ここってホスト名は入ってこないの？
        if (request.getRequestURI().equals("/learn-securityapi/cb")) {

            Client client = ClientBuilder.newClient();

            // Discovery
            Discovery  discovery = client.target("https://accounts.google.com/.well-known/openid-configuration").request(MediaType.APPLICATION_JSON_TYPE).get(Discovery.class);

            // アクセストークンをもらいに行きます
            ClientAuthProperties properties = ClientAuthProperties.getInstace();
            String clientId = properties.getClientId();
            String clientSecret = properties.getClientSecret();
            String redirectUri = properties.getRedirectUri();

            Form form = new Form();
            form.param("client_id", clientId);
            form.param("client_secret", clientSecret);
            form.param("grant_type", "authorization_code");
            form.param("code", request.getParameter("code")); // 必須項目なのでなかったらふつーにエラー
            form.param("redirect_uri", redirectUri);

            String tokenEndpoint = discovery.getTokenEndpoint();
            WebTarget webTarget = client.target(tokenEndpoint);

            TokenResponse tokenResponse = webTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED), TokenResponse.class);

            Logger.getLogger(TestAuthenticationMechanism.class.getName()).info(tokenResponse.getIdToken());

            /*
              ID Tokenを検証
             */
            String[] idTokenValues = tokenResponse.getIdToken().split("\\.");

            // ヘッダーを取得
            byte[] header = Base64.getUrlDecoder().decode(idTokenValues[0]);
            JsonReader reader = Json.createReader(new StringReader(new String(header, StandardCharsets.UTF_8)));
            JsonObject headerJson = reader.readObject();

            // アルゴリズムはRS256以外NG
            JsonString alg = (JsonString) headerJson.get("alg");
            if (!"RS256".equals(alg.getString())) {
                return httpMessageContext.responseUnauthorized();
            }

            // JWKを特定
            JsonString kid = (JsonString) headerJson.get("kid");

            // JWK Setから使われたJWKを取得
            String jwksUri = discovery.getJwksUri();
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

                boolean result = verifier.verify(signature);
                Logger.getLogger(TestAuthenticationMechanism.class.getName()).log(Level.INFO, String.valueOf(result));

                // 検証NG
                if (!result) {
                    return httpMessageContext.responseUnauthorized();
                }

            } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException ex) {
                Logger.getLogger(TestAuthenticationMechanism.class.getName()).log(Level.SEVERE, null, ex);
            }

            // ペイロードを取得
            String payload = new String(Base64.getUrlDecoder().decode(idTokenValues[1]), StandardCharsets.UTF_8);
            IdToken idToken = JsonbBuilder.create().fromJson(payload, IdToken.class);

            Logger.getLogger(TestAuthenticationMechanism.class.getName()).log(Level.INFO, idToken.getIss());

            // iss（issuer = ID Token発行者）とOPのIssuer Identifierと一致する
            if (!discovery.getIssuer().equals(idToken.getIss())) {
                return httpMessageContext.responseUnauthorized();
            }
            // aud(audience = クライアント)とclient_idが一致する
            if (!clientId.equals(idToken.getAud())) {
                return httpMessageContext.responseUnauthorized();
            }
            // 現在時刻が有効期限より後はダメ
            long createtime = (new Date().getTime()) / 1000;
            if (createtime > Long.parseLong(idToken.getExp())) {
                return httpMessageContext.responseUnauthorized();
            }
            // nonceをチェック
            if (idToken.getNonce() != null) {
                // iatのチェックもここでする
            }

            client.close(); // TODO finally

            CredentialValidationResult result = identityStoreHandler.validate(new UsernamePasswordCredential("reza", "secret2"));
            return httpMessageContext.notifyContainerAboutLogin(result.getCallerPrincipal(), result.getCallerGroups());
        }

        return httpMessageContext.doNothing();
    }

}
