package com.example.springapi.controller;

import com.example.springapi.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private Map<String, User> userDatabase = new HashMap<>();

    // GET method to retrieve a user by ID
    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return userDatabase.get(id);
    }

    // POST method to create a new user
    @PostMapping
    public User createUser(@RequestBody User user) {
        userDatabase.put(user.getId(), user);
        return user;
    }
}
