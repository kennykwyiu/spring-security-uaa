package com.kenny.uaa.controller;

import com.kenny.uaa.domain.Auth;
import com.kenny.uaa.domain.MfaType;
import com.kenny.uaa.domain.User;
import com.kenny.uaa.domain.dto.LoginDto;
import com.kenny.uaa.domain.dto.SendTotpDto;
import com.kenny.uaa.domain.dto.UserDto;
import com.kenny.uaa.domain.dto.VerifyTotpDto;
import com.kenny.uaa.exception.*;
import com.kenny.uaa.service.EmailService;
import com.kenny.uaa.service.SmsService;
import com.kenny.uaa.service.UserCacheService;
import com.kenny.uaa.service.UserService;
import com.kenny.uaa.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Key;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/authorize")
public class AuthorizeResource {

    private final UserService userService;
    private final UserCacheService userCacheService;
    private final JwtUtil jwtUtil;
    private final SmsService smsService;
    private final EmailService emailService;

    @GetMapping(value = "greeting")
    public String sayHello() {
        return "hello world";
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody UserDto userDto) {

        // TODO: Verify that username, email, and mobile are unique - need to query database to check for duplicates
        if (userService.isUsernameExisted(userDto.getUsername())) {
            throw new DuplicateProblem("Username already exists");
        }
        if (userService.isEmailExisted(userDto.getEmail())) {
            throw new DuplicateProblem("Email already exists");
        }

        if (userService.isMobileExisted(userDto.getMobile())) {
            throw new DuplicateProblem("Mobile already exists");
        }

        User user = User.builder()
                .username(userDto.getUsername())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .mobile(userDto.getMobile())
                .password(userDto.getPassword())
                .build();
        // TODO: Convert userDto to User entity, assign default ROLE_USER role and save to database
        userService.register(user);


    }

    @GetMapping("/problem")
    public void raiseProblem() {
        throw new AccessDeniedException("You do not have the privilege");
    }

    @PostMapping("/token")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        return userService.findOptionalByUsernameAndPassword(loginDto.getUsername(),
                        loginDto.getPassword()).map(
                        user -> {
                            //1. Upgrade password encoding
                            userService.updatePassword(user, loginDto.getPassword());
                            //2. Verification
                            if (!user.isEnabled()) {
                                throw new UserNotEnabledProblem();
                            }
                            if (!user.isAccountNonLocked()) {
                                throw new UserAccountLockedProblem();
                            }
                            if (!user.isAccountNonExpired()) {
                                throw new UserAccountExpiredProblem();
                            }
                            //3. Check if usingMfa is false, then directly return Token
                            if (!user.isUsingMfa()) {
                                return ResponseEntity.ok(userService.login(loginDto.getUsername(),
                                        loginDto.getPassword()));
                            }
                            //4. use mfa
                            String mfaId = userCacheService.cache(user);
                            //5. "X-Authenticate": "mfa", "realm=" + mfaId
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                    .header("X-Authenticate", "mfa", "realm=" + mfaId)
                                    .build();
                        }
                )
                .orElseThrow(() -> new BadCredentialsException("Username or password incorrect!"));
    }

    @PutMapping("/totp")
    public void sendTotp(@Valid @RequestBody SendTotpDto sendTotpDto) {
        userCacheService.retrieveUser(sendTotpDto.getMfaId())
                .flatMap(user -> userService.createTotp(user.getMfaKey())
                                            .map(totp -> Pair.of(user, totp))
                         )
                .ifPresentOrElse(pair -> {
                    if (sendTotpDto.getMfaType() == MfaType.SMS) {
                        smsService.send(pair.getFirst().getMobile(), pair.getSecond());
                    } else {
                        emailService.send(pair.getFirst().getEmail(), pair.getSecond());
                    }
                }, () -> {
                    throw new InvalidTotpProblem();
                });

    }

    @PostMapping("/totp")
    public Auth verifyTotp(@Valid @RequestBody VerifyTotpDto verifyTotpDto) {
        return userCacheService.verifyTotp(verifyTotpDto.getMfaId(), verifyTotpDto.getCode())
                .map(user -> userService.login(user.getUsername(), user.getPassword()))
                .orElseThrow(() -> new InvalidTotpProblem());
    }

    @PostMapping("/token/refresh")
    public Auth refreshToken(@RequestHeader(name = "Authorization") String authorization,
                             @RequestParam String refreshToken) throws AccessDeniedException {
        String PREFIX = "Bearer ";
        String accessToken = authorization.replace(PREFIX, "");
        if (jwtUtil.validateRefreshToken(refreshToken) &&
                jwtUtil.validateAccessTokenWithoutExpiration(accessToken)) {
            return new Auth(jwtUtil.createAccessTokenWithRefreshToken(refreshToken), refreshToken);
        }
        throw new AccessDeniedException("Access denied");
    }


}
