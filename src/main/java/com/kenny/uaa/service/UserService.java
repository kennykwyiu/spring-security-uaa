package com.kenny.uaa.service;

import com.kenny.uaa.domain.Auth;
import com.kenny.uaa.domain.User;
import com.kenny.uaa.repository.RoleRepo;
import com.kenny.uaa.repository.UserRepo;
import com.kenny.uaa.util.Constants;
import com.kenny.uaa.util.JwtUtil;
import com.kenny.uaa.util.TotpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TotpUtil totpUtil;

    public Auth login(String username, String password) throws AuthenticationException {
        return userRepo.findOptionalByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> new Auth(
                        jwtUtil.createAccessToken(user),
                        jwtUtil.createRefreshToken(user)
                ))
                .orElseThrow(() -> new BadCredentialsException("Username or password incorrect"));
    }

    public boolean isUsernameExisted(String username) {
        return userRepo.countByUsername(username) > 0;
    }

    public boolean isEmailExisted(String email) {
        return userRepo.countByEmail(email) > 0;
    }

    public boolean isMobileExisted(String mobile) {
        return userRepo.countByMobile(mobile) > 0;
    }

    public boolean isValidUser(Authentication authentication, String username) {
        return authentication.getName().equals(username);
    }

    @Transactional
    public User register(User user) {
        return roleRepo.findOptionalByAuthority(Constants.ROLE_USER)
                .map(role -> {
                    User userToSave = user.withRoles(Set.of(role))
                            .withPassword(passwordEncoder.encode(user.getPassword()))
                            .withMfaKey(totpUtil.encodeKeyToString());
                    return userRepo.save(userToSave);
                })
                .orElseThrow();
    }

    public Optional<User> findOptionalByUsernameAndPassword(String username, String password) {
        return userRepo.findOptionalByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    public UserDetails updatePassword(User user, String newPassword) {
        return userRepo.findOptionalByUsername(user.getUsername())
                .map(u -> (UserDetails) userRepo.save(u.withPassword(newPassword)))
                .orElse(user);
    }

    public Optional<String> createTotp(String key) {
        return totpUtil.createTotp(key);
    }

    public Optional<User> findOptionalByEmail(String email) {
        return userRepo.findOptionalByEmail(email);
    }

    @Transactional
    @PreAuthorize("authentication.name == #user.username or " +
            "hasAnyAuthority('" + Constants.ROLE_ADMIN + "' , '" + Constants.AUTHORITY_USER_UPDATE +
            "')")
    public User saveUser(User user) {
        return userRepo.save(user);
    }
}
