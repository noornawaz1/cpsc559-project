package com.cpsc559.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReplicationService {
    @Value("${server.urls:}")
    private String[] backupUrls;

    @Value("${server.url:}")
    private String[] currentURL;

    private final WebClient webClient = WebClient.builder().build();

    public void replicate(String operation, String apiPath, Object body, HttpHeaders headers) {

        Flux.fromArray(backupUrls)
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .flatMap(backupUrl -> {
                String requestUrl = backupUrl.trim() + apiPath;
                System.out.println("Sending replication request to: " + requestUrl);
                return webClient.method(HttpMethod.valueOf(operation))
                        .uri(requestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(httpHeaders -> httpHeaders.addAll(headers))
                        .body(body == null ? BodyInserters.empty() : BodyInserters.fromValue(body))
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnSuccess(ack -> 
                            System.out.println("Received ACK from " + requestUrl)
                        )
                        .doOnError(err -> 
                            System.err.println("Error replicating to " + requestUrl + ": " + err.getMessage())
                        );
            })
            .sequential()
            .collectList()
            .block(); // Wait until all backup requests complete
    }
}