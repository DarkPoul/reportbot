package com.greenstate.eveningreport.storage.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.greenstate.eveningreport.domain.ReportDraft;
import com.greenstate.eveningreport.storage.JsonStorage;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DraftRepository {
    private final JsonStorage storage;
    private final Path file;
    private Map<String, ReportDraft> drafts;

    public DraftRepository(JsonStorage storage) {
        this.storage = storage;
        this.file = storage.dataFile("drafts.json");
        this.drafts = storage.read(file, new TypeReference<>() {}, new HashMap<>());
    }

    public Optional<ReportDraft> findByTelegramUserId(Long telegramUserId) {
        return Optional.ofNullable(drafts.get(String.valueOf(telegramUserId)));
    }

    public void save(ReportDraft draft) {
        drafts.put(String.valueOf(draft.getTelegramUserId()), draft);
        storage.writeAtomic(file, drafts);
    }

    public void delete(Long telegramUserId) {
        drafts.remove(String.valueOf(telegramUserId));
        storage.writeAtomic(file, drafts);
    }
}
