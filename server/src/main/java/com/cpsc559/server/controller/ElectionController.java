package com.cpsc559.server.controller;

import com.cpsc559.server.message.ElectionMessage;
import com.cpsc559.server.message.LeaderMessage;
import com.cpsc559.server.message.BullyMessage;
import com.cpsc559.server.service.ElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ElectionController {

    @Autowired
    private ElectionService electionService;

    // Case: received message is election message
    @PostMapping("/election")
    public ResponseEntity<?> handleElection(@RequestBody ElectionMessage message) {
		
		BullyMessage bullyMessage = electionService.onElectionMessage(message);
	
		// The server has to send back a 204 but the client can wait for a 200 response using time out
		if (bullyMessage != null)
			return ResponseEntity.ok(bullyMessage);
		else
			return ResponseEntity.noContent().build();
    }

    // Case: received message is leader message
    @PostMapping("/leader")
    public void handleLeader(@RequestBody LeaderMessage message) {
        electionService.onLeaderMessage(message);
    }
}
