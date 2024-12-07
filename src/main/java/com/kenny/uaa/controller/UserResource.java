package com.kenny.uaa.controller;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class UserResource {
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


    @Data
    static class Profile {
        private String gender;
        private String idNo;
    }
}
