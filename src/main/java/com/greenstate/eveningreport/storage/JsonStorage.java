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
    private final Path dataDir;

    public JsonStorage(Path dataDir) {
        this.dataDir = dataDir;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ensureDataDir();
    }

    public <T> T read(Path file, Class<T> clazz, T defaultValue) {
        try {
            if (Files.notExists(file)) {
                return defaultValue;
            }
            return mapper.readValue(file.toFile(), clazz);
        } catch (IOException e) {
            throw new RuntimeException("Не вдалося прочитати файл: " + file, e);
        }
    }

    public <T> T read(Path file, TypeReference<T> typeReference, T defaultValue) {
        try {
            if (Files.notExists(file)) {
                return defaultValue;
            }
            return mapper.readValue(file.toFile(), typeReference);
        } catch (IOException e) {
            throw new RuntimeException("Не вдалося прочитати файл: " + file, e);
        }
    }

    public void writeAtomic(Path file, Object value) {
        try {
            Files.createDirectories(file.getParent());
            Path temp = file.resolveSibling(file.getFileName() + ".tmp");
            mapper.writerWithDefaultPrettyPrinter().writeValue(temp.toFile(), value);
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException("Не вдалося зберегти файл: " + file, e);
        }
    }

    public Path dataFile(String name) {
        return dataDir.resolve(name);
    }

    private void ensureDataDir() {
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new RuntimeException("Не вдалося створити data директорію", e);
        }
    }
}
