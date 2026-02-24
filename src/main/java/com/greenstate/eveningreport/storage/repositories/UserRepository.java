package com.greenstate.eveningreport.storage.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.greenstate.eveningreport.domain.EmployeeProfile;
import com.greenstate.eveningreport.storage.JsonStorage;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserRepository {
    private final JsonStorage storage;
    private final Path file;
    private Map<String, EmployeeProfile> users;

    public UserRepository(JsonStorage storage) {
        this.storage = storage;
        this.file = storage.dataFile("users.json");
        this.users = storage.read(file, new TypeReference<>() {}, new HashMap<>());
    }

    public Optional<EmployeeProfile> findByTelegramUserId(Long telegramUserId) {
        return Optional.ofNullable(users.get(String.valueOf(telegramUserId)));
    }

    public void save(EmployeeProfile profile) {
        users.put(String.valueOf(profile.getTelegramUserId()), profile);
        storage.writeAtomic(file, users);
    }
}
