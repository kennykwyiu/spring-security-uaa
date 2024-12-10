package com.kenny.uaa.util;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class TotpUtil {
    private static final long TIME_STEP = 60 * 5L;
    private static final int PASSWORD_LENGTH = 6;
    private KeyGenerator keyGenerator;
    private TimeBasedOneTimePasswordGenerator totp;

    /*
     * Initialization block, supported starting from Java 8. The execution of this initialization block occurs before the constructor.
     * More precisely, the Java compiler copies the block to the very beginning of the constructor.
     */
    {
        try {
            totp = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(TIME_STEP), PASSWORD_LENGTH);
            keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
            // SHA-1 and SHA-256 require a 64-byte (512-bit) key;
            // SHA-512 requires a 128-byte (1024-bit) key
            keyGenerator.init(512);
        } catch (NoSuchAlgorithmException e) {
            log.error("Algorithm not found: {}", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    public String createTotp(Key key, Instant time) throws InvalidKeyException {
        int password = totp.generateOneTimePassword(key, time);
        String format = "%0" + PASSWORD_LENGTH + "d";
        return String.format(format, password);
    }

    public Optional<String> createTotp(String strKey) {
        try {
            return Optional.of(createTotp(decodeKeyFromString(strKey), Instant.now()));
        } catch (InvalidKeyException e) {
            return Optional.empty();
        }
    }
    public Key decodeKeyFromString(String strKey) {
        return new SecretKeySpec(Base64.getDecoder().decode(strKey), totp.getAlgorithm());
    }

    public boolean verifyTotp(Key key, String code) throws InvalidKeyException {
        Instant now = Instant.now();
        return code.equals(createTotp(key, now));
    }


}
