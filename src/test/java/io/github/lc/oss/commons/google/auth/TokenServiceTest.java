package io.github.lc.oss.commons.google.auth;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import io.github.lc.oss.commons.testing.AbstractMockTest;
import io.github.lc.oss.commons.util.IoTools;

@SuppressWarnings("unchecked")
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
            Mockito.when(this.service.getClient().execute( //
                    ArgumentMatchers.any(HttpUriRequestBase.class), //
                    ArgumentMatchers.any(HttpClientResponseHandler.class))). //
                    thenThrow(new IOException());
        } catch (IOException e) {
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
        try {
            Mockito.when(this.service.getClient().execute( //
                    ArgumentMatchers.any(HttpUriRequestBase.class), //
                    ArgumentMatchers.any(HttpClientResponseHandler.class))). //
                    thenReturn(null);
        } catch (IOException ex) {
            Assertions.fail("Unexpected exception");
        }

        String result = this.service.forCloudFunction(this.getTestIdentity(), "https://localhost/function/name");
        Assertions.assertNull(result);
    }

    @Test
    public void test_forCloudFunction() {
        final String token = "token.jwt.sig";

        try {
            Mockito.when(this.service.getClient().execute( //
                    ArgumentMatchers.any(HttpUriRequestBase.class), //
                    ArgumentMatchers.any(HttpClientResponseHandler.class))). //
                    thenReturn(token);

        } catch (IOException ex) {
            Assertions.fail("Unexpected exception");
        }

        String result = this.service.forCloudFunction(this.getTestIdentity(), "https://localhost/function/name");
        Assertions.assertEquals(token, result);
    }

    @Test
    public void test_forCloudFunction_statusError() {
        final String json = "{\"error\":\"message\"}";

        ClassicHttpResponse response = Mockito.mock(ClassicHttpResponse.class);
        HttpEntity responseEntity = Mockito.mock(HttpEntity.class);

        Mockito.when(response.getCode()).thenReturn(422);
        Mockito.when(response.getEntity()).thenReturn(responseEntity);
        InputStream responseStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        try {
            Mockito.when(responseEntity.getContent()).thenReturn(responseStream);
        } catch (UnsupportedOperationException | IOException e) {
            Assertions.fail("Unexpected exception");
        }

        try {
            TokenService.RESPONSE_HANDLER.handleResponse(response);
            Assertions.fail("Expected exception");
        } catch (HttpException | IOException ex) {
            Assertions.fail("Unexpected exception");
        } catch (RuntimeException ex) {
            Assertions.assertEquals("Error getting token: " + json, ex.getMessage());
        }
    }

    @Test
    public void test_parseError() {
        HttpEntity responseEntity = Mockito.mock(HttpEntity.class);

        final IOException cause = new IOException("Boom!");

        try {
            Mockito.when(responseEntity.getContent()).thenThrow(cause);
        } catch (UnsupportedOperationException | IOException e) {
            Assertions.fail("Unexpected exception");
        }

        try {
            ((AbstractHttpClientResponseHandler<String>) TokenService.RESPONSE_HANDLER).handleEntity(responseEntity);
            Assertions.fail("Expected exception");
        } catch (IOException ex) {
            Assertions.assertSame(cause, ex.getCause());
        }
    }

    @Test
    public void test_parse_valid() {
        ClassicHttpResponse response = Mockito.mock(ClassicHttpResponse.class);
        HttpEntity responseEntity = Mockito.mock(HttpEntity.class);

        Mockito.when(response.getCode()).thenReturn(200);
        Mockito.when(response.getEntity()).thenReturn(responseEntity);

        try {
            String result = TokenService.RESPONSE_HANDLER.handleResponse(response);

            Assertions.assertNull(result);
        } catch (HttpException | IOException e) {
            Assertions.fail("Unexpected exception");
        }
    }

    private String getTestIdentity() {
        return new String(IoTools.readFile("src/test/resources/junit_identity.json"), StandardCharsets.UTF_8);
    }
}
