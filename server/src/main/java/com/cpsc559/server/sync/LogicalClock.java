package com.cpsc559.server.sync;

import org.springframework.stereotype.Component;

@Component
public class LogicalClock {
    private static int timestamp = 0;

    public static synchronized int getTimestamp() {
        return timestamp;
    }

    public static synchronized void incrementTimestamp() {
        timestamp++;
    }

    public static synchronized int getAndIncrementTimestamp() {
        return ++timestamp;
    }
}
