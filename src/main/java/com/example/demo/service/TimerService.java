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
    private final ConcurrentHashMap<String, Runnable> matchTimers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final SimpMessagingTemplate messagingTemplate; // Spring's WebSocket messaging template

    @Autowired
    public TimerService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

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
                }
            }
        };

        matchTimers.put(matchId, task);
        scheduler.schedule(task, 1, TimeUnit.SECONDS);
    }

    public void cancelTimer(String matchId) {
        matchTimers.remove(matchId);
    }

    public boolean isTimerRunning(String matchId) {
        return matchTimers.containsKey(matchId);
    }
}
