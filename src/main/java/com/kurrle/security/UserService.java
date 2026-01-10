package com.kurrle.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(String firstName, String lastName, String email, String plainPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        String encodedPassword = passwordEncoder.encode(plainPassword);
        User user = new User(firstName, lastName, email, encodedPassword);
        user.setRole(Role.ROLE_USER);
        
        return userRepository.save(user);
    }
}
