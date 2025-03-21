package com.cpsc559.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

// Opens up the h2 console and swagger docs when the app starts
@Component
public class BrowserLauncher implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Retrieve the port from application.properties. Defaults to 8080 if not set.
        String port = environment.getProperty("server.port", "8080");

        // Define urls for h2 console and swagger docs
        String baseUrl = "http://localhost:" + port;
        String swaggerUrl = baseUrl + "/swagger-ui/index.html";
        String h2Url = baseUrl + "/h2-console/";

        // Try using Java's desktop API
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(baseUrl + swaggerUrl));
                Desktop.getDesktop().browse(new URI(baseUrl + h2Url));
            } catch (Exception e) {
                System.out.println("Desktop API Error: " + e.getMessage());
            }
        }

        // Fallback: Try using an OS-specific command (Windows, Mac, Linux/Unix)
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", swaggerUrl});
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", h2Url});
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", swaggerUrl});
                Runtime.getRuntime().exec(new String[]{"open", h2Url});
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec(new String[]{"xdg-open", swaggerUrl});
                Runtime.getRuntime().exec(new String[]{"xdg-open", h2Url});
            } else {
                System.out.println("Unsupported OS. Please open the URLs manually:");
                System.out.println(swaggerUrl);
                System.out.println(h2Url);
            }
        } catch (Exception ex) {
            System.out.println("Error launching browser with OS-specific command: " + ex.getMessage());
            System.out.println("Please open the following URLs manually:");
            System.out.println(swaggerUrl);
            System.out.println(h2Url);
        }
    }
}
