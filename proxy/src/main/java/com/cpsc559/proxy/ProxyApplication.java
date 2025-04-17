package com.cpsc559.proxy;

import com.cpsc559.proxy.config.PropertiesConfig;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProxyApplication {

	private static final Logger logger = LoggerFactory.getLogger(ProxyApplication.class);

	@Autowired
	private PropertiesConfig propertiesConfig;

	public ProxyApplication() {
    }

    public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

	@PostConstruct
	public void logPrimaryUrl() {
		logger.info("Starting ProxyApplication with primary server: {}", propertiesConfig.getUrl());
	}

}
