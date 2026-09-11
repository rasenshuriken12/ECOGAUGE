// In src/main/java/com/rupam/ecogauge/controller/RankingPageController.java

package com.rupam.ecogauge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RankingPageController {

    @GetMapping("/ranking")
    public String showDashboardRankingPage() {
        // Secured path for dashboard/admin view
        return "ranking.html";
    }

    @GetMapping("/user/ranking")
    public String showUserRankingPage() {
        // Secured path for the user view (serves file from /static/)
        return "user_ranking.html";
    }
}