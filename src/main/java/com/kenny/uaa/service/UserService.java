package com.kenny.uaa.service;

import com.kenny.uaa.domain.Auth;
import com.kenny.uaa.domain.User;
import com.kenny.uaa.repository.RoleRepo;
import com.kenny.uaa.repository.UserRepo;
import com.kenny.uaa.util.Constants;
import com.kenny.uaa.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


}
