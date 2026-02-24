package com.greenstate.eveningreport.service;

import com.greenstate.eveningreport.domain.EmployeeProfile;
import com.greenstate.eveningreport.storage.UserRepository;

public class RegistrationService {
    private final UserRepository userRepository;

    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isRegistered(long userId) {
        return userRepository.findByUserId(userId) != null;
    }

    public EmployeeProfile getProfile(long userId) {
        return userRepository.findByUserId(userId);
    }

    public void saveProfile(EmployeeProfile profile) {
        userRepository.save(profile);
    }
}
