package ua.kpi.webcrawler.repository;

import ua.kpi.webcrawler.model.PageData;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryPageRepository implements Repository<PageData, Long> {

    private final Map<Long, PageData> storage = new HashMap<>();
    private long seq = 1L;

    public PageData create(Long sessionId, String url, String plainText, Map<String, Integer> counts) {
        PageData page = new PageData(seq++, sessionId, url, plainText, counts);
        save(page);
        return page;
    }

    @Override
    public void save(PageData entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<PageData> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<PageData> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<PageData> findBySessionId(Long sessionId) {
        return storage.values().stream()
                .filter(p -> p.getSessionId().equals(sessionId))
                .collect(Collectors.toList());
    }
}
