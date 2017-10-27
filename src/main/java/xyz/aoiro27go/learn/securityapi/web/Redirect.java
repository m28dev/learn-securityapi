/*
 * The MIT License
 *
 * Copyright 2017 m28dev.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
