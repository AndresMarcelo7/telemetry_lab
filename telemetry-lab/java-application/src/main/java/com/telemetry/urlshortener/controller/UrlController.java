package com.telemetry.urlshortener.controller;

import com.telemetry.urlshortener.model.ShortenRequest;
import com.telemetry.urlshortener.model.UrlMapping;
import com.telemetry.urlshortener.service.UrlShortenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UrlController {

    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);
    private final UrlShortenerService urlShortenerService;

    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    /**
     * Endpoint raíz para verificar que el servicio está activo
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, String>> home() {
        logger.info("Home endpoint accessed");
        return ResponseEntity.ok(Map.of(
            "message", "URL Shortener Service",
            "version", "1.0.0",
            "status", "running"
        ));
    }

    /**
     * Endpoint para acortar una URL
     * POST /api/shorten
     * Body: { "url": "https://example.com", "customCode": "optional" }
     */
    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody ShortenRequest request) {
        logger.info("Received shorten request for URL: {}", request.getUrl());
        
        if (request.getUrl() == null || request.getUrl().isEmpty()) {
            logger.warn("Empty URL provided");
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "URL is required"));
        }

        try {
            UrlMapping mapping = urlShortenerService.shortenUrl(
                    request.getUrl(), 
                    request.getCustomCode()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                        "shortCode", mapping.getShortCode(),
                        "originalUrl", mapping.getOriginalUrl(),
                        "shortUrl", "/api/" + mapping.getShortCode(),
                        "createdAt", mapping.getCreatedAt().toString()
                    ));
        } catch (IllegalArgumentException e) {
            logger.error("Error shortening URL: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para redirigir desde un código corto
     * GET /api/{shortCode}
     */
    @GetMapping("/{shortCode}")
    public RedirectView redirect(@PathVariable String shortCode) {
        logger.info("Redirect request for short code: {}", shortCode);
        
        return urlShortenerService.getOriginalUrl(shortCode)
                .map(mapping -> {
                    RedirectView redirectView = new RedirectView(mapping.getOriginalUrl());
                    redirectView.setStatusCode(HttpStatus.FOUND);
                    return redirectView;
                })
                .orElseGet(() -> {
                    RedirectView redirectView = new RedirectView("/api/");
                    redirectView.setStatusCode(HttpStatus.NOT_FOUND);
                    return redirectView;
                });
    }

    /**
     * Endpoint para obtener información de una URL acortada
     * GET /api/info/{shortCode}
     */
    @GetMapping("/info/{shortCode}")
    public ResponseEntity<?> getUrlInfo(@PathVariable String shortCode) {
        logger.info("Info request for short code: {}", shortCode);
        
        return urlShortenerService.getOriginalUrl(shortCode)
                .map(mapping -> ResponseEntity.ok(Map.of(
                    "shortCode", mapping.getShortCode(),
                    "originalUrl", mapping.getOriginalUrl(),
                    "createdAt", mapping.getCreatedAt().toString(),
                    "accessCount", mapping.getAccessCount()
                )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Short code not found: " + shortCode)));
    }

    /**
     * Endpoint para obtener todas las URLs (útil para debugging)
     * GET /api/urls
     */
    @GetMapping("/urls")
    public ResponseEntity<Map<String, UrlMapping>> getAllUrls() {
        logger.info("Request to get all URLs");
        return ResponseEntity.ok(urlShortenerService.getAllUrls());
    }

    /**
     * Endpoint para obtener estadísticas del servicio
     * GET /api/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        logger.info("Stats endpoint accessed");
        return ResponseEntity.ok(urlShortenerService.getStats());
    }
}
