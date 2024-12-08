package com.kenny.uaa.controller;

import com.kenny.uaa.domain.dto.UserDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
@RestController
@RequestMapping("/authorize")
public class AuthorizeResource {
    @GetMapping(value = "greeting")
    public String sayHello() {
        return "hello world";
    }

    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody UserDto userDto) {
        return userDto;
    }

    @GetMapping("/problem")
    public void raiseProblem() {
        throw new AccessDeniedException("You do not have the privilege");
    }
}
