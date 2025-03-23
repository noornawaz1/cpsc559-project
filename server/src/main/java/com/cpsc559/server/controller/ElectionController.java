package com.cpsc559.server.controller;

import com.cpsc559.server.message.ElectionMessage;
import com.cpsc559.server.message.LeaderMessage;
import com.cpsc559.server.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ElectionController {

    @Autowired
    private ElectionService electionService;

    // Case: received message is election message
    @PostMapping("/election")
    public void handleElection(@RequestBody ElectionMessage message) {
        electionService.onElectionMessage(message);
    }

    // Case: received message is leader message
    @PostMapping("/leader")
    public void handleLeader(@RequestBody LeaderMessage message) {
        electionService.onLeaderMessage(message);
    }
}
