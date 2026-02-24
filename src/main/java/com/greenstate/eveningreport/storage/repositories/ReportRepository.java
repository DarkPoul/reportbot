package com.greenstate.eveningreport.storage.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.greenstate.eveningreport.domain.FinalReport;
import com.greenstate.eveningreport.storage.JsonStorage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ReportRepository {
    private final JsonStorage storage;
    private final Path file;
    private final List<FinalReport> reports;

    public ReportRepository(JsonStorage storage) {
        this.storage = storage;
        this.file = storage.dataFile("reports.json");
        this.reports = storage.read(file, new TypeReference<>() {}, new ArrayList<>());
    }

    public void save(FinalReport report) {
        reports.add(report);
        storage.writeAtomic(file, reports);
    }

    public Optional<FinalReport> findLastByUser(Long telegramUserId) {
        return reports.stream()
                .filter(r -> telegramUserId.equals(r.getTelegramUserId()))
                .max(Comparator.comparing(FinalReport::getFinalizedAt));
    }
}
