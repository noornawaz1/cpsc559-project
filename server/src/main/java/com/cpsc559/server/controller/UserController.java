package com.cpsc559.server.controller;

import com.cpsc559.server.model.User;
import com.cpsc559.server.repository.UserRepository;
import com.cpsc559.server.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
  @Autowired
  private UserRepository userRepository;

  // GET /api/user/{id} - user
  @GetMapping("/{id}")
  public User getUser(@PathVariable Long id) {
      return userRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("User not found"));
  }

}