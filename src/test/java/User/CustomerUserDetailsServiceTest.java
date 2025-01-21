package User;

import com.example.demo.user.CustomUserDetailsService;
import com.example.demo.user.Role;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the CustomUserDetailsService.
 * This test class verifies that the CustomUserDetailsService behaves correctly.
 *
 * Author: Danann Bartels
 */
@ExtendWith(MockitoExtension.class)
public class CustomerUserDetailsServiceTest {

    // Mock the UserRepository to avoid interacting with a real database
    @Mock
    private UserRepository userRepository;

    // Inject the mocked dependencies into the CustomUserDetailsService
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    /**
     * This method runs before each test case.
     * It initializes the User object that will be used for testing.
     */
    @BeforeEach
    void setUp() {
        // Initialize the user object with test data
        user = new User();
        user.setName("testuser@example.com");
        user.setPassword("password123");
        user.setRole(Role.USER);
    }

    /**
     * Test case for loading a user successfully by username.
     * Verifies that the user details are returned correctly and the repository was called once.
     */
    @Test
    void testLoadUserByUsername_success() {
        // Mock the repository to return a user when the email is searched
        when(userRepository.findUserByEmail("testuser@example.com"))
                .thenReturn(java.util.Optional.of(user)); // Return the mock user wrapped in Optional

        // Call the method to load user by username
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser@example.com");

        // Verify that the user details returned are correct
        assertNotNull(userDetails);
        assertEquals("testuser@example.com", userDetails.getUsername(), "The username should match the expected email.");
        assertEquals("password123", userDetails.getPassword(), "The password should match the expected password.");
        assertEquals("ROLE_USER", userDetails.getAuthorities().toArray()[0].toString(), "The role should match the expected role.");

        // Verify that the repository method was called once with the correct email
        verify(userRepository, times(1)).findUserByEmail("testuser@example.com");
    }

    /**
     * Test case for when a user is not found by username.
     * Verifies that a UsernameNotFoundException is thrown.
     */
    @Test
    void testLoadUserByUsername_userNotFound() {
        // Mock the repository to return an empty Optional when the email is searched
        long callCount = Mockito.mockingDetails(userRepository).getInvocations().stream()
                .filter(invocation -> invocation.getMethod().getName().equals("findUserByEmail"))
                .count();

        assertEquals(1, callCount, "Expected findUserByEmail to be called once with the email 'nonexistent@example.com'.");

        // Verify that UsernameNotFoundException is thrown
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent@example.com");
        }, "Expected UsernameNotFoundException when the user is not found by email.");

        // Verify that the repository method was called once with the correct email
        assertEquals(1, Mockito.mockingDetails(userRepository).getInvocations().stream()
                .filter(invocation -> invocation.getMethod().getName().equals("findUserByEmail"))
                .count(), "Expected findUserByEmail to be called once with the email 'testuser@example.com'.");
    }
}
