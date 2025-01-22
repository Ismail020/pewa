package friend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.example.demo.friend.FriendRequestController;
import com.example.demo.friend.FriendRequestService;
import com.example.demo.friend.FriendRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests voor de FriendRequestController.
 * Deze tests valideren de logica en interacties van de controller.
 */
class FriendRequestControllerTest {

    // Mock de service die door de controller wordt gebruikt
    @Mock
    private FriendRequestService friendRequestService;

    // Inject de mocks in de controller
    @InjectMocks
    private FriendRequestController friendRequestController;

    @BeforeEach
    void setUp() {
        // Initialiseer Mockito-annotaties
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendFriendRequest() {
        // Arrange: Stel dummy e-mailadressen in
        String senderEmail = "sender@example.com";
        String receiverEmail = "receiver@example.com";

        // Act: Roep de methode aan
        friendRequestController.sendFriendRequest(senderEmail, receiverEmail);

        // Assert: Controleer of de service correct is aangeroepen
        verify(friendRequestService, times(1)).sendFriendRequest(senderEmail, receiverEmail);
    }

    @Test
    void testAcceptFriendRequest() {
        // Arrange: Stel een mock request ID in
        Integer requestId = 1;

        // Act: Roep de methode aan
        friendRequestController.acceptFriendRequest(requestId);

        // Assert: Controleer of de service correct is aangeroepen
        verify(friendRequestService, times(1)).acceptFriendRequest(requestId);
    }

    @Test
    void testRejectFriendRequest() {
        // Arrange: Stel een mock request ID in
        Integer requestId = 2;

        // Act: Roep de methode aan
        friendRequestController.rejectFriendRequest(requestId);

        // Assert: Controleer of de service correct is aangeroepen
        verify(friendRequestService, times(1)).rejectFriendRequest(requestId);
    }

    @Test
    void testGetPendingRequests() {
        // Arrange: Stel een mock user-email en een lijst van friend requests in
        String userEmail = "user@example.com";
        FriendRequest request1 = FriendRequest.builder().id(1).build();
        FriendRequest request2 = FriendRequest.builder().id(2).build();
        List<FriendRequest> mockRequests = Arrays.asList(request1, request2);

        // Stel de mock-gedrag in
        when(friendRequestService.getPendingRequestsForUser(userEmail)).thenReturn(mockRequests);

        // Act: Roep de methode aan
        List<FriendRequest> result = friendRequestController.getPendingRequests(userEmail);

        // Assert: Controleer of het resultaat correct is
        assertEquals(2, result.size(), "De lijst met verzoeken moet 2 elementen bevatten.");
        assertEquals(mockRequests, result, "De geretourneerde lijst moet overeenkomen met de mock-lijst.");

        // Controleer of de service correct is aangeroepen
        verify(friendRequestService, times(1)).getPendingRequestsForUser(userEmail);
    }
}
