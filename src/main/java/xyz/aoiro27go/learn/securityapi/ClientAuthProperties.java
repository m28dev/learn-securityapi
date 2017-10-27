/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xyz.aoiro27go.learn.securityapi;

import java.util.ResourceBundle;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author m28dev
 */
@ApplicationScoped
public class ClientAuthProperties {

    private ResourceBundle rb = null;

    public ClientAuthProperties() {
        rb = ResourceBundle.getBundle("clientauth");
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

    public String getOpenidConfigurationURI() {
        return rb.getString("openid_configuration_uri");
    }
}
