package com.greenstate.eveningreport.service;

import com.greenstate.eveningreport.domain.EmployeeProfile;
import com.greenstate.eveningreport.storage.repositories.UserRepository;

import java.util.Optional;

public class RegistrationService {
    private final UserRepository userRepository;

    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<EmployeeProfile> findProfile(Long userId) {
        return userRepository.findByTelegramUserId(userId);
    }

    public void save(EmployeeProfile profile) {
        userRepository.save(profile);
    }
}
