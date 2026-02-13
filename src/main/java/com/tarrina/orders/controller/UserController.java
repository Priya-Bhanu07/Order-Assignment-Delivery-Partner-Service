package com.tarrina.orders.controller;

import com.tarrina.orders.entity.User;
import com.tarrina.orders.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // POST /api/v1/users
    @PostMapping
    public User create(@RequestBody User user) {

        user.setUuid(UUID.randomUUID());
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // GET /api/v1/users/{id}
    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
