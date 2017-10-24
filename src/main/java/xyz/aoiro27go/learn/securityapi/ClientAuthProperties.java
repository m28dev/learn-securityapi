/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xyz.aoiro27go.learn.securityapi;

import java.util.ResourceBundle;

/**
 *
 * @author m28dev
 */
public class ClientAuthProperties {

    private ResourceBundle rb = null;

    private ClientAuthProperties() {
        rb = ResourceBundle.getBundle("clientauth");
    }

    private static class ClientAuthPropertiesHolder {
        private static final ClientAuthProperties INSTANCE = new ClientAuthProperties();
    }

    public static ClientAuthProperties getInstace() {
        return ClientAuthPropertiesHolder.INSTANCE;
    }

    public String getClientId() {
        return rb.getString("client_id");
    }

    public String getClientSecret() {
        return rb.getString("client_secret");
    }

    public String getRedirectUri() {
        return rb.getString("redirect_uri");
    }
}
