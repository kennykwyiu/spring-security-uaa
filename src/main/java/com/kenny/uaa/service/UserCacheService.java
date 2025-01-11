package com.kenny.uaa.service;

import com.kenny.uaa.domain.User;
import com.kenny.uaa.util.Constants;
import com.kenny.uaa.util.CryptoUtil;
import com.kenny.uaa.util.TotpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService {
    private final RedissonClient redisson;
    private final TotpUtil totpUtil;
    private final CryptoUtil cryptoUtil;

    public String cache(User user) {
        String mfaId = cryptoUtil.randomAlphanumeric(12);
        RMapCache<String, User> cache = redisson.getMapCache(Constants.CACHE_MFA);
        if (!cache.containsKey(mfaId)) {
            cache.put(mfaId, user, totpUtil.getTimeStepInLong(), TimeUnit.SECONDS);
        }
        return mfaId;
    }

    public Optional<User> retrieveUser(String mfaId) {
        RMapCache<String, User> cache = redisson.getMapCache(Constants.CACHE_MFA);
        if (cache.containsKey(mfaId)) {
            return Optional.of(cache.get(mfaId));
        }
        return Optional.empty();
    }

    public Optional<User> verifyTotp(String mfaId, String code) {
        RMapCache<String, User> cache = redisson.getMapCache(Constants.CACHE_MFA);
        if (!cache.containsKey(mfaId) || cache.get(mfaId) == null) {
            return Optional.empty();
        }
        User cacheUser = cache.get(mfaId);
        try {
            boolean isValid = totpUtil.verifyTotp(totpUtil.decodeKeyFromString(cacheUser.getMfaKey()), code);
            if (!isValid) {
                return Optional.empty();
            }
            cache.remove(mfaId);
            return Optional.of(cacheUser);
        } catch (InvalidKeyException e) {
            return Optional.empty();
        }
    }
}
