package com.kenny.uaa.util;

import com.kenny.uaa.common.BaseIntegrationTest;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
public class TotpUtilIntTests extends BaseIntegrationTest {
    @Autowired
    private TotpUtil totpUtil;

    @Test
    public void givenSameKeyAndTotp_whenValidateTwice_thenFail() throws Exception {
        val now = Instant.now();
        val validFuture = now.plus(totpUtil.getTimeStep());
        val key = totpUtil.generateKey();
        val first = totpUtil.createTotp(key, now);
        val newKey = totpUtil.generateKey();
        assertTrue(totpUtil.verifyTotp(key, first), "Validation should pass the first time");
        val second = totpUtil.createTotp(key, Instant.now());
        assertEquals(first, second, "Two TOTPs generated within the time step should be identical");
        val afterTimeStep = totpUtil.createTotp(key, validFuture);
        assertNotEquals(first, afterTimeStep, "TOTP after expiration should not match the original one");
        assertFalse(totpUtil.verifyTotp(newKey, first), "Validating the original TOTP with a new key should fail");
    }

    @Test
    public void givenKey_ThenEncodeAndDecodeSuccess() {
        val key = totpUtil.generateKey();
        val strKey = totpUtil.encodeKeyToString(key);
        val decodeKey = totpUtil.decodeKeyFromString(strKey);
        assertEquals(key, decodeKey, "Decoding the key from a string should match the original key");
    }
}
