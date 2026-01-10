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
public class CoffeeBeanService {

    private final CoffeeBeanRepository repository;
    private final AuthenticatedUser authenticatedUser;

    CoffeeBeanService(CoffeeBeanRepository repository, AuthenticatedUser authenticatedUser) {
        this.repository = repository;
        this.authenticatedUser = authenticatedUser;
    }

    private User getCurrentUser() {
        return authenticatedUser.get()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
    }

    @Transactional
    public CoffeeBean createBean(String name, RoastLevel roastLevel, @Nullable String origin, @Nullable String flavorNotes) {
        var bean = new CoffeeBean(name, roastLevel, getCurrentUser());
        bean.setOrigin(origin);
        bean.setFlavorNotes(flavorNotes);
        return repository.saveAndFlush(bean);
    }

    @Transactional
    public CoffeeBean updateBean(Long id, String name, RoastLevel roastLevel, @Nullable String origin, @Nullable String flavorNotes) {
        var bean = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Bean not found: " + id));
        // Verify ownership
        if (!bean.getUser().getId().equals(getCurrentUser().getId())) {
            throw new IllegalArgumentException("Not authorized to update this bean");
        }
        bean.setName(name);
        bean.setRoastLevel(roastLevel);
        bean.setOrigin(origin);
        bean.setFlavorNotes(flavorNotes);
        return repository.saveAndFlush(bean);
    }

    @Transactional
    public void deleteBean(Long id) {
        var bean = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Bean not found: " + id));
        // Verify ownership
        if (!bean.getUser().getId().equals(getCurrentUser().getId())) {
            throw new IllegalArgumentException("Not authorized to delete this bean");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void deactivateBean(Long id) {
        var bean = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Bean not found: " + id));
        // Verify ownership
        if (!bean.getUser().getId().equals(getCurrentUser().getId())) {
            throw new IllegalArgumentException("Not authorized to deactivate this bean");
        }
        bean.setActive(false);
        repository.saveAndFlush(bean);
    }

    @Transactional(readOnly = true)
    public List<CoffeeBean> list(Pageable pageable) {
        return repository.findAllByUser(getCurrentUser(), pageable).toList();
    }

    @Transactional(readOnly = true)
    public List<CoffeeBean> listActive() {
        return repository.findByUserAndActiveTrue(getCurrentUser());
    }

    @Transactional(readOnly = true)
    public List<CoffeeBean> listActive(Pageable pageable) {
        return repository.findByUserAndActiveTrue(getCurrentUser(), pageable).toList();
    }

    @Transactional(readOnly = true)
    public Optional<CoffeeBean> findById(Long id) {
        return repository.findById(id)
                .filter(bean -> bean.getUser().getId().equals(getCurrentUser().getId()));
    }
}
