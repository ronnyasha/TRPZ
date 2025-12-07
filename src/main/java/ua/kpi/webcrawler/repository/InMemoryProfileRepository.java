package ua.kpi.webcrawler.repository;

import ua.kpi.webcrawler.model.CrawlProfile;

import java.util.*;

public class InMemoryProfileRepository implements Repository<CrawlProfile, Long> {

    private final Map<Long, CrawlProfile> storage = new HashMap<>();

    @Override
    public void save(CrawlProfile entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<CrawlProfile> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<CrawlProfile> findAll() {
        return new ArrayList<>(storage.values());
    }
}
