package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TimerService {
    private final ConcurrentHashMap<String, Runnable> matchTimers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startTimer(String matchId, Runnable task, long delay, TimeUnit unit) {
        matchTimers.put(matchId, task);
        scheduler.schedule(() -> {
            task.run();
            matchTimers.remove(matchId);
        }, delay, unit);
    }

    public void cancelTimer(String matchId) {
        Runnable task = matchTimers.remove(matchId);
        if (task != null) {
            // TODO: Handle any task-specific cancellation logic if needed
        }
    }

    public boolean isTimerRunning(String matchId) {
        return matchTimers.containsKey(matchId);
    }
}
