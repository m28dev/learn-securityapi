/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xyz.aoiro27go.learn.securityapi;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author m28dev
 */
@WebServlet("/redirect")
public class Redirect extends HttpServlet {

    private final String RESPONSE_TYPE = "code";
    private final String SCOPE = "openid";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ClientAuthProperties properties = ClientAuthProperties.getInstace();

        String clientId = properties.getClientId();
        String redirectUri = properties.getRedirectUri();

        StringBuilder url = new StringBuilder();
        url.append("https://accounts.google.com/o/oauth2/v2/auth?");
        url.append("response_type=").append(RESPONSE_TYPE);
        url.append("&scope=").append(SCOPE);
        url.append("&client_id=").append(clientId);
        url.append("&state=abcdefg123");
        url.append("&redirect_uri=").append(redirectUri);

        resp.sendRedirect(resp.encodeRedirectURL(url.toString()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
