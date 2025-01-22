package MatchmakingControllerTest;

import com.example.demo.controller.MatchmakingController;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import com.example.demo.service.GameMatchmaker;
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;
        import org.mockito.Mock;
        import org.mockito.MockitoAnnotations;
        import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

        import java.security.Principal;

        import static org.mockito.Mockito.*;

class MatchmakingControllerTest {

    @Mock
    private GameMatchmaker matchmaker;

    private MatchmakingController matchmakingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        matchmakingController = new MatchmakingController(matchmaker);
    }

    @Test
    void testJoinQueue() {
        // Arrange
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("testUser");

        // Act
        matchmakingController.joinQueue(mockPrincipal, "Joining queue message");

        // Assert
        verify(matchmaker, times(1)).addPlayerToQueue("testUser");
    }

    @Test
    void testLeaveQueue() {
        // Arrange
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("testUser");

        // Act
        matchmakingController.leaveQueue(mockPrincipal);

        // Assert
        verify(matchmaker, times(1)).removePlayerFromQueue("testUser");
    }

    @Test
    void testChallengePlayer() {
        // Arrange
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("challenger");

        // Act
        matchmakingController.challengePlayer("\"challenged\"", mockPrincipal);

        // Assert
        verify(matchmaker, times(1)).handleChallenge("challenger", "challenged");
    }
}
