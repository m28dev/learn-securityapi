/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xyz.aoiro27go.learn.securityapi.web;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;
import xyz.aoiro27go.learn.securityapi.ClientAuthProperties;
import xyz.aoiro27go.learn.securityapi.response.OpenIdProviderMetadata;

/**
 *
 * @author m28dev
 */
@WebServlet("/redirect")
public class Redirect extends HttpServlet {

    private final String RESPONSE_TYPE = "code";
    private final String SCOPE = "openid";

    @Inject
    private ClientAuthProperties clientAuthProperties;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String clientId = clientAuthProperties.getClientId();
        String redirectUri = clientAuthProperties.getRedirectUri();

        byte[] state = new byte[16];
        try {
            SecureRandom.getInstance("SHA1PRNG").nextBytes(state);
            req.getSession(true).setAttribute("state_param", DatatypeConverter.printHexBinary(state));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Redirect.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }
        
        // Discovery
        Client client = ClientBuilder.newClient();
        try {
            OpenIdProviderMetadata openIdProviderMetadata = client.target(clientAuthProperties.getOpenidConfigurationURI())
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(OpenIdProviderMetadata.class);

            StringBuilder url = new StringBuilder();
            url.append(openIdProviderMetadata.getAuthorizationEndpoint());
            url.append("?response_type=").append(RESPONSE_TYPE);
            url.append("&scope=").append(SCOPE);
            url.append("&client_id=").append(clientId);
            url.append("&state=").append(DatatypeConverter.printHexBinary(state));
            url.append("&redirect_uri=").append(redirectUri);

            resp.sendRedirect(resp.encodeRedirectURL(url.toString()));
        } finally {
            client.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
