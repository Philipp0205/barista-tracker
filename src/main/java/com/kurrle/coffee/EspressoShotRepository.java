package com.kurrle.coffee;

import com.kurrle.security.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface EspressoShotRepository extends JpaRepository<EspressoShot, Long>, JpaSpecificationExecutor<EspressoShot> {

    Slice<EspressoShot> findAllByUser(User user, Pageable pageable);

    @Query("SELECT s FROM EspressoShot s LEFT JOIN FETCH s.coffeeBean LEFT JOIN FETCH s.review WHERE s.id = :id AND s.user = :user")
    Optional<EspressoShot> findByIdAndUserWithDetails(Long id, User user);

    @Query("SELECT s FROM EspressoShot s LEFT JOIN FETCH s.coffeeBean WHERE s.user = :user ORDER BY s.createdAt DESC")
    Slice<EspressoShot> findAllByUserWithBean(User user, Pageable pageable);
}
