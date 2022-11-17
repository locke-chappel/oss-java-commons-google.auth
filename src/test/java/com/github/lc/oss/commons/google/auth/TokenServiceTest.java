package com.github.lc.oss.commons.google.auth;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.github.lc.oss.commons.testing.AbstractMockTest;
import com.github.lc.oss.commons.util.IoTools;

public class TokenServiceTest extends AbstractMockTest {
    private TokenService service = new TokenService() {
        private CloseableHttpClient client = Mockito.mock(CloseableHttpClient.class);

        @Override
        protected CloseableHttpClient getClient() {
            return this.client;
        };

        @Override
        protected String getCloudFunctionTokenUrl() {
            return "https://localhost";
        };
    };

    @Test
    public void test_codeCoverage() {
        TokenService service = new TokenService();

        Assertions.assertNotNull(service.getClient());
        Assertions.assertNotNull(service.getCloudFunctionTokenUrl());
    }

    @Test
    public void test_forCloudFunction_blanks() {
        String result = this.service.forCloudFunction(null, null);
        Assertions.assertNull(result);

        result = this.service.forCloudFunction("", null);
        Assertions.assertNull(result);

        result = this.service.forCloudFunction(" \t \r \n \t ", null);
        Assertions.assertNull(result);
    }

    @Test
    public void test_forCloudFunction_error() {
        try {
            Mockito.doAnswer(new Answer<CloseableHttpResponse>() {
                @Override
                public CloseableHttpResponse answer(InvocationOnMock invocation) throws Throwable {
                    throw new IOException("Boom!");
                }
            }).when(this.service.getClient()).execute(ArgumentMatchers.notNull());
        } catch (IOException ex) {
            Assertions.fail("Unexpected exception");
        }

        try {
            this.service.forCloudFunction(this.getTestIdentity(), "https://localhost/function/name");
            Assertions.fail("Expected exception");
        } catch (RuntimeException ex) {
            Assertions.assertEquals("Error getting token from Google", ex.getMessage());
        }
    }

    @Test
    public void test_forCloudFunction_noResponseBody() {
        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);

        try {
            Mockito.when(this.service.getClient().execute(ArgumentMatchers.notNull())).thenReturn(response);
        } catch (IOException ex) {
            Assertions.fail("Unexpected exception");
        }
        Mockito.when(response.getStatusLine()).thenReturn(statusLine);
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Mockito.when(response.getEntity()).thenReturn(null);

        String result = this.service.forCloudFunction(this.getTestIdentity(), "https://localhost/function/name");
        Assertions.assertNull(result);
    }

    @Test
    public void test_forCloudFunction_statusError() {
        final String json = "{\"error\":\"message\"}";

        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        HttpEntity responseEntity = Mockito.mock(HttpEntity.class);
        InputStream responseStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        try {
            Mockito.when(this.service.getClient().execute(ArgumentMatchers.notNull())).thenReturn(response);
            Mockito.when(responseEntity.getContent()).thenReturn(responseStream);
        } catch (IOException ex) {
            Assertions.fail("Unexpected exception");
        }
        Mockito.when(response.getEntity()).thenReturn(responseEntity);
        Mockito.when(response.getStatusLine()).thenReturn(statusLine);
        Mockito.when(statusLine.getStatusCode()).thenReturn(422);

        try {
            this.service.forCloudFunction(this.getTestIdentity(), "https://localhost/function/name");
            Assertions.fail("Expected exception");
        } catch (RuntimeException ex) {
            Assertions.assertEquals("Error getting token: " + json, ex.getMessage());
        }
    }

    @Test
    public void test_forCloudFunction() {
        final String token = "token.jwt.sig";

        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        HttpEntity responseEntity = Mockito.mock(HttpEntity.class);
        InputStream responseStream = new ByteArrayInputStream(token.getBytes(StandardCharsets.UTF_8));

        try {
            Mockito.when(this.service.getClient().execute(ArgumentMatchers.notNull())).thenReturn(response);
            Mockito.when(responseEntity.getContent()).thenReturn(responseStream);
        } catch (IOException ex) {
            Assertions.fail("Unexpected exception");
        }
        Mockito.when(response.getStatusLine()).thenReturn(statusLine);
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Mockito.when(response.getEntity()).thenReturn(responseEntity);

        String result = this.service.forCloudFunction(this.getTestIdentity(), "https://localhost/function/name");
        Assertions.assertEquals(token, result);
    }

    private String getTestIdentity() {
        return new String(IoTools.readFile("src/test/resources/junit_identity.json"), StandardCharsets.UTF_8);
    }
}
