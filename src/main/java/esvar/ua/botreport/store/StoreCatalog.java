package esvar.ua.botreport.store;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StoreCatalog {

    private final ShopSheetsRepository repo;

    private volatile Map<String, Store> stores = Map.of();

    public StoreCatalog(ShopSheetsRepository repo) {
        this.repo = repo;
    }

    @PostConstruct
    public void init() {
        reload();
    }

    public void reload() {
        try {
            this.stores = repo.loadStores();
            log.info("✅ Loaded stores from Google Sheets: {}", stores.keySet());
        } catch (Exception e) {
            log.warn("❗ Failed to load stores from Google Sheets, using empty map", e);
            this.stores = Map.of();
        }
    }

    public List<String> getKeys() {
        return stores.keySet().stream().toList();
    }

    public Store getByKey(String key) {
        if (key == null) return null;
        return stores.get(key.trim());
    }
}