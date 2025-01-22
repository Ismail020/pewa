package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TimerService {
    /**
     * This service is responsible for managing timers for matches.
     * It uses a ScheduledExecutorService to schedule tasks that send countdown messages to the clients.
     * The timers are stored in a ConcurrentHashMap with the match ID as the key.
     *
     * Author: Danann Bartels
     */
    private final ConcurrentHashMap<String, Runnable> matchTimers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Constructor for the TimerService.
     * It injects the SimpMessagingTemplate to send messages to the clients.
     *
     * @param messagingTemplate The messaging template to send messages to clients
     */
    @Autowired
    public TimerService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Starts a timer for the given match ID with the specified delay.
     * The timer sends countdown messages to the clients every second until the time is up.
     *
     * @param matchId The ID of the match
     * @param delay   The delay in seconds before the timer expires
     * @param unit    The time unit for the delay
     */
    public void startTimer(String matchId, long delay, TimeUnit unit) {
        Runnable task = new Runnable() {
            private long remainingTime = unit.toSeconds(delay);

            @Override
            public void run() {
                if (remainingTime > 0) {
                    messagingTemplate.convertAndSend("/topic/match/" + matchId, remainingTime);
                    remainingTime--;
                    scheduler.schedule(this, 1, TimeUnit.SECONDS);
                } else {
                    matchTimers.remove(matchId);
                    // Notify players about match end
                    messagingTemplate.convertAndSend("/topic/match/" + matchId, "Time's up!");
                }
            }
        };

        matchTimers.put(matchId, task);
        scheduler.schedule(task, 1, TimeUnit.SECONDS);
    }

    /**
     * Cancels the timer for the given match ID.
     *
     * @param matchId The ID of the match
     */
    public void cancelTimer(String matchId) {
        matchTimers.remove(matchId);
    }

    /**
     * Checks if a timer is running for the given match ID.
     *
     * @param matchId The ID of the match
     * @return true if a timer is running, false otherwise
     */
    public boolean isTimerRunning(String matchId) {
        return matchTimers.containsKey(matchId);
    }
}
