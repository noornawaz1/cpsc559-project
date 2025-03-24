package com.cpsc559.server.model;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplicationRequest {
    private String operation;
    private String apiPath;
    private Object payload;
    private Map<String, String> headers;

}