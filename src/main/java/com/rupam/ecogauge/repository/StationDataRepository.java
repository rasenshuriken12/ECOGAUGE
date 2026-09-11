package com.rupam.ecogauge.repository;

import com.rupam.ecogauge.model.StationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing StationData entities.
 * Provides methods to interact with the STATION_DATA table.
 */
@Repository
public interface StationDataRepository extends JpaRepository<StationData, Long> {

    /**
     * Fetches all station data, ordered by the latest timestamp first.
     */
    List<StationData> findAllByOrderByTimestampDesc();
}
