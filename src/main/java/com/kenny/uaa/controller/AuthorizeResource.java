package com.kenny.uaa.controller;

import com.kenny.uaa.domain.Auth;
import com.kenny.uaa.domain.User;
import com.kenny.uaa.domain.dto.LoginDto;
import com.kenny.uaa.domain.dto.UserDto;
import com.kenny.uaa.exception.DuplicateProblem;
import com.kenny.uaa.service.UserService;
import com.kenny.uaa.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Key;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/authorize")
public class AuthorizeResource {

    private final UserService userService;
    private final JwtUtil jwtUtil;

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
    public Auth login(@Valid @RequestBody LoginDto loginDto) {
        return userService.login(loginDto.getUsername(),
                loginDto.getPassword());
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
