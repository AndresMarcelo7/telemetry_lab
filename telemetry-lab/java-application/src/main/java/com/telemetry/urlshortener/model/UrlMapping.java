package com.telemetry.urlshortener.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlMapping {
    
    private String shortCode;
    private String originalUrl;
    private LocalDateTime createdAt;
    private long accessCount;

    public UrlMapping(String shortCode, String originalUrl) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.createdAt = LocalDateTime.now();
        this.accessCount = 0;
    }
    
    public void incrementAccessCount() {
        this.accessCount++;
    }
}
