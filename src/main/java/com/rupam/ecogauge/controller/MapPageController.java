package com.rupam.ecogauge.controller;

import com.rupam.ecogauge.model.StationData;
import com.rupam.ecogauge.repository.StationDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MapPageController {

    private final StationDataRepository stationDataRepository;

    @Autowired
    public MapPageController(StationDataRepository stationDataRepository) {
        this.stationDataRepository = stationDataRepository;
    }

    /**
     * Helper to get only the latest reading for each unique station name.
     * (Retained but no longer used by the controller itself.)
     */
    private List<StationData> getLatestStationReadings() {
        // Fetch all data, relies on sorting in repository for efficiency
        List<StationData> allReadings = stationDataRepository.findAllByOrderByTimestampDesc();

        // Use a Map to filter for the newest reading of each unique station name
        return allReadings.stream()
                .collect(Collectors.toMap(
                        StationData::getStationName,
                        station -> station,
                        (existing, replacement) -> existing
                ))
                .values().stream()
                .toList();
    }

    @GetMapping("/maps/aqi")
    public String showAqiMap() {
        // Returns the static file: AQI_MAP.html
        return "AQI_MAP.html";
    }

    @GetMapping("/maps/noise")
    public String showNoiseMap() {
        // Returns the static file: NOISE_MAP.html
        return "NOISE_MAP.html";
    }
}