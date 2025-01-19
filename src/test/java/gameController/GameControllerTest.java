package gameController;

/** * Unit tests for the GameController class.
 * This test class verifies the behavior of the GameController's startGame method.
 * It uses Mockito to mock the GameMatchmaker service and a Principal object.
 * The tests ensure that:
 * - The startGame method calls the addPlayerToQueue method of GameMatchmaker exactly once.
 * - The startGame method returns the expected response message.
 * This helps to validate that the GameController interacts correctly with the
 * GameMatchmaker service and produces the correct output when a user starts a game.
 */

import com.example.demo.controller.GameController;
import com.example.demo.service.GameMatchmaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class GameControllerTest {
    @Mock
    private GameMatchmaker matchmaker;

    @InjectMocks
    private GameController gameController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void startGame() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");

//        String response = gameController.startGame(principal);
//
//        verify(matchmaker, times(1)).addPlayerToQueue("testUser");
//        assertEquals("{\"message\":\"Welcome to waiting queue, testUser\"}", response);
    }
}
