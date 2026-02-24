package com.greenstate.eveningreport.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.greenstate.eveningreport.domain.ReportDraft;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DraftRepository {
    private final JsonStorage storage;
    private final Path path;

    public DraftRepository(Path dataDir, JsonStorage storage) {
        this.storage = storage;
        this.path = dataDir.resolve("drafts.json");
    }

    private Map<Long, ReportDraft> all() {
        return storage.read(path, new TypeReference<>() {}, new HashMap<>());
    }

    public ReportDraft findByUserId(long userId) {
        return all().get(userId);
    }

    public void save(ReportDraft draft) {
        Map<Long, ReportDraft> map = all();
        map.put(draft.getTelegramUserId(), draft);
        storage.writeAtomic(path, map);
    }

    public void delete(long userId) {
        Map<Long, ReportDraft> map = all();
        map.remove(userId);
        storage.writeAtomic(path, map);
    }
}
