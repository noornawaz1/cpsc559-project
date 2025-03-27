package com.cpsc559.server.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PrimaryCheckService{

    @Value("${server.urls:}")
    private String backupUrls;

    public boolean isPrimary(HttpServletRequest request) {
      String currentUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
      
      String[] backups = backupUrls.split(",");
      for (String backup : backups) {
          if (currentUrl.equalsIgnoreCase(backup.trim())) {
              // This server's URL matches one of the backup URLs.
              return false;
          }
      }
      return true;
    } 
}