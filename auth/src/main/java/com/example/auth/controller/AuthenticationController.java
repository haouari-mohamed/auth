package com.example.auth.controller;

import com.example.auth.model.LoginRequest;
import com.example.auth.model.AuthResponse;
import com.example.auth.service.CustomUserDetailsService;
import com.example.auth.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // POST: Login endpoint to authenticate and generate a JWT token
    @PostMapping("/login")
    public AuthResponse authenticate(@RequestBody LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        // Generate JWT token
        String token = jwtUtil.generateToken(userDetails.getUsername());

        return new AuthResponse(token);
    }

    // GET: Validate token
    @GetMapping("/validate")
    public String validateToken(@RequestHeader("Authorization") String token) {
        String authToken = token.substring(7); // Remove "Bearer " prefix
        if (jwtUtil.validateToken(authToken)) {
            return "Token is valid";
        } else {
            return "Token is invalid";
        }
    }
}
