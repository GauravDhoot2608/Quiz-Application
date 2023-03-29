package com.quizapp.controllers;

import com.quizapp.entities.User;
import com.quizapp.payloads.LoginRequest;
import com.quizapp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<User> createUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(this.userService.createUser(loginRequest.getEmail(),loginRequest.getPassword()));
    }

    @GetMapping("/login")
    public ResponseEntity<User> getUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(this.userService.getUser(loginRequest.getEmail(),loginRequest.getPassword()));
    }
}
