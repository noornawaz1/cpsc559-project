package com.cpsc559.server.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ElectionMessage {
    private String senderUrl;
}
