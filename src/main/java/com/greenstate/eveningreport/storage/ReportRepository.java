package com.greenstate.eveningreport.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.greenstate.eveningreport.domain.FinalReport;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReportRepository {
    private final JsonStorage storage;
    private final Path path;

    public ReportRepository(Path dataDir, JsonStorage storage) {
        this.storage = storage;
        this.path = dataDir.resolve("reports.json");
    }

    private List<FinalReport> all() {
        return storage.read(path, new TypeReference<>() {}, new ArrayList<>());
    }

    public void save(FinalReport report) {
        List<FinalReport> list = all();
        list.add(report);
        storage.writeAtomic(path, list);
    }

    public FinalReport findLastForUser(long userId) {
        return all().stream()
                .filter(r -> r.getTelegramUserId() == userId)
                .max(Comparator.comparing(FinalReport::getCreatedAt))
                .orElse(null);
    }
}
