package com.rupam.ecogauge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/") // Maps the root path to the new PUBLIC landing page
    public String showLandingPage() {
        return "landing_page.html";
    }

    @GetMapping("/home") // NEW path for the authenticated user's home page
    public String showAqiHome() {
        return "AQI_Home.html";
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        // This is the secured ADMIN landing page.
        return "dashboard.html";
    }

    @GetMapping("/feedback")
    public String showFeedbackForm() {
        return "feedback.html";
    }
}