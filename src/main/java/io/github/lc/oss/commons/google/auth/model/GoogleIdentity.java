package io.github.lc.oss.commons.google.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleIdentity {
    private String type;
    @JsonProperty("project_id")
    private String projectId;
    @JsonProperty("private_key_id")
    private String privatekeyId;
    @JsonProperty("private_key")
    private String privateKey;
    @JsonProperty("client_email")
    private String clientEmail;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("auth_uri")
    private String authUri;
    @JsonProperty("token_uri")
    private String tokenUri;
    @JsonProperty("auth_provider_x509_cert_url")
    private String authProviderX509CertUrl;
    @JsonProperty("client_x509_cert_url")
    private String clientX509CertUrl;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getPrivatekeyId() {
        return this.privatekeyId;
    }

    public void setPrivatekeyId(String privatekeyId) {
        this.privatekeyId = privatekeyId;
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getClientEmail() {
        return this.clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAuthUri() {
        return this.authUri;
    }

    public void setAuthUri(String authUri) {
        this.authUri = authUri;
    }

    public String getTokenUri() {
        return this.tokenUri;
    }

    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    public String getAuthProviderX509CertUrl() {
        return this.authProviderX509CertUrl;
    }

    public void setAuthProviderX509CertUrl(String authProviderX509CertUrl) {
        this.authProviderX509CertUrl = authProviderX509CertUrl;
    }

    public String getClientX509CertUrl() {
        return this.clientX509CertUrl;
    }

    public void setClientX509CertUrl(String clientX509CertUrl) {
        this.clientX509CertUrl = clientX509CertUrl;
    }
}
