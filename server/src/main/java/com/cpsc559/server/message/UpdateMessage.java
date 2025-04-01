package com.cpsc559.server.message;

import jakarta.servlet.AsyncContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class UpdateMessage implements Comparable<UpdateMessage> {
    private int timestamp;
    private AsyncContext asyncContext;

    @Override
    public int compareTo(UpdateMessage updateMessage) {
        return Integer.compare(timestamp, updateMessage.timestamp);
    }
}
