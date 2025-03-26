package com.cpsc559.server.service;

import com.cpsc559.server.message.BullyMessage;
import com.cpsc559.server.message.ElectionMessage;
import com.cpsc559.server.message.LeaderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

// Main implementation of the bully election algorithm.
@Service
public class ElectionService {

    private static final Logger logger = LoggerFactory.getLogger(ElectionService.class);

    private final WebClient webClient;

    @Value("${server.url}")
    private String serverUrl;

    @Value("${proxy.url}")
    private String proxyUrl;

    @Value("#{'${server.urls}'.split(',')}")
    private List<String> otherServerUrls;

    // State flags used in the election
    // The 'volatile' keyword ensures updates from one thread, are immediately visible to all other threads.
    private volatile boolean running = false;
    private volatile String leaderUrl = null;

    public ElectionService(WebClient webClient) {
        this.webClient = webClient;
    }

    // Our implementation of the Initiate_Election(int i) pseudocode from class
    public void initiateElection() {
        logger.info("Initiating election.");
        running = true;

        if (hasHighestId()) { // Automatically wins election
            leaderUrl = serverUrl;
            sendLeaderMessage(new LeaderMessage(serverUrl));
        } else {
            /*
                Send election message to all other servers with higher ids at /election
                Wait for T time units
                If no response (timeout) or recieve 204 from all other servers
                    leader(i) = i
                    send leader message at /leader to every other server
                Otherwise, a bully response (OK) is received
                    Wait for T’ time units
                    If leader didn't change
                        initiateElection()
             */
        }

        /*
        initiateElection():
            running = true
            If i have the highest id:
                Send a leader message to all other servers at /leader and /updatePrimary to the proxy
            Otherwise:
                Send election message to all other servers with higher ids at /election
                Wait for T time units
                If no response (timeout) or recieve 204 from all other servers
                    leader(i) = i
                    send leader message at /leader to every other server
                Otherwise, a bully response (OK) is received
                    Wait for T’ time units
                    If leader didn't change
                        initiateElection()
         */

        // rest of implementation goes here...
    }

    private boolean hasHighestId() {
        return serverUrl.compareTo(otherServerUrls.get(0)) > 0
                && serverUrl.compareTo(otherServerUrls.get(1)) > 0
                && serverUrl.compareTo(otherServerUrls.get(2)) > 0;
    }

    // Case: received message is leader message
    public void onLeaderMessage(LeaderMessage message) {
        leaderUrl = message.getLeaderUrl();
	    running = false;
    }

    // Case: received message is election message
    public BullyMessage onElectionMessage(ElectionMessage message) {
        String senderUrl = message.getSenderUrl();

        if (senderUrl.compareTo(serverUrl) < 0) {
            BullyMessage bullyMessage = new BullyMessage();
            bullyMessage.setSenderUrl(serverUrl);

            if (!running) {
               initiateElection();
            }

            return bullyMessage;
        }

	   return null;
        
    }

    private void sendLeaderMessage(LeaderMessage message) {
        // Send a leader message to all other servers at /leader
        for (String serverUrl : otherServerUrls) {
            webClient.post()
                    .uri(serverUrl + "/election")
                    .bodyValue(message)
                    .retrieve();
        }

        // Send /updatePrimary to the proxy
        webClient.post()
                .uri(proxyUrl + "/updatePrimary")
                .bodyValue(message)
                .retrieve();
    }

    private void sendElectionMessage(ElectionMessage message) {

        // used to broadcast message to all other servers with greater URLs/ID [use String.CompareTo()]
    }

    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.SECONDS)
    public void heartbeat() {
        if (!isLeader()) {
            if (leaderUrl == null) {
                if (!running) {
                    initiateElection();
                }
                return;
            }

            // Sends /health request to the primary
            String uri = leaderUrl + "/api/health";
            logger.info("Sending health request to {}", uri);

            // Build the request object and send it
            webClient.get()
                    .uri(uri)
                    .accept(MediaType.ALL)
                    .retrieve()
                    .toEntity(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .doOnError(e -> {
                        // Server didn't respond in time - initiate the election process
                        logger.info("Health check failed.");
                        if (!running) {
                            initiateElection();
                        }
                    })
                    .subscribe();
        }
    }

    // True if the current server is the leader
    private boolean isLeader() {
        return serverUrl.equals(leaderUrl);
    }
}
