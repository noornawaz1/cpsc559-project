package com.cpsc559.server.controller;

import com.cpsc559.server.model.ReplicationRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/replication")
public class ReplicationController {

    @PostMapping
    public ResponseEntity<String> receiveReplication(@RequestBody ReplicationRequest replicationRequest, HttpServletRequest request) {
        // Log the server's address and the replication info received
        String serverInfo = request.getLocalAddr() + ":" + request.getLocalPort();
        System.out.println("Replication request received on backup server: " + serverInfo);
        System.out.println("Operation: " + replicationRequest.getOperation());
        System.out.println("API Path: " + replicationRequest.getApiPath());
        System.out.println("Payload: " + replicationRequest.getPayload());
        System.out.println("Headers: " + replicationRequest.getHeaders());

        // For now, just return an ACK
        return ResponseEntity.ok("ACK");
    }
}