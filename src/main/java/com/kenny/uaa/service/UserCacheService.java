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


}
