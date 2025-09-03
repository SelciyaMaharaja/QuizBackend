package com.example.quizonline.controller;

import com.example.quizonline.exception.UserAlreadyExistsException;
import com.example.quizonline.model.User;
import com.example.quizonline.request.LoginRequest;
import com.example.quizonline.response.JwtResponse;
import com.example.quizonline.security.jwt.JwtUtils;
import com.example.quizonline.security.user.QuizUserDetails;
import com.example.quizonline.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(IUserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid user data");
        }
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("Registration successful!");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body("Invalid login request");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            QuizUserDetails userDetails = (QuizUserDetails) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtTokenForUser(authentication);

            JwtResponse response = new JwtResponse(
                    userDetails.getId(),
                    userDetails.getEmail(),
                    jwt,
                    userDetails.getAuthorities()
                            .stream()
                            .map(auth -> auth.getAuthority())
                            .collect(Collectors.toList())
            );

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
