package com.cpsc559.server.service;

import com.cpsc559.server.message.BullyMessage;
import com.cpsc559.server.message.ElectionMessage;
import com.cpsc559.server.message.LeaderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Main implementation of the bully election algorithm.
@Service
public class ElectionService {

    private static final Logger logger = LoggerFactory.getLogger(ElectionService.class);

    private final WebClient webClient;
    private final ExecutorService threadpool = Executors.newCachedThreadPool();

    @Value("${server.url}")
    private String serverUrl;

    @Value("${proxy.url}")
    private String proxyUrl;

    @Value("#{'${server.urls}'.split(',')}")
    private List<String> otherServerUrls;

    // State flags used in the election
    // The 'volatile' keyword ensures updates from one thread, are immediately visible to all other threads.
    private volatile boolean running = false;
    private volatile String leaderUrl = "http://localhost:8081";

    public ElectionService(WebClient webClient) {
        this.webClient = webClient;
    }

    // Initiates the election process
    public void initiateElection() {
        logger.info("Initiating election.");
        running = true;
        String prevLeader = leaderUrl;

        if (hasHighestId()) { // Automatically wins election
            leaderUrl = serverUrl;
            sendLeaderMessage(new LeaderMessage(serverUrl));

        } else {
            ElectionMessage electionMessage = new ElectionMessage(serverUrl);
            sendElectionMessage(electionMessage)
                    .subscribe(bullied -> {
                        if (bullied) {
                            try {
                                TimeUnit.SECONDS.sleep(5);
                            } catch (InterruptedException e) {
                                // Ignore
                            }

                            // If leader didn't change, a crash occurred and we need to restart the election
                            if (leaderUrl.equals(prevLeader)) {
                                logger.info("Leader didn't change");
                                initiateElection();
                                return;
                            }

                            running = false;
                        } else {
                            leaderUrl = serverUrl;
                            sendLeaderMessage(new LeaderMessage(serverUrl));
                        }
                    });
        }
    }

    private boolean hasHighestId() {
        return serverUrl.compareTo(otherServerUrls.get(0)) > 0
                && serverUrl.compareTo(otherServerUrls.get(1)) > 0
                && serverUrl.compareTo(otherServerUrls.get(2)) > 0;
    }

    // Case: received message is leader message
    public void onLeaderMessage(LeaderMessage message) {
        leaderUrl = message.getLeaderUrl();
        logger.info("Received message that {} is the current leader", leaderUrl);
	      running = false;
    }

    // Case: received message is election message
    public BullyMessage onElectionMessage(ElectionMessage message) {
        String senderUrl = message.getSenderUrl();
        logger.info("Received election message from {}", senderUrl);

        if (senderUrl.compareTo(serverUrl) < 0) {
            logger.info("Sending bully message to {}", senderUrl);
            BullyMessage bullyMessage = new BullyMessage(serverUrl);

            if (!running) {
                // initiate election in separate thread
                logger.info("Not currently running in election");
                threadpool.submit(this::initiateElection);
            }

            return bullyMessage;
        }

        return null;

    }

    // Broadcasts leader message to all other servers & sends update to the proxy
    private void sendLeaderMessage(LeaderMessage message) {
        logger.info("Elected as leader, sending leader messages");

        // Send a leader message to all other servers at /leader
        for (String otherServerUrl : otherServerUrls) {
            logger.info("Sending leader message to {}/api/leader", otherServerUrl);
            webClient.post()
                    .uri(otherServerUrl + "/api/leader")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL)
                    .bodyValue(message)
                    .retrieve()
                    .toBodilessEntity()
                    .subscribe();;
        }

        // Send /updatePrimary to the proxy
        logger.info("Updating proxy at {}/updatePrimary", proxyUrl);
        webClient.post()
                .uri(proxyUrl + "/updatePrimary")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.ALL)
                .bodyValue(message.getLeaderUrl())
                .retrieve()
                .toBodilessEntity()
                .subscribe();;

        running = false;
    }

    // Broadcast message to all other servers with greater URLs/ID
    // Returns true if bullied, false otherwise
    private Mono<Boolean> sendElectionMessage(ElectionMessage message) {
        return Flux.fromIterable(otherServerUrls)  // Convert list of URLs to a Flux stream
                .filter(otherServerUrl -> serverUrl.compareTo(otherServerUrl) < 0) // Only higher ID servers
                .flatMap(otherServerUrl -> {
                    logger.info("Sending election message to {}", otherServerUrl);

                    return webClient.post()
                            .uri(otherServerUrl + "/api/election")
                            .bodyValue(message)
                            .exchangeToMono(Mono::just)
                            .timeout(Duration.ofSeconds(5))
                            .onErrorReturn(ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build()) // Fallback on error
                            .map(response -> {
                                if (response.statusCode() == HttpStatus.OK) {
                                    logger.info("Bullied by {}", otherServerUrl);
                                    return true;
                                }
                                return false;
                            });
                }) // Stream of true & false values
                .filter(result -> result) // Filters out false
                .next() // Gets the first element of the stream
                .defaultIfEmpty(false); // If no trues (no bully messages), return false
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
                        logger.info("Health check failed. Running = {}", running);
                        if (!running) {
                            initiateElection();
                        }
                    })
                    .subscribe();
        }
    }

    // True if the current server is the leader
    public boolean isLeader() {
        return serverUrl.equals(leaderUrl);
    }
}
