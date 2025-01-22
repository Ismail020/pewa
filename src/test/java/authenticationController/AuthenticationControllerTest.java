package authenticationController;

/**
 * Unit tests for the AuthenticationController class.
 *
 * This test class verifies the behavior of the AuthenticationController's register and authenticate methods.
 * It uses Mockito to mock the AuthenticationService and create test requests.
 *
 * The tests ensure that:
 * - The register method calls the register method of AuthenticationService exactly once
 *   and returns the expected response.
 * - The authenticate method calls the authenticate method of AuthenticationService exactly once
 *   and returns the expected response.
 *
 * This helps to validate that the AuthenticationController interacts correctly with the
 * AuthenticationService and produces the correct output for registration and authentication requests.
 */

import com.example.demo.auth.*;
import com.example.demo.service.JwtService;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {
    private static final String EMAIL = "user@email";
    private static final String REQUEST_TOKEN = "requestToken";
    public static final String PASSWORD = "pwd";

    @Mock
    private AuthenticationService authService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder pwdEncoder;

    @Mock
    private User user;

    private AuthenticationController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AuthenticationController(authService, jwtService, userRepo, pwdEncoder);
    }

    @Test
    void testRegister() {
        RegisterRequest request = new RegisterRequest();
        AuthenticationResponse response = new AuthenticationResponse();
        when(authService.register(request)).thenReturn(response);

        ResponseEntity<AuthenticationResponse> result = controller.register(request);

        assertEquals(ResponseEntity.ok(response), result);
        verify(authService, times(1)).register(request);
    }

    @Test
    void testAuthenticate() {
        AuthenticationRequest request = new AuthenticationRequest();
        AuthenticationResponse response = new AuthenticationResponse();
        when(authService.authenticate(request)).thenReturn(response);

        ResponseEntity<AuthenticationResponse> result = controller.authenticate(request);

        assertEquals(ResponseEntity.ok(response), result);
        verify(authService, times(1)).authenticate(request);
    }

    @Test
    void resetPassword_happy_path() {
        when(pwdEncoder.encode(PASSWORD)).thenReturn("encodedPwd");
        when(jwtService.extractUsername(anyString())).thenReturn(EMAIL);
        when(userRepo.findUserByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(REQUEST_TOKEN, user)).thenReturn(true);
        SetNewPasswordRequest request = new SetNewPasswordRequest(PASSWORD, REQUEST_TOKEN);
        assertThat(controller.resetPassword(request))
                .isEqualTo(ResponseEntity
                        .ok(Map.of("message", "Your password has been successfully reset")));
        verify(user).setPassword("encodedPwd");
        verify(userRepo).saveAndFlush(user);
    }

    @Test
    void resetPassword_jwt_expired() {
        when(jwtService.extractUsername(anyString())).thenThrow(ExpiredJwtException.class);
        SetNewPasswordRequest request = new SetNewPasswordRequest(PASSWORD, REQUEST_TOKEN);
        assertThat(controller.resetPassword(request).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(user, never()).setPassword(anyString());
        verify(userRepo, never()).saveAndFlush(any());
    }

    @Test
    void resetPassword_user_not_found() {
        when(jwtService.extractUsername(anyString())).thenReturn(EMAIL);
        when(userRepo.findUserByEmail(EMAIL)).thenReturn(Optional.empty());
        SetNewPasswordRequest request = new SetNewPasswordRequest(PASSWORD, REQUEST_TOKEN);
        assertThat(controller.resetPassword(request).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        verify(user, never()).setPassword(anyString());
        verify(userRepo, never()).saveAndFlush(any());
    }
}
