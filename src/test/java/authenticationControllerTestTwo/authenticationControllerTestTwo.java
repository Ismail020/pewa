package authenticationControllerTestTwo;

import com.example.demo.auth.AuthenticationController;
import com.example.demo.auth.AuthenticationService;
import com.example.demo.auth.AuthenticationResponse;
import com.example.demo.auth.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import com.example.demo.auth.AuthenticationRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class authenticationControllerTestTwo {

    @Mock
    private AuthenticationService service;

    @InjectMocks
    private AuthenticationController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .avatar("https://example.com/avatar.jpg")
                .name("John Doe")
                .email("john.doe@example.com")
                .password("secure-password")
                .location("Amsterdam")
                .build();

        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .token("mocked-jwt-token")
                .build();

        when(service.register(registerRequest)).thenReturn(expectedResponse);

        ResponseEntity<AuthenticationResponse> response = controller.register(registerRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(service, times(1)).register(registerRequest);
    }

    @Test
    void testAuthenticate() {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("john.doe@example.com")
                .password("secure-password")
                .build();

        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .token("mocked-jwt-token")
                .build();

        when(service.authenticate(authenticationRequest)).thenReturn(expectedResponse);

        ResponseEntity<AuthenticationResponse> response = controller.authenticate(authenticationRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(service, times(1)).authenticate(authenticationRequest);
    }

    @Test
    void testDuplicateRegister() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .avatar("https://example.com/avatar.jpg")
                .name("John Doe")
                .email("john.doe@example.com")
                .password("secure-password")
                .location("Amsterdam")
                .build();

        when(service.register(registerRequest))
                .thenThrow(new IllegalStateException("Email is already taken"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            controller.register(registerRequest);
        });

        assertEquals("Email is already taken", exception.getMessage());
        verify(service, times(1)).register(registerRequest);
    }
}
