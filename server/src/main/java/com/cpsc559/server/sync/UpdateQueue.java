package com.cpsc559.server.sync;

import com.cpsc559.server.message.UpdateMessage;
import org.springframework.stereotype.Component;

import java.util.PriorityQueue;

@Component
public class UpdateQueue {

    private final PriorityQueue<UpdateMessage> updateQueue = new PriorityQueue<>();

    public synchronized UpdateMessage peek() {
        return updateQueue.peek();
    }

    public synchronized void enqueue(UpdateMessage updateMessage) {
        updateQueue.add(updateMessage);
    }

    public synchronized UpdateMessage dequeue() {
        return updateQueue.poll();
    }
}
