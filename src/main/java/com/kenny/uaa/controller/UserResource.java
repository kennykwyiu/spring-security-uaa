package com.kenny.uaa.controller;

import com.kenny.uaa.domain.User;
import com.kenny.uaa.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import java.security.Principal;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserResource {
    private final UserService userService;
    @GetMapping("/greeting")
    public String greeting() {
        return "Hello World";
    }

//    @PostMapping("/greeting")
//    public String makeGreeting(@RequestParam String name) {
//        return "Hello " + name;
//    }

    @PutMapping ("/greeting/{name}")
    public String putGreeting(@PathVariable String name) {
        return "Hello " + name;
    }

    @PostMapping ("/greeting")
    @ResponseStatus(HttpStatus.CREATED)
    public String makeGreeting(@RequestParam String name,
                               @RequestBody Profile profile) {
        return "Hello " + name + "!\n" + profile.getGender();
    }

    @GetMapping("/principal")
    public Principal getPrincipal(Principal principal) {
        return principal;
    }
//    public Authentication getPrincipal() {
//        return SecurityContextHolder.getContext().getAuthentication();
//    }

    @GetMapping("/users/{username}")
    public String getCurrentUsername(@PathVariable String username) {
        return "hello, " + username;
    }

//    @PreAuthorize("hasRole('USER')")
    @PostAuthorize("authentication.name.equals(returnObject.username)")
    @GetMapping("/users/by-email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.findOptionalByEmail(email)
                .orElseThrow(() -> new ResourceAccessException("Cannot find by email"));
    }

    @Data
    static class Profile {
        private String gender;
        private String idNo;
    }
}
