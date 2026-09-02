package com.brahmosbhawan.controller;

import com.brahmosbhawan.dto.FoodDtos;
import com.brahmosbhawan.entity.ComplaintCategory;
import com.brahmosbhawan.entity.ComplaintStatus;
import com.brahmosbhawan.entity.MealType;
import com.brahmosbhawan.entity.PriorityLevel;
import com.brahmosbhawan.service.ExcelExportService;
import com.brahmosbhawan.service.FoodPreferenceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminFoodController {

    private final FoodPreferenceService foodPreferenceService;
    private final ExcelExportService excelExportService;

    public AdminFoodController(FoodPreferenceService foodPreferenceService, ExcelExportService excelExportService) {
        this.foodPreferenceService = foodPreferenceService;
        this.excelExportService = excelExportService;
    }

    @GetMapping("/food-summary")
    public ResponseEntity<FoodDtos.FoodSummaryResponse> getFoodSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) MealType mealType) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        FoodDtos.FoodSummaryResponse summary = foodPreferenceService.getAdminFoodSummary(queryDate, mealType);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/food-preferences")
    public ResponseEntity<List<FoodDtos.StudentPreferenceDto>> getFoodPreferences(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) MealType mealType) {
        List<FoodDtos.StudentPreferenceDto> preferences = foodPreferenceService.getAllPreferencesForAdmin(startDate, endDate, mealType);
        return ResponseEntity.ok(preferences);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) MealType mealType) throws IOException {

        byte[] excelBytes = excelExportService.generateMasterExcelReport(startDate, endDate, mealType, null, null, null, null);
        String filename = "BRAHMOS_Hostel_Report_" + (startDate != null ? startDate : LocalDate.now()) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/export/complaints/excel")
    public ResponseEntity<byte[]> exportComplaintsExcel(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) ComplaintCategory category,
            @RequestParam(required = false) PriorityLevel priority,
            @RequestParam(required = false) String search) throws IOException {

        byte[] excelBytes = excelExportService.generateComplaintsExcelReport(status, category, priority, search);
        String filename = "BRAHMOS_Complaints_Report_" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
