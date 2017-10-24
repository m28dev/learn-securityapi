/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xyz.aoiro27go.learn.securityapi;

import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

            ClientAuthProperties properties = ClientAuthProperties.getInstace();
            String clientId = properties.getClientId();
            String clientSecret = properties.getClientSecret();
            String redirectUri = properties.getRedirectUri();

            // アクセストークンをもらいに行きます
            Form form = new Form();
            form.param("client_id", clientId);
            form.param("client_secret", clientSecret);
            form.param("grant_type", "authorization_code");
            form.param("code", request.getParameter("code")); // 必須項目なのでなかったらふつーにエラー
            form.param("redirect_uri", redirectUri);

            Client client = ClientBuilder.newClient();
            WebTarget webTarget = client.target("https://www.googleapis.com/oauth2/v4/token");

            TokenResponse tokenResponse = webTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED), TokenResponse.class);

            Logger.getLogger(TestAuthenticationMechanism.class.getName()).info(tokenResponse.getIdToken());

            client.close(); // TODO finally

            CredentialValidationResult result = identityStoreHandler.validate(new UsernamePasswordCredential("reza", "secret2"));
            return httpMessageContext.notifyContainerAboutLogin(result.getCallerPrincipal(), result.getCallerGroups());
        }

        return httpMessageContext.doNothing();
    }

}
