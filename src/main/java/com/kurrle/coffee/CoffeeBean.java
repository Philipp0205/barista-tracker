package com.kurrle.coffee;

import com.kurrle.security.User;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@Entity
@Table(name = "coffee_bean")
public class CoffeeBean {

    public static final int NAME_MAX_LENGTH = 100;
    public static final int ORIGIN_MAX_LENGTH = 100;
    public static final int FLAVOR_NOTES_MAX_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "bean_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = NAME_MAX_LENGTH)
    private String name = "";

    @Column(name = "origin", length = ORIGIN_MAX_LENGTH)
    @Nullable
    private String origin;

    @Enumerated(EnumType.STRING)
    @Column(name = "roast_level", nullable = false)
    private RoastLevel roastLevel = RoastLevel.MEDIUM;

    @Column(name = "flavor_notes", length = FLAVOR_NOTES_MAX_LENGTH)
    @Nullable
    private String flavorNotes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    protected CoffeeBean() { // For Hibernate
    }

    public CoffeeBean(String name, RoastLevel roastLevel, User user) {
        this.name = name;
        this.roastLevel = roastLevel;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Name length exceeds " + NAME_MAX_LENGTH);
        }
        this.name = name;
    }

    public @Nullable String getOrigin() {
        return origin;
    }

    public void setOrigin(@Nullable String origin) {
        if (origin != null && origin.length() > ORIGIN_MAX_LENGTH) {
            throw new IllegalArgumentException("Origin length exceeds " + ORIGIN_MAX_LENGTH);
        }
        this.origin = origin;
    }

    public RoastLevel getRoastLevel() {
        return roastLevel;
    }

    public void setRoastLevel(RoastLevel roastLevel) {
        this.roastLevel = roastLevel;
    }

    public @Nullable String getFlavorNotes() {
        return flavorNotes;
    }

    public void setFlavorNotes(@Nullable String flavorNotes) {
        if (flavorNotes != null && flavorNotes.length() > FLAVOR_NOTES_MAX_LENGTH) {
            throw new IllegalArgumentException("Flavor notes length exceeds " + FLAVOR_NOTES_MAX_LENGTH);
        }
        this.flavorNotes = flavorNotes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return name + (origin != null ? " (" + origin + ")" : "");
    }
}
