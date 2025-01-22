package friend;

import com.example.demo.user.User;
import com.example.demo.friend.FriendRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests voor de FriendRequest-klasse.
 * Deze tests controleren de aanmaak, wijzigingen en validatie van
 * FriendRequest-objecten.
 */
class FriendRequestTest {

    private User sender; // Verzender van de vriendverzoek
    private User receiver; // Ontvanger van de vriendverzoek
    private FriendRequest friendRequest; // Het te testen FriendRequest-object

    @BeforeEach
    void setUp() {
        // Stel dummy User-objecten in voor testdoeleinden
        sender = new User();
        sender.setId(1); // Geef de verzender een ID
        sender.setName("Sender"); // Geef de verzender een naam

        receiver = new User();
        receiver.setId(2); // Geef de ontvanger een ID
        receiver.setName("Receiver"); // Geef de ontvanger een naam

        // Initialiseer een FriendRequest-object met status PENDING
        friendRequest = FriendRequest.builder()
                .id(100) // Stel een ID in voor de vriendverzoek
                .sender(sender) // Stel de verzender in
                .receiver(receiver) // Stel de ontvanger in
                .status(FriendRequest.Status.PENDING) // Stel de status in op PENDING
                .build();
    }

    @Test
    void testFriendRequestInitialization() {
        // Test of het FriendRequest-object correct is geïnitialiseerd

        // Controleer of de verzender correct is ingesteld
        assertEquals(sender, friendRequest.getSender(), "De verzender moet overeenkomen met de opgegeven gebruiker.");

        // Controleer of de ontvanger correct is ingesteld
        assertEquals(receiver, friendRequest.getReceiver(),
                "De ontvanger moet overeenkomen met de opgegeven gebruiker.");

        // Controleer of de status standaard is ingesteld op PENDING
        assertEquals(FriendRequest.Status.PENDING, friendRequest.getStatus(),
                "De status moet standaard PENDING zijn.");
    }

    @Test
    void testChangeStatusToAccepted() {
        // Wijzig de status naar ACCEPTED
        friendRequest.setStatus(FriendRequest.Status.ACCEPTED);

        // Controleer of de status correct is gewijzigd naar ACCEPTED
        assertEquals(FriendRequest.Status.ACCEPTED, friendRequest.getStatus(),
                "De status moet zijn gewijzigd naar ACCEPTED.");
    }

    @Test
    void testChangeStatusToRejected() {
        // Wijzig de status naar REJECTED
        friendRequest.setStatus(FriendRequest.Status.REJECTED);

        // Controleer of de status correct is gewijzigd naar REJECTED
        assertEquals(FriendRequest.Status.REJECTED, friendRequest.getStatus(),
                "De status moet zijn gewijzigd naar REJECTED.");
    }

    @Test
    void testInvalidSenderOrReceiver() {
        // Test voor een ongeldige verzender
        friendRequest.setSender(null); // Stel de verzender in op null

        // Controleer of er een NullPointerException wordt gegenereerd
        assertThrows(NullPointerException.class, () -> {
            friendRequest.getSender().getId(); // Probeer de ID van een null-verzender op te halen
        }, "De verzender mag niet null zijn.");

        // Test voor een ongeldige ontvanger
        friendRequest.setReceiver(null); // Stel de ontvanger in op null

        // Controleer of er een NullPointerException wordt gegenereerd
        assertThrows(NullPointerException.class, () -> {
            friendRequest.getReceiver().getId(); // Probeer de ID van een null-ontvanger op te halen
        }, "De ontvanger mag niet null zijn.");
    }

    @Test
    void testEqualityAndHashCode() {
        // Maak een tweede FriendRequest-object met dezelfde attributen
        FriendRequest anotherRequest = FriendRequest.builder()
                .id(100) // Zelfde ID als het eerste object
                .sender(sender) // Zelfde verzender
                .receiver(receiver) // Zelfde ontvanger
                .status(FriendRequest.Status.PENDING) // Zelfde status
                .build();

        // Controleer of de twee FriendRequest-objecten als gelijk worden beschouwd
        assertEquals(friendRequest, anotherRequest,
                "FriendRequests met dezelfde attributen moeten gelijk zijn.");

        // Controleer of de hashcodes van de twee objecten hetzelfde zijn
        assertEquals(friendRequest.hashCode(), anotherRequest.hashCode(),
                "FriendRequests met dezelfde attributen moeten dezelfde hashcode hebben.");
    }
}
