package com.brahmosbhawan.controller;

import com.brahmosbhawan.dto.FoodDtos;
import com.brahmosbhawan.security.UserPrincipal;
import com.brahmosbhawan.service.FoodPreferenceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentFoodController {

    private final FoodPreferenceService foodPreferenceService;

    public StudentFoodController(FoodPreferenceService foodPreferenceService) {
        this.foodPreferenceService = foodPreferenceService;
    }

    @PostMapping("/food-preference")
    public ResponseEntity<FoodDtos.StudentPreferenceDto> submitFoodPreference(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody FoodDtos.FoodPreferenceRequest request) {
        FoodDtos.StudentPreferenceDto result = foodPreferenceService.saveOrUpdatePreference(currentUser, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/food-preference")
    public ResponseEntity<List<FoodDtos.StudentPreferenceDto>> getFoodPreferencesForWeek(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDate start = startDate != null ? startDate : LocalDate.now();
        LocalDate end = endDate != null ? endDate : start.plusDays(6);

        List<FoodDtos.StudentPreferenceDto> list = foodPreferenceService.getStudentPreferencesForDateRange(currentUser, start, end);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/meal-history")
    public ResponseEntity<List<FoodDtos.StudentPreferenceDto>> getMealHistory(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<FoodDtos.StudentPreferenceDto> history = foodPreferenceService.getStudentMealHistory(currentUser);
        return ResponseEntity.ok(history);
    }
}
