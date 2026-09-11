package com.rupam.ecogauge.controller;

import com.rupam.ecogauge.model.Feedback;
import com.rupam.ecogauge.model.StationData;
import com.rupam.ecogauge.repository.FeedbackRepository;
import com.rupam.ecogauge.repository.StationDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DataRestController {

    private final StationDataRepository stationDataRepository;
    private final FeedbackRepository feedbackRepository;

    @Autowired
    public DataRestController(StationDataRepository stationDataRepository, FeedbackRepository feedbackRepository) {
        this.stationDataRepository = stationDataRepository;
        this.feedbackRepository = feedbackRepository;
    }

    // --- STATION DATA ENDPOINTS ---

    @GetMapping("/stations")
    public List<StationData> getAllStations() {
        return stationDataRepository.findAllByOrderByTimestampDesc();
    }

    @PostMapping("/stations")
    public StationData addStation(@RequestBody StationData station) {
        // Ensure ID is null for new insert
        station.setId(null);
        station.setTimestamp(Instant.now());

        // Perform necessary categorization calculations before saving
        station.setAqiCategory(categorizeAqi(station.getAqiValue()));
        station.setNoiseCategory(categorizeNoise(station.getNoiseLevel()));
        station.setDominantPollutant(determineDominant(station));

        return stationDataRepository.save(station);
    }

    // NEW: UPDATE (PUT) Endpoint
    @PutMapping("/stations/{id}")
    public StationData updateStation(@PathVariable Long id, @RequestBody StationData updatedStation) {
        return stationDataRepository.findById(id)
                .map(station -> {
                    // Update only mutable administrative and measurement fields
                    station.setStationName(updatedStation.getStationName());
                    station.setLine(updatedStation.getLine());
                    station.setLat(updatedStation.getLat());
                    station.setLon(updatedStation.getLon());

                    // Update latest readings and recalculate categories
                    station.setAqiValue(updatedStation.getAqiValue());
                    station.setNoiseLevel(updatedStation.getNoiseLevel());
                    station.setPm25(updatedStation.getPm25());
                    station.setPm10(updatedStation.getPm10());
                    station.setNo2(updatedStation.getNo2());
                    station.setO3(updatedStation.getO3());

                    // Recalculate derived fields
                    station.setAqiCategory(categorizeAqi(updatedStation.getAqiValue()));
                    station.setNoiseCategory(categorizeNoise(updatedStation.getNoiseLevel()));
                    station.setDominantPollutant(determineDominant(updatedStation));

                    // Update timestamp to mark the latest update time
                    station.setTimestamp(Instant.now());

                    return stationDataRepository.save(station);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found with id " + id));
    }

    // NEW: DELETE Endpoint
    @DeleteMapping("/stations/{id}")
    public void deleteStation(@PathVariable Long id) {
        if (!stationDataRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found with id " + id);
        }
        stationDataRepository.deleteById(id);
    }


    // --- FEEDBACK ENDPOINTS (No change) ---

    @GetMapping("/feedback")
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAllByOrderByTimestampDesc();
    }

    @PostMapping("/feedback")
    public Feedback submitFeedback(@RequestBody Feedback feedback) {
        feedback.setId(null);
        feedback.setTimestamp(Instant.now());
        return feedbackRepository.save(feedback);
    }

    // Helper functions for categorization (simple placeholders)
    private String categorizeAqi(double aqi) {
        if (aqi <= 50) return "Good";
        if (aqi <= 100) return "Moderate";
        if (aqi <= 150) return "Poor";
        if (aqi <= 200) return "Unhealthy";
        return "Severe";
    }

    private String categorizeNoise(double noise) {
        if (noise <= 70) return "Moderate";
        if (noise <= 85) return "High";
        return "Dangerous";
    }

    private String determineDominant(StationData station) {
        // Simple logic: PM2.5 > PM10 > NO2
        Double pm25 = station.getPm25() != null ? station.getPm25() : 0.0;
        Double pm10 = station.getPm10() != null ? station.getPm10() : 0.0;
        Double no2 = station.getNo2() != null ? station.getNo2() : 0.0;

        if (pm25 > pm10 && pm25 > no2) return "PM2.5";
        if (pm10 > pm25 && pm10 > no2) return "PM10";
        if (no2 > pm25 && no2 > pm10) return "NO2";

        return "N/A";
    }
}