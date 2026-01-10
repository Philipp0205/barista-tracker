package com.kurrle.coffee;

import com.kurrle.security.AuthenticatedUser;
import com.kurrle.security.User;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EspressoShotService {

    private final EspressoShotRepository shotRepository;
    private final CoffeeBeanRepository beanRepository;
    private final ShotReviewRepository reviewRepository;
    private final AuthenticatedUser authenticatedUser;

    EspressoShotService(EspressoShotRepository shotRepository, CoffeeBeanRepository beanRepository, 
                        ShotReviewRepository reviewRepository, AuthenticatedUser authenticatedUser) {
        this.shotRepository = shotRepository;
        this.beanRepository = beanRepository;
        this.reviewRepository = reviewRepository;
        this.authenticatedUser = authenticatedUser;
    }

    private User getCurrentUser() {
        return authenticatedUser.get()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
    }

    @Transactional
    public EspressoShot createShot(Double grindSize, Double grindAmount, Double yield, Integer extractionTime, @Nullable Long beanId) {
        User user = getCurrentUser();
        var shot = new EspressoShot(grindSize, grindAmount, yield, extractionTime, user);
        if (beanId != null) {
            beanRepository.findById(beanId)
                    .filter(bean -> bean.getUser().getId().equals(user.getId()))
                    .ifPresent(shot::setCoffeeBean);
        }
        return shotRepository.saveAndFlush(shot);
    }

    @Transactional
    public EspressoShot updateShot(Long id, Double grindSize, Double grindAmount, Double yield, Integer extractionTime, @Nullable Long beanId) {
        User user = getCurrentUser();
        var shot = shotRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Shot not found: " + id));
        // Verify ownership
        if (!shot.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not authorized to update this shot");
        }
        shot.setGrindSize(grindSize);
        shot.setGrindAmount(grindAmount);
        shot.setYield(yield);
        shot.setExtractionTime(extractionTime);
        if (beanId != null) {
            beanRepository.findById(beanId)
                    .filter(bean -> bean.getUser().getId().equals(user.getId()))
                    .ifPresent(shot::setCoffeeBean);
        } else {
            shot.setCoffeeBean(null);
        }
        return shotRepository.saveAndFlush(shot);
    }

    @Transactional
    public void deleteShot(Long id) {
        var shot = shotRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Shot not found: " + id));
        // Verify ownership
        if (!shot.getUser().getId().equals(getCurrentUser().getId())) {
            throw new IllegalArgumentException("Not authorized to delete this shot");
        }
        shotRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EspressoShot> list(Pageable pageable) {
        return shotRepository.findAllByUserWithBean(getCurrentUser(), pageable).toList();
    }

    @Transactional(readOnly = true)
    public Optional<EspressoShot> findById(Long id) {
        return shotRepository.findById(id)
                .filter(shot -> shot.getUser().getId().equals(getCurrentUser().getId()));
    }

    @Transactional(readOnly = true)
    public Optional<EspressoShot> findByIdWithDetails(Long id) {
        return shotRepository.findByIdAndUserWithDetails(id, getCurrentUser());
    }

    @Transactional
    public ShotReview reviewShot(Long shotId, TasteProfile tasteProfile, @Nullable String notes) {
        User user = getCurrentUser();
        var shot = shotRepository.findById(shotId).orElseThrow(() -> new IllegalArgumentException("Shot not found: " + shotId));
        // Verify ownership
        if (!shot.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not authorized to review this shot");
        }
        
        // Remove existing review if present
        if (shot.getReview() != null) {
            reviewRepository.delete(shot.getReview());
        }
        
        var review = new ShotReview(shot, tasteProfile);
        review.setNotes(notes);
        shot.setReview(review);
        shotRepository.saveAndFlush(shot);
        return review;
    }

    @Transactional(readOnly = true)
    public Optional<ShotReview> findReviewByShot(Long shotId) {
        return shotRepository.findById(shotId)
                .filter(shot -> shot.getUser().getId().equals(getCurrentUser().getId()))
                .map(EspressoShot::getReview);
    }
}
