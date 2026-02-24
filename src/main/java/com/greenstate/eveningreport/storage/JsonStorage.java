package com.greenstate.eveningreport.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class JsonStorage {
    private final ObjectMapper mapper;

    public JsonStorage() {
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public <T> T read(Path path, TypeReference<T> type, T defaultValue) {
        try {
            if (!Files.exists(path) || Files.size(path) == 0) {
                return defaultValue;
            }
            return mapper.readValue(path.toFile(), type);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public synchronized void writeAtomic(Path path, Object value) {
        try {
            Files.createDirectories(path.getParent());
            Path temp = path.resolveSibling(path.getFileName() + ".tmp");
            mapper.writeValue(temp.toFile(), value);
            Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException("Не вдалося зберегти файл: " + path, e);
        }
    }
}
