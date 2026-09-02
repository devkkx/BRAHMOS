package com.brahmosbhawan.service;

import com.brahmosbhawan.dto.FoodDtos;
import com.brahmosbhawan.entity.FoodPreference;
import com.brahmosbhawan.entity.MealType;
import com.brahmosbhawan.entity.PreferenceType;
import com.brahmosbhawan.entity.User;
import com.brahmosbhawan.exception.CustomExceptions;
import com.brahmosbhawan.repository.FoodPreferenceRepository;
import com.brahmosbhawan.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodPreferenceService {

    private final FoodPreferenceRepository foodPreferenceRepository;
    private final AuthService authService;

    public FoodPreferenceService(FoodPreferenceRepository foodPreferenceRepository, AuthService authService) {
        this.foodPreferenceRepository = foodPreferenceRepository;
        this.authService = authService;
    }

    @Transactional
    public FoodDtos.StudentPreferenceDto saveOrUpdatePreference(UserPrincipal currentUser, FoodDtos.FoodPreferenceRequest request) {
        User user = authService.getUserByPrincipal(currentUser);
        LocalDate requestDate = request.getDate();

        // Rule 1: Monday Food is Fixed for Everyone
        if (requestDate.getDayOfWeek() == DayOfWeek.MONDAY) {
            throw new CustomExceptions.BadRequestException("Monday meal is fixed and the same for all boarders.");
        }

        // Rule 2: 8-Hour Prior Deadline Validation
        validate8HourDeadline(requestDate, request.getMealType());

        // Check if preference already exists for this (User, Date, MealType)
        Optional<FoodPreference> existingOpt = foodPreferenceRepository
                .findByUserIdAndDateAndMealType(user.getId(), requestDate, request.getMealType());

        FoodPreference preference;
        if (existingOpt.isPresent()) {
            preference = existingOpt.get();
            // Single Edit Lock Enforcement
            if (preference.isLocked() || preference.getEditCount() >= 1) {
                throw new CustomExceptions.BadRequestException("Food preference can only be edited ONCE per meal. Your selection is locked.");
            }
            preference.setFoodPreference(request.getFoodPreference());
            preference.setEditCount(preference.getEditCount() + 1);
            preference.setLocked(true); // Lock immediately after 1 edit!
        } else {
            preference = new FoodPreference(user, requestDate, request.getMealType(), request.getFoodPreference());
            preference.setEditCount(0);
            preference.setLocked(false);
        }

        FoodPreference saved = foodPreferenceRepository.save(preference);
        return convertToDto(saved);
    }

    public List<FoodDtos.StudentPreferenceDto> getStudentMealHistory(UserPrincipal currentUser) {
        User user = authService.getUserByPrincipal(currentUser);
        List<FoodPreference> preferences = foodPreferenceRepository.findByUserIdOrderByDateDescMealTypeAsc(user.getId());
        return preferences.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<FoodDtos.StudentPreferenceDto> getStudentPreferencesForDateRange(UserPrincipal currentUser, LocalDate startDate, LocalDate endDate) {
        User user = authService.getUserByPrincipal(currentUser);
        List<FoodPreference> preferences = foodPreferenceRepository
                .findByUserIdAndDateBetweenOrderByDateAscMealTypeAsc(user.getId(), startDate, endDate);
        return preferences.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Admin APIs: Separate Lunch Veg/Non-Veg & Dinner Veg/Non-Veg Breakdown
    public FoodDtos.FoodSummaryResponse getAdminFoodSummary(LocalDate date, MealType mealType) {
        long lunchVeg = foodPreferenceRepository.countByDateAndMealTypeAndPreference(date, MealType.LUNCH, PreferenceType.VEG);
        long lunchNonVeg = foodPreferenceRepository.countByDateAndMealTypeAndPreference(date, MealType.LUNCH, PreferenceType.NON_VEG);
        
        long dinnerVeg = foodPreferenceRepository.countByDateAndMealTypeAndPreference(date, MealType.DINNER, PreferenceType.VEG);
        long dinnerNonVeg = foodPreferenceRepository.countByDateAndMealTypeAndPreference(date, MealType.DINNER, PreferenceType.NON_VEG);

        List<FoodPreference> list;
        if (mealType != null) {
            list = foodPreferenceRepository.findByDateAndMealType(date, mealType);
        } else {
            list = foodPreferenceRepository.findByDateBetween(date, date);
        }

        List<FoodDtos.StudentPreferenceDto> studentDtos = list.stream().map(this::convertToDto).collect(Collectors.toList());
        String dayName = date.getDayOfWeek().name();

        return new FoodDtos.FoodSummaryResponse(
                date, dayName, mealType,
                lunchVeg, lunchNonVeg,
                dinnerVeg, dinnerNonVeg,
                studentDtos
        );
    }

    public List<FoodDtos.StudentPreferenceDto> getAllPreferencesForAdmin(LocalDate startDate, LocalDate endDate, MealType mealType) {
        List<FoodPreference> list;
        if (startDate != null && endDate != null) {
            list = foodPreferenceRepository.findByDateBetween(startDate, endDate);
        } else if (startDate != null) {
            list = foodPreferenceRepository.findByDateBetween(startDate, startDate);
        } else {
            LocalDate today = LocalDate.now();
            list = foodPreferenceRepository.findByDateBetween(today.minusDays(30), today.plusDays(60));
        }

        if (mealType != null) {
            list = list.stream().filter(fp -> fp.getMealType() == mealType).collect(Collectors.toList());
        }

        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private void validate8HourDeadline(LocalDate targetDate, MealType mealType) {
        LocalDateTime now = LocalDateTime.now();
        // Lunch at 13:00 (1:00 PM), Dinner at 20:00 (8:00 PM)
        LocalTime mealTime = mealType == MealType.LUNCH ? LocalTime.of(13, 0) : LocalTime.of(20, 0);
        LocalDateTime mealDateTime = targetDate.atTime(mealTime);
        LocalDateTime cutoffTime = mealDateTime.minusHours(8);

        if (now.isAfter(cutoffTime)) {
            throw new CustomExceptions.DeadlineExpiredException("Food preference can only be submitted or edited at least 8 hours before the meal (Cutoff was " + cutoffTime.toLocalTime() + "). Submission closed.");
        }
    }

    public FoodDtos.StudentPreferenceDto convertToDto(FoodPreference fp) {
        return new FoodDtos.StudentPreferenceDto(
                fp.getId(),
                fp.getUser().getStudentId(),
                fp.getUser().getName(),
                fp.getUser().getRoomNumber(),
                fp.getUser().getBlock() != null ? fp.getUser().getBlock().name() : "A_BLOCK",
                fp.getDate(),
                fp.getDay().name(),
                fp.getMealType(),
                fp.getFoodPreference(),
                fp.getSubmittedAt(),
                fp.getEditCount(),
                fp.isLocked()
        );
    }
}
