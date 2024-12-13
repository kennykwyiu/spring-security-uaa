package com.kenny.uaa.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CryptoUtil {
    public String randomAlphanumeric(int targetStringLength) {
        int leftLimit = 48; // '0'
        int rightLimit = 122; // 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)) // Filtering out characters between Unicode 65 and 90
                .limit(targetStringLength)
                .collect(() -> new StringBuilder(),
                        (stringBuilder, codePoint) -> stringBuilder.appendCodePoint(codePoint),
                        (stringBuilder1, s) -> stringBuilder1.append(s))
                .toString();
    }
}
