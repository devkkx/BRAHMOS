package com.brahmosbhawan.service;

import com.brahmosbhawan.dto.ComplaintDtos;
import com.brahmosbhawan.dto.FoodDtos;
import com.brahmosbhawan.entity.ComplaintCategory;
import com.brahmosbhawan.entity.ComplaintStatus;
import com.brahmosbhawan.entity.MealType;
import com.brahmosbhawan.entity.PriorityLevel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class ExcelExportService {

    private final FoodPreferenceService foodPreferenceService;
    private final ComplaintService complaintService;

    public ExcelExportService(FoodPreferenceService foodPreferenceService, ComplaintService complaintService) {
        this.foodPreferenceService = foodPreferenceService;
        this.complaintService = complaintService;
    }

    public byte[] generateFoodPreferenceExcelReport(LocalDate startDate, LocalDate endDate, MealType mealType) throws IOException {
        return generateMasterExcelReport(startDate, endDate, mealType, null, null, null, null);
    }

    public byte[] generateComplaintsExcelReport(ComplaintStatus status, ComplaintCategory category, PriorityLevel priority, String search) throws IOException {
        return generateMasterExcelReport(null, null, null, status, category, priority, search);
    }

    public byte[] generateMasterExcelReport(LocalDate startDate, LocalDate endDate, MealType mealType,
                                            ComplaintStatus status, ComplaintCategory category,
                                            PriorityLevel priority, String search) throws IOException {

        LocalDate effectiveDate = startDate != null ? startDate : LocalDate.now();
        FoodDtos.FoodSummaryResponse foodSummary = foodPreferenceService.getAdminFoodSummary(effectiveDate, mealType);
        List<FoodDtos.StudentPreferenceDto> preferences = foodPreferenceService.getAllPreferencesForAdmin(startDate, endDate, mealType);
        List<ComplaintDtos.ComplaintResponse> complaints = complaintService.getAdminFilteredComplaints(status, category, priority, search);
        Map<String, Long> complaintStats = complaintService.getComplaintStatistics();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Shared Fonts and Styles
            Font titleFont = workbook.createFont();
            titleFont.setFontName("Calibri");
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setBold(true);
            titleFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Font headerFont = workbook.createFont();
            headerFont.setFontName("Calibri");
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);

            CellStyle subHeaderStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            subHeaderStyle.setFont(boldFont);

            CellStyle vegStyle = workbook.createCellStyle();
            Font vegFont = workbook.createFont();
            vegFont.setBold(true);
            vegFont.setColor(IndexedColors.DARK_GREEN.getIndex());
            vegStyle.setFont(vegFont);

            CellStyle nonVegStyle = workbook.createCellStyle();
            Font nonVegFont = workbook.createFont();
            nonVegFont.setBold(true);
            nonVegFont.setColor(IndexedColors.DARK_RED.getIndex());
            nonVegStyle.setFont(nonVegFont);

            CellStyle pendingStyle = workbook.createCellStyle();
            Font pendingFont = workbook.createFont();
            pendingFont.setBold(true);
            pendingFont.setColor(IndexedColors.ORANGE.getIndex());
            pendingStyle.setFont(pendingFont);

            CellStyle progressStyle = workbook.createCellStyle();
            Font progressFont = workbook.createFont();
            progressFont.setBold(true);
            progressFont.setColor(IndexedColors.BLUE.getIndex());
            progressStyle.setFont(progressFont);

            DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            // =========================================================================
            // Sheet 1: Student Mess Food Selections
            // =========================================================================
            Sheet foodSheet = workbook.createSheet("Mess Food Choices");
            foodSheet.setDisplayGridlines(true);

            Row fTitleRow = foodSheet.createRow(0);
            fTitleRow.setHeightInPoints(40);
            Cell fTitleCell = fTitleRow.createCell(0);
            fTitleCell.setCellValue("🏠 BRAHMOS BHAWAN - STUDENT MEAL SELECTIONS REPORT");
            fTitleCell.setCellStyle(titleStyle);
            foodSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            int rowIdx = 2;
            Row rDate = foodSheet.createRow(rowIdx++);
            rDate.createCell(0).setCellValue("Report Date:");
            rDate.createCell(1).setCellValue(effectiveDate.format(dateFormatter));
            rDate.getCell(0).setCellStyle(subHeaderStyle);

            Row rMeal = foodSheet.createRow(rowIdx++);
            rMeal.createCell(0).setCellValue("Meal Filter:");
            rMeal.createCell(1).setCellValue(mealType != null ? mealType.name() : "ALL MEALS (LUNCH & DINNER)");
            rMeal.getCell(0).setCellStyle(subHeaderStyle);

            rowIdx++; // Blank

            // Summary Counts Header - Separate Lunch & Dinner Breakdown!
            Row fSumHeaderRow = foodSheet.createRow(rowIdx++);
            Cell fsh1 = fSumHeaderRow.createCell(0); fsh1.setCellValue("Meal & Preference Type"); fsh1.setCellStyle(headerStyle);
            Cell fsh2 = fSumHeaderRow.createCell(1); fsh2.setCellValue("Count"); fsh2.setCellStyle(headerStyle);

            Row rLunchVeg = foodSheet.createRow(rowIdx++);
            Cell cLV = rLunchVeg.createCell(0); cLV.setCellValue("☀️ Lunch - Veg Count"); cLV.setCellStyle(vegStyle);
            rLunchVeg.createCell(1).setCellValue(foodSummary.getLunchVegCount());

            Row rLunchNonVeg = foodSheet.createRow(rowIdx++);
            Cell cLNV = rLunchNonVeg.createCell(0); cLNV.setCellValue("☀️ Lunch - Non-Veg Count"); cLNV.setCellStyle(nonVegStyle);
            rLunchNonVeg.createCell(1).setCellValue(foodSummary.getLunchNonVegCount());

            Row rDinnerVeg = foodSheet.createRow(rowIdx++);
            Cell cDV = rDinnerVeg.createCell(0); cDV.setCellValue("🌙 Dinner - Veg Count"); cDV.setCellStyle(vegStyle);
            rDinnerVeg.createCell(1).setCellValue(foodSummary.getDinnerVegCount());

            Row rDinnerNonVeg = foodSheet.createRow(rowIdx++);
            Cell cDNV = rDinnerNonVeg.createCell(0); cDNV.setCellValue("🌙 Dinner - Non-Veg Count"); cDNV.setCellStyle(nonVegStyle);
            rDinnerNonVeg.createCell(1).setCellValue(foodSummary.getDinnerNonVegCount());

            Row rTotal = foodSheet.createRow(rowIdx++);
            Cell cTotLabel = rTotal.createCell(0); cTotLabel.setCellValue("Total Meals Requested"); cTotLabel.setCellStyle(subHeaderStyle);
            Cell cTotVal = rTotal.createCell(1); cTotVal.setCellValue(foodSummary.getTotalCount()); cTotVal.setCellStyle(subHeaderStyle);

            rowIdx += 2;

            // Student Roster Header
            Row fRosterTitle = foodSheet.createRow(rowIdx++);
            Cell frCell = fRosterTitle.createCell(0); frCell.setCellValue("📋 Detailed Boarder Selection Roster"); frCell.setCellStyle(subHeaderStyle);

            Row fHeaderRow = foodSheet.createRow(rowIdx++);
            fHeaderRow.setHeightInPoints(24);
            String[] fCols = {"Student ID", "Student Name", "Hostel Block", "Room No", "Date", "Day", "Meal Type", "Food Preference", "Submission Time"};
            for (int i = 0; i < fCols.length; i++) {
                Cell cell = fHeaderRow.createCell(i);
                cell.setCellValue(fCols[i]);
                cell.setCellStyle(headerStyle);
            }

            for (FoodDtos.StudentPreferenceDto dto : preferences) {
                Row r = foodSheet.createRow(rowIdx++);
                r.createCell(0).setCellValue(dto.getStudentId());
                r.createCell(1).setCellValue(dto.getStudentName());
                r.createCell(2).setCellValue(dto.getBlock() != null ? dto.getBlock().replace("_", " ") : "A BLOCK");
                r.createCell(3).setCellValue(dto.getRoomNumber());
                r.createCell(4).setCellValue(dto.getDate() != null ? dto.getDate().format(dateFormatter) : "");
                r.createCell(5).setCellValue(dto.getDay());
                r.createCell(6).setCellValue(dto.getMealType().name());

                Cell prefCell = r.createCell(7);
                prefCell.setCellValue(dto.getFoodPreference().name());
                if (dto.getFoodPreference() == com.brahmosbhawan.entity.PreferenceType.VEG) {
                    prefCell.setCellStyle(vegStyle);
                } else {
                    prefCell.setCellStyle(nonVegStyle);
                }

                r.createCell(8).setCellValue(dto.getSubmittedAt() != null ? dto.getSubmittedAt().format(dtFormatter) : "");
            }

            for (int i = 0; i < fCols.length; i++) {
                foodSheet.autoSizeColumn(i);
                foodSheet.setColumnWidth(i, foodSheet.getColumnWidth(i) + 1200);
            }

            // =========================================================================
            // Sheet 2: Student Complaints & Maintenance Reports
            // =========================================================================
            Sheet compSheet = workbook.createSheet("Hostel Complaints");
            compSheet.setDisplayGridlines(true);

            Row cTitleRow = compSheet.createRow(0);
            cTitleRow.setHeightInPoints(40);
            Cell cTitleCell = cTitleRow.createCell(0);
            cTitleCell.setCellValue("🚨 BRAHMOS BHAWAN - STUDENT COMPLAINTS REPORT");
            cTitleCell.setCellStyle(titleStyle);
            compSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));

            int cRowIdx = 2;
            Row cHeaderRow = compSheet.createRow(cRowIdx++);
            cHeaderRow.setHeightInPoints(24);
            String[] cCols = {"ID", "Student ID", "Student Name", "Hostel Block", "Room No", "Category", "Problem Title", "Description", "Priority", "Status", "Photo Evidence URL", "Admin Remarks", "Filed Date"};
            for (int i = 0; i < cCols.length; i++) {
                Cell cell = cHeaderRow.createCell(i);
                cell.setCellValue(cCols[i]);
                cell.setCellStyle(headerStyle);
            }

            for (ComplaintDtos.ComplaintResponse c : complaints) {
                Row r = compSheet.createRow(cRowIdx++);
                r.createCell(0).setCellValue("#" + c.getId());
                r.createCell(1).setCellValue(c.getStudentId());
                r.createCell(2).setCellValue(c.getStudentName());
                r.createCell(3).setCellValue(c.getBlock() != null ? c.getBlock().replace("_", " ") : "A BLOCK");
                r.createCell(4).setCellValue(c.getRoomNumber());
                r.createCell(5).setCellValue(c.getCategory().name());
                r.createCell(6).setCellValue(c.getTitle());
                r.createCell(7).setCellValue(c.getDescription());
                r.createCell(8).setCellValue(c.getPriority().name());

                Cell stCell = r.createCell(9);
                stCell.setCellValue(c.getStatus().name());
                if (c.getStatus() == ComplaintStatus.PENDING) stCell.setCellStyle(pendingStyle);
                else if (c.getStatus() == ComplaintStatus.IN_PROGRESS) stCell.setCellStyle(progressStyle);
                else if (c.getStatus() == ComplaintStatus.RESOLVED) stCell.setCellStyle(vegStyle);
                else if (c.getStatus() == ComplaintStatus.REJECTED) stCell.setCellStyle(nonVegStyle);

                r.createCell(10).setCellValue(c.getImageUrl() != null && !c.getImageUrl().isEmpty() ? c.getImageUrl() : "No Photo");
                r.createCell(11).setCellValue(c.getAdminRemark() != null ? c.getAdminRemark() : "N/A");
                r.createCell(12).setCellValue(c.getCreatedAt() != null ? c.getCreatedAt().format(dtFormatter) : "");
            }

            for (int i = 0; i < cCols.length; i++) {
                compSheet.autoSizeColumn(i);
                compSheet.setColumnWidth(i, compSheet.getColumnWidth(i) + 1200);
            }

            // =========================================================================
            // Sheet 3: Executive Summary Overview
            // =========================================================================
            Sheet execSheet = workbook.createSheet("Executive Summary");
            execSheet.setDisplayGridlines(true);

            Row eTitleRow = execSheet.createRow(0);
            eTitleRow.setHeightInPoints(40);
            Cell eTitleCell = eTitleRow.createCell(0);
            eTitleCell.setCellValue("📊 BRAHMOS BHAWAN - EXECUTIVE SUMMARY OVERVIEW");
            eTitleCell.setCellStyle(titleStyle);
            execSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            int eRowIdx = 2;
            Row eH1 = execSheet.createRow(eRowIdx++);
            Cell ec1 = eH1.createCell(0); ec1.setCellValue("Metric Description"); ec1.setCellStyle(headerStyle);
            Cell ec2 = eH1.createCell(1); ec2.setCellValue("Count / Status"); ec2.setCellStyle(headerStyle);

            Row er1 = execSheet.createRow(eRowIdx++); er1.createCell(0).setCellValue("☀️ Lunch - Veg Count:"); er1.createCell(1).setCellValue(foodSummary.getLunchVegCount()); er1.getCell(0).setCellStyle(vegStyle);
            Row er2 = execSheet.createRow(eRowIdx++); er2.createCell(0).setCellValue("☀️ Lunch - Non-Veg Count:"); er2.createCell(1).setCellValue(foodSummary.getLunchNonVegCount()); er2.getCell(0).setCellStyle(nonVegStyle);
            Row er3 = execSheet.createRow(eRowIdx++); er3.createCell(0).setCellValue("🌙 Dinner - Veg Count:"); er3.createCell(1).setCellValue(foodSummary.getDinnerVegCount()); er3.getCell(0).setCellStyle(vegStyle);
            Row er4 = execSheet.createRow(eRowIdx++); er4.createCell(0).setCellValue("🌙 Dinner - Non-Veg Count:"); er4.createCell(1).setCellValue(foodSummary.getDinnerNonVegCount()); er4.getCell(0).setCellStyle(nonVegStyle);
            Row er5 = execSheet.createRow(eRowIdx++); er5.createCell(0).setCellValue("Total Mess Meals Selected:"); er5.createCell(1).setCellValue(foodSummary.getTotalCount()); er5.getCell(0).setCellStyle(subHeaderStyle);
            
            eRowIdx++; // spacer
            Row er6 = execSheet.createRow(eRowIdx++); er6.createCell(0).setCellValue("Total Complaints Filed:"); er6.createCell(1).setCellValue(complaintStats.getOrDefault("total", 0L)); er6.getCell(0).setCellStyle(subHeaderStyle);
            Row er7 = execSheet.createRow(eRowIdx++); er7.createCell(0).setCellValue("🔴 Pending Complaints:"); er7.createCell(1).setCellValue(complaintStats.getOrDefault("pending", 0L)); er7.getCell(0).setCellStyle(pendingStyle);
            Row er8 = execSheet.createRow(eRowIdx++); er8.createCell(0).setCellValue("🟡 In Progress Complaints:"); er8.createCell(1).setCellValue(complaintStats.getOrDefault("inProgress", 0L)); er8.getCell(0).setCellStyle(progressStyle);
            Row er9 = execSheet.createRow(eRowIdx++); er9.createCell(0).setCellValue("🟢 Resolved Complaints:"); er9.createCell(1).setCellValue(complaintStats.getOrDefault("resolved", 0L)); er9.getCell(0).setCellStyle(vegStyle);
            Row er10 = execSheet.createRow(eRowIdx++); er10.createCell(0).setCellValue("⚪ Rejected Complaints:"); er10.createCell(1).setCellValue(complaintStats.getOrDefault("rejected", 0L)); er10.getCell(0).setCellStyle(nonVegStyle);

            execSheet.autoSizeColumn(0); execSheet.setColumnWidth(0, execSheet.getColumnWidth(0) + 2000);
            execSheet.autoSizeColumn(1); execSheet.setColumnWidth(1, execSheet.getColumnWidth(1) + 2000);

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
