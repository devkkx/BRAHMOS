package com.brahmosbhawan.repository;

import com.brahmosbhawan.entity.FoodPreference;
import com.brahmosbhawan.entity.MealType;
import com.brahmosbhawan.entity.PreferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodPreferenceRepository extends JpaRepository<FoodPreference, Long> {

    Optional<FoodPreference> findByUserIdAndDateAndMealType(Long userId, LocalDate date, MealType mealType);

    List<FoodPreference> findByUserIdOrderByDateDescMealTypeAsc(Long userId);

    List<FoodPreference> findByUserIdAndDateBetweenOrderByDateAscMealTypeAsc(Long userId, LocalDate startDate, LocalDate endDate);

    List<FoodPreference> findByDateAndMealType(LocalDate date, MealType mealType);

    List<FoodPreference> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(fp) FROM FoodPreference fp WHERE fp.date = :date AND fp.mealType = :mealType AND fp.foodPreference = :preference")
    long countByDateAndMealTypeAndPreference(
            @Param("date") LocalDate date,
            @Param("mealType") MealType mealType,
            @Param("preference") PreferenceType preference
    );

    @Query("SELECT COUNT(fp) FROM FoodPreference fp WHERE fp.date = :date AND fp.foodPreference = :preference")
    long countByDateAndPreference(
            @Param("date") LocalDate date,
            @Param("preference") PreferenceType preference
    );
}
