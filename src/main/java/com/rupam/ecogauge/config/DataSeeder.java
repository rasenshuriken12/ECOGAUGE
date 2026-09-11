package com.rupam.ecogauge.config;

import com.rupam.ecogauge.model.AuthProvider;
import com.rupam.ecogauge.model.Feedback;
import com.rupam.ecogauge.model.StationData;
import com.rupam.ecogauge.model.User;
import com.rupam.ecogauge.repository.FeedbackRepository;
import com.rupam.ecogauge.repository.StationDataRepository;
import com.rupam.ecogauge.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Configuration
public class DataSeeder {

    private final Random random = new Random();

    @Bean
    public CommandLineRunner initDatabase(StationDataRepository stationDataRepository,
                                          FeedbackRepository feedbackRepository,
                                          UserRepository userRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {

            // --- USER DATA SEEDING ---
            // 1. Create ADMIN user
            if (userRepository.findByEmail("admin@ecogauge").isEmpty()) {
                User adminUser = new User();
                adminUser.setEmail("admin@ecogauge");
                adminUser.setName("Admin User");
                adminUser.setPassword(passwordEncoder.encode("admin"));
                adminUser.setProvider(AuthProvider.LOCAL);
                adminUser.setRole("ADMIN"); // Set the ADMIN role
                userRepository.save(adminUser);
            }

            // 2. Create a normal user for testing
            if (userRepository.findByEmail("testuser@ecogauge").isEmpty()) {
                User normalUser = new User();
                normalUser.setEmail("testuser@ecogauge");
                normalUser.setName("Test User");
                normalUser.setPassword(passwordEncoder.encode("password"));
                normalUser.setProvider(AuthProvider.LOCAL);
                normalUser.setRole("USER"); // Default USER role
                userRepository.save(normalUser);
            }

            // --- STATION DATA SEEDING ---
            // Clear existing station data for a fresh seed run (optional, remove if you want to keep old data)
            // stationDataRepository.deleteAll();

            // Only seed if the database is empty
            if (stationDataRepository.count() == 0) {
                Instant now = Instant.now();

                List<StationData> initialStations = List.of(
                        // --- Existing 14 Stations ---
                        createReading("Churchgate", "Western", 18.9283, 72.8297, 48.0, 88.0, 35.0, 40.0, 68.0, now.minusSeconds(randSecs())),
                        createReading("Mumbai Central", "Western", 18.9690, 72.8195, 50.0, 92.0, 38.0, 38.0, 71.0, now.minusSeconds(randSecs())),
                        createReading("Dadar (W)", "Western", 19.0186, 72.8432, 52.0, 95.0, 40.0, 35.0, 75.0, now.minusSeconds(randSecs())),
                        createReading("Kandivali", "Western", 19.2081, 72.8554, 45.0, 85.0, 33.0, 41.0, 66.0, now.minusSeconds(randSecs())),
                        createReading("Mira Road", "Western", 19.2780, 72.8519, 43.0, 82.0, 31.0, 44.0, 64.0, now.minusSeconds(randSecs())),

                        createReading("CSMT", "Central", 18.9402, 72.8355, 50.0, 93.0, 38.0, 38.0, 70.0, now.minusSeconds(randSecs())),
                        createReading("Dadar (C)", "Central", 19.0180, 72.8450, 53.0, 96.0, 41.0, 34.0, 77.0, now.minusSeconds(randSecs())),
                        createReading("Kurla", "Central", 19.0640, 72.8850, 54.0, 98.0, 42.0, 33.0, 80.0, now.minusSeconds(randSecs())),
                        createReading("Vidyavihar", "Central", 19.0792, 72.9189, 48.0, 90.0, 37.0, 39.0, 73.0, now.minusSeconds(randSecs())),
                        createReading("Vikhroli", "Central", 19.1171, 72.9463, 49.0, 91.0, 38.0, 37.0, 74.0, now.minusSeconds(randSecs())),

                        createReading("Govandi", "Harbour", 19.0480, 72.9200, 56.0, 102.0, 44.0, 31.0, 82.0, now.minusSeconds(randSecs())),
                        createReading("Panvel", "Harbour", 18.9894, 73.1175, 48.0, 88.0, 36.0, 39.0, 67.0, now.minusSeconds(randSecs())),
                        createReading("Seawoods-Darave", "Harbour", 19.0200, 73.0010, 42.0, 80.0, 31.0, 43.0, 65.0, now.minusSeconds(randSecs())),
                        createReading("Khandeshwar", "Harbour", 18.9700, 73.1000, 45.0, 85.0, 33.0, 40.0, 68.0, now.minusSeconds(randSecs())),

                        // --- NEW 16 Stations ---
                        // Western Line (5 new)
                        createReading("Bandra", "Western", 19.0560, 72.8410, 51.0, 94.0, 39.0, 36.0, 74.0, now.minusSeconds(randSecs())),
                        createReading("Andheri", "Western", 19.1190, 72.8465, 53.0, 97.0, 41.0, 34.0, 78.0, now.minusSeconds(randSecs())),
                        createReading("Borivali", "Western", 19.2290, 72.8570, 46.0, 86.0, 34.0, 42.0, 69.0, now.minusSeconds(randSecs())),
                        createReading("Bhayandar", "Western", 19.3000, 72.8500, 44.0, 83.0, 32.0, 43.0, 65.0, now.minusSeconds(randSecs())),
                        createReading("Nalasopara", "Western", 19.4180, 72.8190, 47.0, 89.0, 35.0, 40.0, 67.0, now.minusSeconds(randSecs())),

                        // Central Line (6 new)
                        createReading("Thane", "Central", 19.1860, 72.9760, 55.0, 100.0, 43.0, 32.0, 81.0, now.minusSeconds(randSecs())),
                        createReading("Dombivli", "Central", 19.2180, 73.0860, 52.0, 95.0, 40.0, 35.0, 79.0, now.minusSeconds(randSecs())),
                        createReading("Kalyan", "Central", 19.2430, 73.1310, 54.0, 99.0, 42.0, 33.0, 80.0, now.minusSeconds(randSecs())),
                        createReading("Sion", "Central", 19.0430, 72.8630, 49.0, 91.0, 37.0, 38.0, 72.0, now.minusSeconds(randSecs())),
                        createReading("Ghatkopar", "Central", 19.0850, 72.9090, 51.0, 93.0, 39.0, 36.0, 76.0, now.minusSeconds(randSecs())),
                        createReading("Mulund", "Central", 19.1720, 72.9490, 48.0, 89.0, 36.0, 40.0, 71.0, now.minusSeconds(randSecs())),

                        // Harbour Line (5 new)
                        createReading("Vashi", "Harbour", 19.0660, 73.0010, 43.0, 81.0, 32.0, 44.0, 66.0, now.minusSeconds(randSecs())),
                        createReading("Belapur CBD", "Harbour", 19.0180, 73.0300, 41.0, 79.0, 30.0, 45.0, 63.0, now.minusSeconds(randSecs())),
                        createReading("Chembur", "Harbour", 19.0560, 72.9000, 53.0, 97.0, 41.0, 34.0, 78.0, now.minusSeconds(randSecs())),
                        createReading("Mankhurd", "Harbour", 19.0500, 72.9300, 57.0, 104.0, 45.0, 30.0, 83.0, now.minusSeconds(randSecs())),
                        createReading("Nerul", "Harbour", 19.0300, 73.0150, 40.0, 78.0, 29.0, 46.0, 62.0, now.minusSeconds(randSecs()))
                );
                stationDataRepository.saveAll(initialStations);
            }


            // --- FEEDBACK SEEDING ---
            if (feedbackRepository.count() == 0) {
                List<Feedback> initialFeedback = List.of(
                        createFeedback("Rahul Sharma", "rahul.s@example.com", "Western", "Borivali", "The AQI alerts are very useful. The historical charts load quickly!"),
                        createFeedback("Priya Singh", "priya.s@example.com", "Central", "Thane", "The dashboard layout is confusing. Can you simplify the navigation bar?"),
                        createFeedback("Amit Patel", "amit.p@example.com", "Harbour", "Panvel", "The map view link doesn't work. Please fix the map feature.")
                );
                feedbackRepository.saveAll(initialFeedback);
            }


            System.out.println("Seeding complete. Total users: " + userRepository.count() + ", Total stations: " + stationDataRepository.count() + ", Total feedback: " + feedbackRepository.count());
        };
    }

    // Helper to add minor variation to timestamps
    private int randSecs() {
        return random.nextInt(300); // 0-5 minutes ago
    }

    private StationData createReading(String name, String line, double lat, double lon, double pm25, double pm10, double no2, double o3, double noise, Instant timestamp) {
        String aqiCat = categorizeAqi(pm25, pm10, no2, o3);
        int aqiValue = calculateAQI(pm25, pm10, no2, o3);
        String dominant = determineDominant(pm25, pm10, no2, o3);
        String noiseCat = categorizeNoise(noise);

        return new StationData(null, name, line, lat, lon, aqiValue, aqiCat, dominant, noise, noiseCat, pm25, pm10, no2, o3, timestamp);
    }

    private Feedback createFeedback(String name, String email, String region, String location, String message) {
        Feedback f = new Feedback();
        f.setName(name);
        f.setEmail(email);
        f.setRegion(region);
        f.setLocation(location);
        f.setMessage(message);
        f.setTimestamp(Instant.now().minusSeconds(random.nextInt(86400 * 7))); // Random timestamp within the last week
        return f;
    }

    // --- Helper Logic to mirror front-end for initial seeding ---

    private int calculateAQI(double pm25, double pm10, double no2, double o3) {
        // Simplified logic: just take the max value scaled to 1-150 range.
        double maxPollutant = Math.max(pm25, Math.max(pm10 / 2, Math.max(no2 * 2, o3)));
        // Clamp value to a reasonable range, e.g., 30-180
        int calculated = (int) Math.round(maxPollutant * 1.5);
        return Math.max(30, Math.min(calculated, 180));
    }

    private String categorizeAqi(double pm25, double pm10, double no2, double o3) {
        int aqi = calculateAQI(pm25, pm10, no2, o3);
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

    private String determineDominant(double pm25, double pm10, double no2, double o3) {
        if (pm25 > 50) return "PM2.5";
        if (pm10 > 90) return "PM10";
        if (no2 > 40) return "NO2";
        if (o3 > 50) return "O3";

        // Fallback dominant
        if (pm25 > 30) return "PM2.5";
        return "PM10";
    }
}