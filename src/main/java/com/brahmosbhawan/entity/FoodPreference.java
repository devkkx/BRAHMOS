package com.brahmosbhawan.entity;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_preferences", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_date_meal", columnNames = {"user_id", "date", "meal_type"})
})
public class FoodPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DayOfWeek day;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false, length = 20)
    private MealType mealType;

    @Enumerated(EnumType.STRING)
    @Column(name = "food_preference", nullable = false, length = 20)
    private PreferenceType foodPreference;

    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "edit_count", nullable = false)
    private int editCount = 0;

    @Column(nullable = false)
    private boolean locked = false;

    public FoodPreference() {
    }

    public FoodPreference(User user, LocalDate date, MealType mealType, PreferenceType foodPreference) {
        this.user = user;
        this.date = date;
        this.day = date.getDayOfWeek();
        this.mealType = mealType;
        this.foodPreference = foodPreference;
        this.editCount = 0;
        this.locked = false;
    }

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.date != null) {
            this.day = this.date.getDayOfWeek();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
        if (date != null) {
            this.day = date.getDayOfWeek();
        }
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
