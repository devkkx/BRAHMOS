package com.brahmosbhawan.dto;

import com.brahmosbhawan.entity.MealType;
import com.brahmosbhawan.entity.PreferenceType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FoodDtos {

    public static class FoodPreferenceRequest {
        @NotNull(message = "Date is required")
        private LocalDate date;

        @NotNull(message = "Meal type is required")
        private MealType mealType;

        @NotNull(message = "Food preference is required")
        private PreferenceType foodPreference;

        public FoodPreferenceRequest() {
        }

        public FoodPreferenceRequest(LocalDate date, MealType mealType, PreferenceType foodPreference) {
            this.date = date;
            this.mealType = mealType;
            this.foodPreference = foodPreference;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public MealType getMealType() {
            return mealType;
        }

        public void setMealType(MealType mealType) {
            this.mealType = mealType;
        }

        public PreferenceType getFoodPreference() {
            return foodPreference;
        }

        public void setFoodPreference(PreferenceType foodPreference) {
            this.foodPreference = foodPreference;
        }
    }

    public static class StudentPreferenceDto {
        private Long id;
        private String studentId;
        private String studentName;
        private String roomNumber;
        private String block;
        private LocalDate date;
        private String day;
        private MealType mealType;
        private PreferenceType foodPreference;
        private LocalDateTime submittedAt;
        private int editCount;
        private boolean locked;

        public StudentPreferenceDto() {
        }

        public StudentPreferenceDto(Long id, String studentId, String studentName, String roomNumber, String block,
                                LocalDate date, String day, MealType mealType, PreferenceType foodPreference,
                                LocalDateTime submittedAt, int editCount, boolean locked) {
            this.id = id;
            this.studentId = studentId;
            this.studentName = studentName;
            this.roomNumber = roomNumber;
            this.block = block;
            this.date = date;
            this.day = day;
            this.mealType = mealType;
            this.foodPreference = foodPreference;
            this.submittedAt = submittedAt;
            this.editCount = editCount;
            this.locked = locked;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public void setRoomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
        }

        public String getBlock() {
            return block;
        }

        public void setBlock(String block) {
            this.block = block;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public MealType getMealType() {
            return mealType;
        }

        public void setMealType(MealType mealType) {
            this.mealType = mealType;
        }

        public PreferenceType getFoodPreference() {
            return foodPreference;
        }

        public void setFoodPreference(PreferenceType foodPreference) {
            this.foodPreference = foodPreference;
        }

        public LocalDateTime getSubmittedAt() {
            return submittedAt;
        }

        public void setSubmittedAt(LocalDateTime submittedAt) {
            this.submittedAt = submittedAt;
        }

        public int getEditCount() {
            return editCount;
        }

        public void setEditCount(int editCount) {
            this.editCount = editCount;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }
    }

    public static class FoodSummaryResponse {
        private LocalDate date;
        private String dayName;
        private MealType filterMealType;
        
        // Separate Meal & Preference Breakdown
        private long lunchVegCount;
        private long lunchNonVegCount;
        private long dinnerVegCount;
        private long dinnerNonVegCount;

        private long vegCount;
        private long nonVegCount;
        private long totalCount;

        private List<StudentPreferenceDto> studentPreferences;

        public FoodSummaryResponse() {
        }

        public FoodSummaryResponse(LocalDate date, String dayName, MealType filterMealType,
                                   long lunchVegCount, long lunchNonVegCount,
                                   long dinnerVegCount, long dinnerNonVegCount,
                                   List<StudentPreferenceDto> studentPreferences) {
            this.date = date;
            this.dayName = dayName;
            this.filterMealType = filterMealType;
            this.lunchVegCount = lunchVegCount;
            this.lunchNonVegCount = lunchNonVegCount;
            this.dinnerVegCount = dinnerVegCount;
            this.dinnerNonVegCount = dinnerNonVegCount;
            
            this.vegCount = lunchVegCount + dinnerVegCount;
            this.nonVegCount = lunchNonVegCount + dinnerNonVegCount;
            this.totalCount = this.vegCount + this.nonVegCount;

            this.studentPreferences = studentPreferences;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public String getDayName() {
            return dayName;
        }

        public void setDayName(String dayName) {
            this.dayName = dayName;
        }

        public MealType getFilterMealType() {
            return filterMealType;
        }

        public void setFilterMealType(MealType filterMealType) {
            this.filterMealType = filterMealType;
        }

        public long getLunchVegCount() {
            return lunchVegCount;
        }

        public void setLunchVegCount(long lunchVegCount) {
            this.lunchVegCount = lunchVegCount;
        }

        public long getLunchNonVegCount() {
            return lunchNonVegCount;
        }

        public void setLunchNonVegCount(long lunchNonVegCount) {
            this.lunchNonVegCount = lunchNonVegCount;
        }

        public long getDinnerVegCount() {
            return dinnerVegCount;
        }

        public void setDinnerVegCount(long dinnerVegCount) {
            this.dinnerVegCount = dinnerVegCount;
        }

        public long getDinnerNonVegCount() {
            return dinnerNonVegCount;
        }

        public void setDinnerNonVegCount(long dinnerNonVegCount) {
            this.dinnerNonVegCount = dinnerNonVegCount;
        }

        public long getVegCount() {
            return vegCount;
        }

        public void setVegCount(long vegCount) {
            this.vegCount = vegCount;
        }

        public long getNonVegCount() {
            return nonVegCount;
        }

        public void setNonVegCount(long nonVegCount) {
            this.nonVegCount = nonVegCount;
        }

        public long getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(long totalCount) {
            this.totalCount = totalCount;
        }

        public List<StudentPreferenceDto> getStudentPreferences() {
            return studentPreferences;
        }

        public void setStudentPreferences(List<StudentPreferenceDto> studentPreferences) {
            this.studentPreferences = studentPreferences;
        }
    }
}
