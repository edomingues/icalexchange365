package com.edomingues.icalexchange365.model;

import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.time.Instant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccessTokenTest {

    private AccessToken accessToken;

    @Before
    public void setUp() {
    }

    @Test
    public void testIsExpiredTrue() {
        this.accessToken = new AccessToken("", "",Date.from(Instant.now().minusSeconds(1)),"");

        assertTrue(accessToken.isExpired());
    }

    @Test
    public void testIsExpiredFalse() {
        this.accessToken = new AccessToken("", "",Date.from(Instant.now().plusSeconds(1)),"");

        assertFalse(accessToken.isExpired());
    }

}