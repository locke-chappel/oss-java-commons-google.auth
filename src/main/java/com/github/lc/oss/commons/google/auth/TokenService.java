package com.github.lc.oss.commons.google.auth;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.github.lc.oss.commons.google.auth.model.GoogleIdentity;
import com.github.lc.oss.commons.jwt.Jwt;
import com.github.lc.oss.commons.jwt.JwtHeader;
import com.github.lc.oss.commons.signing.Algorithms;
import com.github.lc.oss.commons.util.CloseableUtil;

public class TokenService {
    /*
     * Based on https://cloud.google.com/functions/docs/securing/authenticating
     */
    public String forCloudFunction(String identityJson, String functionUrl) {
        if (com.github.lc.oss.commons.jwt.Util.isBlank(identityJson)) {
            return null;
        }

        GoogleIdentity identity = com.github.lc.oss.commons.jwt.Util.fromJson(identityJson, GoogleIdentity.class);
        PrivateKey secret = com.github.lc.oss.commons.signing.Util.loadPrivateKeyFromData(identity.getPrivateKey(), "RSA");

        Long now = this.now() / 1000l;
        Long expires = now + 60;

        Jwt jwt = new Jwt();
        jwt.getHeader().setKeyId(identity.getPrivatekeyId());
        jwt.getHeader().setAlgorithm(Algorithms.RS256);
        jwt.getHeader().put(JwtHeader.Keys.TokenType, "JWT");
        jwt.getPayload().setSubject(identity.getClientEmail());
        jwt.getPayload().setIssuedAt(now);
        jwt.getPayload().setExpiration(expires);
        jwt.getPayload().setTokenId(UUID.randomUUID().toString());
        jwt.getPayload().setIssuer(identity.getClientEmail());
        jwt.getPayload().setAudience(this.getCloudFunctionTokenUrl());
        jwt.getPayload().put("target_audience", functionUrl);

        byte[] payload = com.github.lc.oss.commons.jwt.Util.toJsonNoSignature(jwt).getBytes(StandardCharsets.UTF_8);
        String sig = jwt.getAlgorithm().getSignature(secret.getEncoded(), payload);
        jwt.setSignature(sig);
        String token = com.github.lc.oss.commons.jwt.Util.toJson(jwt);
        String body = "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=";
        body += URLEncoder.encode(token, StandardCharsets.UTF_8);

        HttpPost request = new HttpPost(this.getCloudFunctionTokenUrl());
        request.setHeader("Authorization", "Bearer " + token);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(new StringEntity(body, ContentType.TEXT_PLAIN));

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            client = this.getClient();
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String responseBody = entity == null ? null : EntityUtils.toString(entity);

            int status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                throw new RuntimeException("Error getting token: " + responseBody);
            }

            return responseBody;
        } catch (IOException ex) {
            throw new RuntimeException("Error getting token from Google", ex);
        } finally {
            CloseableUtil.close(response);
            CloseableUtil.close(client);
        }
    }

    protected String getCloudFunctionTokenUrl() {
        return "https://www.googleapis.com/oauth2/v4/token";
    }

    protected CloseableHttpClient getClient() {
        return HttpClients.createDefault();
    }

    protected long now() {
        return System.currentTimeMillis();
    }
}
