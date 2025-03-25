package com.cpsc559.server.service;

import com.cpsc559.server.model.ReplicationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReplicationService {
    @Value("${server.urls:}")
    private String[] backupUrls;

    private final WebClient webClient = WebClient.builder().build();

    public void replicate(String operation, String apiPath, Object body, HttpHeaders headers) {
        ReplicationRequest replicationRequest = new ReplicationRequest();
        replicationRequest.setOperation(operation);
        replicationRequest.setApiPath(apiPath);
        replicationRequest.setPayload(body);
        Map<String, String> headerMap = new HashMap<>();
        headers.forEach((key, values) -> headerMap.put(key, String.join(",", values)));
        replicationRequest.setHeaders(headerMap);

        // The replication endpoint is the same on all backups
        String replicationEndpoint = "/api/replication";

        Flux.fromArray(backupUrls)
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .flatMap(backupUrl -> {
                String requestUrl = backupUrl.trim() + replicationEndpoint;
                System.out.println("Sending replication request to: " + requestUrl);
                return webClient.post()
                        .uri(requestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(replicationRequest)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnSuccess(ack -> System.out.println("Received ACK from " + requestUrl + ": " + ack))
                        .doOnError(err -> System.err.println("Error replicating to " + requestUrl + ": " + err.getMessage()));
            })
            .sequential()
            .collectList()
            .block(); // Wait until all backup requests complete
    }
}