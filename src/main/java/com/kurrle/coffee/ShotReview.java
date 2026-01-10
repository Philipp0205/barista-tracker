package com.kurrle.coffee;

import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@Entity
@Table(name = "shot_review")
public class ShotReview {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "review_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shot_id", nullable = false)
    private EspressoShot espressoShot;

    @Enumerated(EnumType.STRING)
    @Column(name = "taste_profile", nullable = false)
    private TasteProfile tasteProfile;

    @Column(name = "notes", length = 500)
    @Nullable
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ShotReview() { // For Hibernate
    }

    public ShotReview(EspressoShot espressoShot, TasteProfile tasteProfile) {
        this.espressoShot = espressoShot;
        this.tasteProfile = tasteProfile;
        this.createdAt = Instant.now();
    }

    public @Nullable Long getId() {
        return id;
    }

    public EspressoShot getEspressoShot() {
        return espressoShot;
    }

    void setEspressoShot(EspressoShot espressoShot) {
        this.espressoShot = espressoShot;
    }

    public TasteProfile getTasteProfile() {
        return tasteProfile;
    }

    public void setTasteProfile(TasteProfile tasteProfile) {
        this.tasteProfile = tasteProfile;
    }

    public @Nullable String getNotes() {
        return notes;
    }

    public void setNotes(@Nullable String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
