package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.classroom.DashboardEngagementDTO;
import com.cosmo.wanda_web.dto.classroom.DashboardOverviewDTO;
import com.cosmo.wanda_web.dto.classroom.DashboardRankingDTO;
import com.cosmo.wanda_web.services.ClassroomDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/classroom")
public class ClassroomDashboardController {

    @Autowired
    private ClassroomDashboardService dashboardService;

    @GetMapping("/{id}/dashboard/overview")
    @PreAuthorize("@authz.isClassroomInstructor(authentication, #id)")
    public ResponseEntity<DashboardOverviewDTO> getOverview(@PathVariable Long id, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        DashboardOverviewDTO response = dashboardService.getOverview(id, from, to);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/dashboard/engagement")
    @PreAuthorize("@authz.isClassroomInstructor(authentication, #id)")
    public ResponseEntity<Page<DashboardEngagementDTO>> getEngagement(@PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable) {
        Page<DashboardEngagementDTO> response = dashboardService.getEngagement(id, from, to, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/dashboard/ranking")
    @PreAuthorize("@authz.isClassroomInstructor(authentication, #id)")
    public ResponseEntity<List<DashboardRankingDTO>> getRanking(@PathVariable Long id) {
        List<DashboardRankingDTO> response = dashboardService.getRanking(id);
        return ResponseEntity.ok(response);
    }
}