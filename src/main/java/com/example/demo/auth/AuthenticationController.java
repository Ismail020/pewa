package com.example.demo.auth;

import com.example.demo.service.JwtService;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> sendPasswordResetLink(@RequestBody PasswordResetRequest request) {
        String email = request.getEmail();
        System.out.println(email);
        boolean userExists = service.checkIfEmailExists(email);
        if (userExists) {
            service.sendResetLink(email);
            return ResponseEntity.ok(Map.of("message", "A password reset link has been sent to your email."));
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "No registered user is associated with this email. Please try another email address"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody SetNewPasswordRequest request) {
        String password = passwordEncoder.encode(request.getPassword());


        String requestToken = request.getToken();
        String email = jwtService.extractUsername(requestToken);
        User user = userRepository.findUserByEmail(email).orElse(null);
        if (user == null) {
            // not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "User does not exist"));
        }
        if (!jwtService.isTokenValid(requestToken, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Invalid or expired token"));
        }
        user.setPassword(password);
        userRepository.saveAndFlush(user);
        System.out.println("The username is decoded: " + email + ". New password is received: " + password);


        return ResponseEntity.ok(Map.of("message", "New Password and token have been received"));
    }
}
