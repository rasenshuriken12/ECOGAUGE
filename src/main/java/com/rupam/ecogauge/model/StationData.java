package com.rupam.ecogauge.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

/**
 * Represents a single station's environmental reading at a specific time.
 * This entity stores AQI, Noise, and pollutant concentration data.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Location Identifiers
    @Column(nullable = false)
    private String stationName;

    @Column(nullable = false)
    private String line; // e.g., "Western", "Central", "Harbour"

    private Double lat;
    private Double lon;

    // Core Environmental Metrics
    private Integer aqiValue;           // Air Quality Index (Primary Value for AQI mode)
    private String aqiCategory;         // e.g., "Good", "Moderate"
    private String dominantPollutant;   // e.g., "PM2.5", "NO2"
    private Double noiseLevel;          // Noise Level (Primary Value for Noise mode)
    private String noiseCategory;       // e.g., "Low", "High"

    // Air Composition Concentrations
    private Double pm25;
    private Double pm10;
    private Double no2;
    private Double o3;

    @Column(nullable = false)
    private Instant timestamp;
}

