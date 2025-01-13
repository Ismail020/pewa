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

import com.example.demo.auth.AuthenticationController;
import com.example.demo.auth.AuthenticationService;
import com.example.demo.auth.AuthenticationResponse;
import com.example.demo.auth.RegisterRequest;
import com.example.demo.auth.AuthenticationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {
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
        RegisterRequest request = new RegisterRequest();
        AuthenticationResponse response = new AuthenticationResponse();
        when(service.register(request)).thenReturn(response);

        ResponseEntity<AuthenticationResponse> result = controller.register(request);

        assertEquals(ResponseEntity.ok(response), result);
        verify(service, times(1)).register(request);
    }

    @Test
    void testAuthenticate() {
        AuthenticationRequest request = new AuthenticationRequest();
        AuthenticationResponse response = new AuthenticationResponse();
        when(service.authenticate(request)).thenReturn(response);

        ResponseEntity<AuthenticationResponse> result = controller.authenticate(request);

        assertEquals(ResponseEntity.ok(response), result);
        verify(service, times(1)).authenticate(request);
    }
}
