package com.kenny.uaa.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserResource {
    @GetMapping("/greeting")
    public String greeting() {
        return "Hello World";
    }

    @PostMapping("/greeting")
    public String makeGreeting(@RequestParam String name) {
        return "Hello " + name;
    }

    @PutMapping ("/greeting/{name}")
    public String putGreeting(@PathVariable String name) {
        return "Hello " + name;
    }
}
