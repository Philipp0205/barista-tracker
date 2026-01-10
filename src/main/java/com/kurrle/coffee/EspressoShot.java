package com.kurrle.coffee;

import com.kurrle.security.User;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@Entity
@Table(name = "espresso_shot")
public class EspressoShot {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "shot_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bean_id")
    @Nullable
    private CoffeeBean coffeeBean;

    @Column(name = "grind_size", nullable = false)
    private Double grindSize; // Grind setting (e.g., 1-40 or similar scale)

    @Column(name = "grind_amount", nullable = false)
    private Double grindAmount; // Coffee amount in grams (dose)

    @Column(name = "yield", nullable = false)
    private Double yield; // Output in grams

    @Column(name = "extraction_time", nullable = false)
    private Integer extractionTime; // Time in seconds

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "notes", length = 500)
    @Nullable
    private String notes;

    @OneToOne(mappedBy = "espressoShot", cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    private ShotReview review;

    protected EspressoShot() { // For Hibernate
    }

    public EspressoShot(Double grindSize, Double grindAmount, Double yield, Integer extractionTime, User user) {
        this.grindSize = grindSize;
        this.grindAmount = grindAmount;
        this.yield = yield;
        this.extractionTime = extractionTime;
        this.user = user;
        this.createdAt = Instant.now();
    }

    public @Nullable Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public @Nullable CoffeeBean getCoffeeBean() {
        return coffeeBean;
    }

    public void setCoffeeBean(@Nullable CoffeeBean coffeeBean) {
        this.coffeeBean = coffeeBean;
    }

    public Double getGrindSize() {
        return grindSize;
    }

    public void setGrindSize(Double grindSize) {
        this.grindSize = grindSize;
    }

    public Double getGrindAmount() {
        return grindAmount;
    }

    public void setGrindAmount(Double grindAmount) {
        this.grindAmount = grindAmount;
    }

    public Double getYield() {
        return yield;
    }

    public void setYield(Double yield) {
        this.yield = yield;
    }

    public Integer getExtractionTime() {
        return extractionTime;
    }

    public void setExtractionTime(Integer extractionTime) {
        this.extractionTime = extractionTime;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public @Nullable String getNotes() {
        return notes;
    }

    public void setNotes(@Nullable String notes) {
        this.notes = notes;
    }

    public @Nullable ShotReview getReview() {
        return review;
    }

    public void setReview(@Nullable ShotReview review) {
        this.review = review;
        if (review != null) {
            review.setEspressoShot(this);
        }
    }

    /**
     * Calculate the brew ratio (yield / dose)
     */
    public double getBrewRatio() {
        return yield / grindAmount;
    }
}
