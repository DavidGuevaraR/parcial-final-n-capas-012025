package com.uca.parcialfinalncapas.controller;

import com.uca.parcialfinalncapas.dto.request.AuthRequest;
import com.uca.parcialfinalncapas.dto.response.AuthResponse;
import com.uca.parcialfinalncapas.service.UserService;
import com.uca.parcialfinalncapas.utils.JwtUtils;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private UserService userService;
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/test-token")
    public ResponseEntity<?> testToken(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        String email = jwtUtils.extractClaim(token, Claims::getSubject);
        return ResponseEntity.ok("Token válido. Email extraído: " + email);
    }

}
