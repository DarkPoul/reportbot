package esvar.ua.botreport.store;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StoreCatalog {

    private final ShopSheetsRepository repository;

    private volatile Map<String, Store> stores = Map.of();

    public StoreCatalog(ShopSheetsRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        reload();
    }

    public void reload() {
        try {
            stores = repository.loadStores();
            log.info("stores_loaded count={}", stores.size());
        } catch (Exception ex) {
            log.warn("stores_load_failed", ex);
            stores = Map.of();
        }
    }

    public List<String> getKeys() {
        return new ArrayList<>(stores.keySet());
    }

    public Store getByKey(String key) {
        if (key == null) {
            return null;
        }
        return stores.get(key.trim());
    }
}
