package com.greenstate.eveningreport.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.greenstate.eveningreport.domain.EmployeeProfile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final JsonStorage storage;
    private final Path path;

    public UserRepository(Path dataDir, JsonStorage storage) {
        this.storage = storage;
        this.path = dataDir.resolve("users.json");
    }

    private Map<Long, EmployeeProfile> all() {
        return storage.read(path, new TypeReference<>() {}, new HashMap<>());
    }

    public EmployeeProfile findByUserId(long userId) {
        return all().get(userId);
    }

    public void save(EmployeeProfile profile) {
        Map<Long, EmployeeProfile> map = all();
        map.put(profile.getTelegramUserId(), profile);
        storage.writeAtomic(path, map);
    }
}
