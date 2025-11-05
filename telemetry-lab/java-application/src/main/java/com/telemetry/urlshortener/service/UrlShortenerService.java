package com.telemetry.urlshortener.service;

import com.telemetry.urlshortener.model.UrlMapping;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UrlShortenerService {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;

    // Persistencia en memoria con ConcurrentHashMap para thread-safety
    private final Map<String, UrlMapping> urlStorage = new ConcurrentHashMap<>();
    private final Random random = new Random();

    // Métricas personalizadas
    private final Counter urlsShortenedCounter;
    private final Counter urlsAccessedCounter;

    public UrlShortenerService(MeterRegistry meterRegistry) {
        // Registrar métricas personalizadas en Prometheus
        this.urlsShortenedCounter = Counter.builder("urls_shortened_total")
                .description("Total number of URLs shortened")
                .register(meterRegistry);
        
        this.urlsAccessedCounter = Counter.builder("urls_accessed_total")
                .description("Total number of times shortened URLs were accessed")
                .register(meterRegistry);
        
        logger.info("UrlShortenerService initialized with in-memory storage");
    }

    /**
     * Acorta una URL y retorna el código corto
     */
    public UrlMapping shortenUrl(String originalUrl, String customCode) {
        String shortCode;
        
        if (customCode != null && !customCode.isEmpty()) {
            // Validar que el código personalizado no exista
            if (urlStorage.containsKey(customCode)) {
                logger.warn("Custom code already exists: {}", customCode);
                throw new IllegalArgumentException("Custom code already exists: " + customCode);
            }
            shortCode = customCode;
        } else {
            // Generar código aleatorio
            shortCode = generateShortCode();
        }

        UrlMapping mapping = new UrlMapping(shortCode, originalUrl);
        urlStorage.put(shortCode, mapping);
        
        urlsShortenedCounter.increment();
        logger.info("URL shortened - Code: {}, Original URL: {}", shortCode, originalUrl);
        
        return mapping;
    }

    /**
     * Obtiene la URL original a partir del código corto
     */
    public Optional<UrlMapping> getOriginalUrl(String shortCode) {
        UrlMapping mapping = urlStorage.get(shortCode);
        
        if (mapping != null) {
            mapping.incrementAccessCount();
            urlsAccessedCounter.increment();
            logger.info("URL accessed - Code: {}, Access count: {}", shortCode, mapping.getAccessCount());
        } else {
            logger.warn("Short code not found: {}", shortCode);
        }
        
        return Optional.ofNullable(mapping);
    }

    /**
     * Obtiene todas las URLs almacenadas (útil para debugging)
     */
    public Map<String, UrlMapping> getAllUrls() {
        logger.debug("Retrieving all URLs - Total: {}", urlStorage.size());
        return Map.copyOf(urlStorage);
    }

    /**
     * Obtiene estadísticas del servicio
     */
    public Map<String, Object> getStats() {
        return Map.of(
            "totalUrls", urlStorage.size(),
            "totalAccesses", urlStorage.values().stream()
                    .mapToLong(UrlMapping::getAccessCount)
                    .sum()
        );
    }

    /**
     * Genera un código corto aleatorio único
     */
    private String generateShortCode() {
        String code;
        do {
            code = random.ints(SHORT_CODE_LENGTH, 0, CHARACTERS.length())
                    .mapToObj(CHARACTERS::charAt)
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString();
        } while (urlStorage.containsKey(code));
        
        return code;
    }
}
