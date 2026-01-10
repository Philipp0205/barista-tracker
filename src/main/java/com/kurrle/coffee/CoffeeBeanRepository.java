package com.kurrle.coffee;

import com.kurrle.security.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

interface CoffeeBeanRepository extends JpaRepository<CoffeeBean, Long>, JpaSpecificationExecutor<CoffeeBean> {

    Slice<CoffeeBean> findAllByUser(User user, Pageable pageable);

    List<CoffeeBean> findByUserAndActiveTrue(User user);

    Slice<CoffeeBean> findByUserAndActiveTrue(User user, Pageable pageable);
}
