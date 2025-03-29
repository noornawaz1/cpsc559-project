package com.cpsc559.server;

import org.springframework.stereotype.Component;

@Component
public class LogicalClock {
    private int timestamp = 0;

    public synchronized int getTimestamp() {
        return timestamp;
    }

    public synchronized void incrementTimestamp() {
        timestamp++;
    }

    public synchronized int getAndIncrementTimestamp() {
        return timestamp++;
    }
}
