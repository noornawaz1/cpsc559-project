package com.cpsc559.server.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Getter
@Setter
@AllArgsConstructor
public class UpdateMessage implements Comparable<UpdateMessage> {
    private int timestamp;
    private ContentCachingRequestWrapper request;

    @Override
    public int compareTo(UpdateMessage updateMessage) {
        return Integer.compare(timestamp, updateMessage.timestamp);
    }
}
