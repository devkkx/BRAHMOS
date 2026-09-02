package com.brahmosbhawan.controller;

import com.brahmosbhawan.entity.ApprovedStudent;
import com.brahmosbhawan.entity.HostelBlock;
import com.brahmosbhawan.exception.CustomExceptions;
import com.brahmosbhawan.repository.ApprovedStudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/api/admin/approved-students")
public class AdminApprovedStudentController {

    private final ApprovedStudentRepository approvedStudentRepository;

    public AdminApprovedStudentController(ApprovedStudentRepository approvedStudentRepository) {
        this.approvedStudentRepository = approvedStudentRepository;
    }

    @GetMapping
    public ResponseEntity<List<ApprovedStudent>> getAllApprovedStudents() {
        return ResponseEntity.ok(approvedStudentRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<ApprovedStudent> addApprovedStudent(@RequestBody ApprovedStudent student) {
        if (student.getEmail() != null) {
            student.setEmail(student.getEmail().trim().toLowerCase());
        }
        ApprovedStudent saved = approvedStudentRepository.save(student);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/upload-excel")
    public ResponseEntity<Map<String, Object>> uploadApprovedStudentsExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomExceptions.BadRequestException("Please upload a valid non-empty Excel file (.xlsx)");
        }

        List<ApprovedStudent> uploadedStudents = new ArrayList<>();
        int addedCount = 0;

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            int nameColIdx = 0;
            int emailColIdx = 1;
            int studentIdColIdx = -1;
            int roomColIdx = -1;
            int blockColIdx = -1;

            boolean hasHeader = false;
            Row firstRow = sheet.getRow(0);
            if (firstRow != null) {
                for (Cell cell : firstRow) {
                    String val = cell.getStringCellValue().trim().toLowerCase();
                    if (val.contains("name")) { nameColIdx = cell.getColumnIndex(); hasHeader = true; }
                    else if (val.contains("mail") || val.contains("email")) { emailColIdx = cell.getColumnIndex(); hasHeader = true; }
                    else if (val.contains("student") || val.contains("id")) { studentIdColIdx = cell.getColumnIndex(); }
                    else if (val.contains("room")) { roomColIdx = cell.getColumnIndex(); }
                    else if (val.contains("block")) { blockColIdx = cell.getColumnIndex(); }
                }
            }

            int startRow = hasHeader ? 1 : 0;
            int autoIdCounter = 101;

            for (int r = startRow; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                Cell nameCell = row.getCell(nameColIdx);
                Cell emailCell = row.getCell(emailColIdx);

                if (nameCell == null || emailCell == null) continue;

                String name = getCellValueAsString(nameCell).trim();
                String email = getCellValueAsString(emailCell).trim().toLowerCase();

                if (name.isEmpty() || email.isEmpty() || !email.contains("@")) continue;

                String studentId = studentIdColIdx >= 0 && row.getCell(studentIdColIdx) != null 
                        ? getCellValueAsString(row.getCell(studentIdColIdx)).trim() 
                        : "ST" + (autoIdCounter++);

                String roomNumber = roomColIdx >= 0 && row.getCell(roomColIdx) != null 
                        ? getCellValueAsString(row.getCell(roomColIdx)).trim() 
                        : "101-A";

                HostelBlock block = HostelBlock.A_BLOCK;
                if (blockColIdx >= 0 && row.getCell(blockColIdx) != null) {
                    String bVal = getCellValueAsString(row.getCell(blockColIdx)).trim().toUpperCase();
                    if (bVal.contains("C")) block = HostelBlock.C_BLOCK;
                }

                // Update or Insert in Whitelist
                Optional<ApprovedStudent> existing = approvedStudentRepository.findByEmail(email);
                ApprovedStudent student;
                if (existing.isPresent()) {
                    student = existing.get();
                    student.setName(name);
                    student.setStudentId(studentId);
                    student.setRoomNumber(roomNumber);
                    student.setBlock(block);
                } else {
                    student = new ApprovedStudent(studentId, name, email, roomNumber, block);
                }

                approvedStudentRepository.save(student);
                uploadedStudents.add(student);
                addedCount++;
            }

        } catch (Exception e) {
            throw new CustomExceptions.BadRequestException("Failed to parse Excel file: " + e.getMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Excel uploaded successfully! Imported " + addedCount + " pre-approved boarders.");
        response.put("importedCount", addedCount);
        response.put("students", uploadedStudents);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, String>> clearAllApprovedStudents() {
        approvedStudentRepository.deleteAllInBatch();
        Map<String, String> resp = new HashMap<>();
        resp.put("message", "All pre-approved boarder whitelist entries cleared.");
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApprovedStudent(@PathVariable Long id) {
        approvedStudentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
