package com.example.demo.auth;

import com.example.demo.service.JwtService;
import com.example.demo.user.Role;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final JavaMailSender mailSender;


        public AuthenticationResponse register(RegisterRequest request) {
                var userCheck = repository.findUserByEmail(request.getEmail());

                if (userCheck.isPresent()) {
                        throw new IllegalStateException("Email is already taken");
                }

                var user = User.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .location(request.getLocation())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.USER)
                                .profilePicture(request.getAvatar())
                                .build();

                repository.save(user);
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .build();
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                var user = repository.findUserByEmail(request.getEmail())
                                .orElseThrow();

                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .build();
        }

        public boolean checkIfEmailExists(String email) {
                return repository.findUserByEmail(email).isPresent();
        }

        public void sendResetLink(String email) {
                String resetToken = jwtService.generatePasswordResetToken(email);

                String resetLink = "http://localhost:5173/reset-password?token="+resetToken;

//                Optional<User> user = repository.findUserByEmail(email);
//                Optional<String>username = user.getName(user);
//

                SimpleMailMessage message = new SimpleMailMessage();

                message.setFrom("projects.smtp@gmail.com");
                message.setTo(email);
                message.setSubject("Password reset link");
                message.setText("Dear user, \nClick on the link below to reset your password to Zeeslag. \n\n"
                        + resetLink + " \n If you did not request password reset, please ignore this message.");
                mailSender.send(message);

        }
}
