package com.github.lc.oss.commons.google.auth.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.lc.oss.commons.testing.AbstractTest;

public class GoogleIdentityTest extends AbstractTest {
    @Test
    public void test_codeCoverage() {
        GoogleIdentity gi = new GoogleIdentity();

        Assertions.assertNull(gi.getAuthProviderX509CertUrl());
        Assertions.assertNull(gi.getAuthUri());
        Assertions.assertNull(gi.getClientEmail());
        Assertions.assertNull(gi.getClientId());
        Assertions.assertNull(gi.getClientX509CertUrl());
        Assertions.assertNull(gi.getPrivateKey());
        Assertions.assertNull(gi.getPrivatekeyId());
        Assertions.assertNull(gi.getProjectId());
        Assertions.assertNull(gi.getTokenUri());
        Assertions.assertNull(gi.getType());

        gi.setAuthProviderX509CertUrl("authProviderX509CertUrl");
        gi.setAuthUri("authUri");
        gi.setClientEmail("clientEmail");
        gi.setClientId("clientId");
        gi.setClientX509CertUrl("clientX509CertUrl");
        gi.setPrivateKey("privateKey");
        gi.setPrivatekeyId("privatekeyId");
        gi.setProjectId("projectId");
        gi.setTokenUri("tokenUri");
        gi.setType("type");

        Assertions.assertEquals("authProviderX509CertUrl", gi.getAuthProviderX509CertUrl());
        Assertions.assertEquals("authUri", gi.getAuthUri());
        Assertions.assertEquals("clientEmail", gi.getClientEmail());
        Assertions.assertEquals("clientId", gi.getClientId());
        Assertions.assertEquals("clientX509CertUrl", gi.getClientX509CertUrl());
        Assertions.assertEquals("privateKey", gi.getPrivateKey());
        Assertions.assertEquals("privatekeyId", gi.getPrivatekeyId());
        Assertions.assertEquals("projectId", gi.getProjectId());
        Assertions.assertEquals("tokenUri", gi.getTokenUri());
        Assertions.assertEquals("type", gi.getType());
    }
}
