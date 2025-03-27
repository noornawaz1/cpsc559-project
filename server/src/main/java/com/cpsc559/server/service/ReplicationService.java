package com.cpsc559.server.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.ContentCachingRequestWrapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.Enumeration;

@Service
public class ReplicationService extends OncePerRequestFilter {
    @Value("${server.urls:}")
    private String[] backupUrls;

    @Autowired
    private ElectionService electionService;

    private final WebClient webClient;

    public ReplicationService(WebClient webClient) {
        this.webClient = webClient;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Wrap the request so the body can be read multiple times.
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);

        // Process the request normally
        filterChain.doFilter(cachingRequest, response);

        // Only the primary should replicate write operations
        boolean currentServerIsPrimary = electionService.isLeader();
        boolean requestIsWriteOperation = isWriteOperation(cachingRequest);

        if (currentServerIsPrimary && requestIsWriteOperation) {
            forwardToBackups(cachingRequest);
        }

    }


    public void forwardToBackups(ContentCachingRequestWrapper request) {
        // Capture method and path
        String method = request.getMethod();
        String apiPath = request.getRequestURI();

        // Extract headers from the original request
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.add(headerName, request.getHeader(headerName));
        }

        // Extract the request body.
        String body = request.getContentAsString();

        Flux.fromArray(backupUrls)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(backupUrl -> {
                    String requestUrl = backupUrl.trim() + apiPath;
                    System.out.println("Sending replication request to: " + requestUrl);
                    return webClient.method(HttpMethod.valueOf(method))
                            .uri(requestUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .headers(httpHeaders -> httpHeaders.addAll(headers))
                            .body(BodyInserters.fromValue(body))
                            .retrieve()
                            .bodyToMono(String.class)
                            .onErrorResume(err -> {
                                // Check if the error is due to connection refused
                                if (err.getMessage() != null && err.getMessage().contains("Connection refused")) {
                                    System.err.println("Ignoring connection refused error for " + requestUrl);
                                    return Mono.empty();
                                }
                                return Mono.error(err);
                            })
                            .doOnSuccess(ack -> System.out.println("Received ACK from " + requestUrl));
                })
                .sequential()
                .collectList()
                .block(); // Wait until all backup requests complete
    }

    public boolean isWriteOperation(ContentCachingRequestWrapper request) {
        // Ensure it is not a GET request
        String requestMethod = request.getMethod();
        boolean isWriteRequest = !requestMethod.equals("GET");

        // Ensure it is not a request to election endpoints /health, /election, /leader
        // or the /login endpoint.
        String requestUri = request.getRequestURI();

        // Ensure it is an api call (not to swagger docs or h2-console)
        boolean isApiCall = requestUri.contains("api");

        boolean isDatabaseOperation = !requestUri.contains("health") &&
                !requestUri.contains("election") &&
                !requestUri.contains("leader") &&
                !requestUri.contains("login");

        // Ensure all are true
        return isWriteRequest && isApiCall && isDatabaseOperation;
    }
}