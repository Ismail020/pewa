package com.example.demo.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

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

    @PostMapping("/password-reset")
    public ResponseEntity<String> sendPasswordResetLink(@RequestBody PasswordResetRequest request) {
        String email = request.getEmail();
        System.out.println(email);
        boolean userExists = service.checkIfEmailExists(email);
        if (userExists) {
            service.sendResetLink(email);
            return ResponseEntity.ok("Password reset link sent to you email.");
        } else {
            return ResponseEntity.status(404).body("No registered user is associated with this email");
        }
    }
}
