/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xyz.aoiro27go.learn.securityapi.response;

import javax.json.bind.annotation.JsonbProperty;

/**
 *
 * @author m28dev
 */
public class TokenResponse {

    @JsonbProperty(value = "access_token")
    private String accessToken;

    @JsonbProperty(value = "token_type")
    private String tokenType;

    @JsonbProperty(value = "expires_in")
    private String expiresIn;

    @JsonbProperty(value = "refresh_token")
    private String refreshToken;

    @JsonbProperty(value = "scope")
    private String scope;

    @JsonbProperty(value = "id_token")
    private String idToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
