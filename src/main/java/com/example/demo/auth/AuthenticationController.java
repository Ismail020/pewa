package com.example.demo.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    public ResponseEntity<Map<String, String>>  sendPasswordResetLink(@RequestBody PasswordResetRequest request) {
        String email = request.getEmail();
        System.out.println(email);
        boolean userExists = service.checkIfEmailExists(email);
        if (userExists) {
            service.sendResetLink(email);
            return ResponseEntity.ok(Map.of("message", "Een link om uw wachtwoord te herstellen is naar uw email verstuurd."));
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "Er is geen geregistreerde gebruiker gekoppeld aan dit e-mailadres. Probeer een ander e-mailadres."));
        }
    }
}
