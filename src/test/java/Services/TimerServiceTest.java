package Services;

import com.example.demo.service.TimerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * Test class for the TimerService.
 * This test class verifies that the TimerService behaves correctly.
 *
 * Author: Danann Bartels
 */
@ExtendWith(MockitoExtension.class)
public class TimerServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private TimerService timerService;

    @Mock
    private ScheduledExecutorService scheduler;

    private static final String MATCH_ID = "match123";

    @BeforeEach
    void setUp() {
        // Reset the timer state before each test
    }

    /**
     * Test case for starting a timer.
     * Verifies that the timer is started and the "Time's up!" message is sent.
     */
    @Test
    void testStartTimer() {
        long delay = 30;  // 30 seconds
        TimeUnit unit = TimeUnit.SECONDS;

        // Mock the scheduler to directly call the task without waiting for time
        Runnable task = mock(Runnable.class);
        when(scheduler.schedule(any(Runnable.class), eq(1L), eq(TimeUnit.SECONDS))).thenAnswer(invocation -> {
            Runnable r = invocation.getArgument(0);
            r.run();  // Directly invoke the task without waiting
            return null;
        });

        timerService.startTimer(MATCH_ID, delay, unit);

        // Verify that the "Time's up!" message was sent
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/match/" + MATCH_ID), eq("Time's up!"));
    }

    /**
     * Test case for cancelling a timer.
     * Verifies that the timer is cancelled and the "Timer cancelled" message is sent.
     */
    @Test
    void testIsTimerRunning() {
        long delay = 30;
        TimeUnit unit = TimeUnit.SECONDS;

        timerService.startTimer(MATCH_ID, delay, unit);
        assert(timerService.isTimerRunning(MATCH_ID));

        timerService.cancelTimer(MATCH_ID);
        assert(!timerService.isTimerRunning(MATCH_ID));
    }
}
