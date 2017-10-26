/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xyz.aoiro27go.learn.securityapi.response;

import java.util.List;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 * @author m28dev
 */
public class OpenIdProviderMetadata {

    @JsonbProperty(value = "issuer")
    private String issuer;

    @JsonbProperty(value = "authorization_endpoint")
    private String authorizationEndpoint;

    @JsonbProperty(value = "token_endpoint")
    private String tokenEndpoint;

    @JsonbProperty(value = "userinfo_endpoint")
    private String userinfoEndpoint;

    @JsonbProperty(value = "jwks_uri")
    private String jwksUri;

    @JsonbProperty(value = "registration_endpoint")
    private String registrationEndpoint;

    @JsonbProperty(value = "scopes_supported")
    private List<String> scopesSupported;

    @JsonbProperty(value = "response_types_supported")
    private List<String> responseTypesSupported;

    @JsonbProperty(value = "subject_types_supported")
    private List<String> subjectTypesSupported;

    @JsonbProperty(value = "id_token_signing_alg_values_supported")
    private List<String> idTokenSigningAlgValuesSupported;

    @JsonbProperty(value = "claims_supported")
    private List<String> claimsSupported;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getUserinfoEndpoint() {
        return userinfoEndpoint;
    }

    public void setUserinfoEndpoint(String userinfoEndpoint) {
        this.userinfoEndpoint = userinfoEndpoint;
    }

    public String getJwksUri() {
        return jwksUri;
    }

    public void setJwksUri(String jwksUri) {
        this.jwksUri = jwksUri;
    }

    public String getRegistrationEndpoint() {
        return registrationEndpoint;
    }

    public void setRegistrationEndpoint(String registrationEndpoint) {
        this.registrationEndpoint = registrationEndpoint;
    }

    public List<String> getScopesSupported() {
        return scopesSupported;
    }

    public void setScopesSupported(List<String> scopesSupported) {
        this.scopesSupported = scopesSupported;
    }

    public List<String> getResponseTypesSupported() {
        return responseTypesSupported;
    }

    public void setResponseTypesSupported(List<String> responseTypesSupported) {
        this.responseTypesSupported = responseTypesSupported;
    }

    public List<String> getSubjectTypesSupported() {
        return subjectTypesSupported;
    }

    public void setSubjectTypesSupported(List<String> subjectTypesSupported) {
        this.subjectTypesSupported = subjectTypesSupported;
    }

    public List<String> getIdTokenSigningAlgValuesSupported() {
        return idTokenSigningAlgValuesSupported;
    }

    public void setIdTokenSigningAlgValuesSupported(List<String> idTokenSigningAlgValuesSupported) {
        this.idTokenSigningAlgValuesSupported = idTokenSigningAlgValuesSupported;
    }

    public List<String> getClaimsSupported() {
        return claimsSupported;
    }

    public void setClaimsSupported(List<String> claimsSupported) {
        this.claimsSupported = claimsSupported;
    }

}
