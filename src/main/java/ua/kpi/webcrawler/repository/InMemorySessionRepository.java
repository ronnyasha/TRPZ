package ua.kpi.webcrawler.repository;

import ua.kpi.webcrawler.model.ScanSession;

import java.util.*;

public class InMemorySessionRepository implements Repository<ScanSession, Long> {

    private final Map<Long, ScanSession> storage = new HashMap<>();
    private long seq = 1L;

    public ScanSession createSession(String profileName) {
        ScanSession session = new ScanSession(seq++, profileName, java.time.LocalDateTime.now());
        save(session);
        return session;
    }

    @Override
    public void save(ScanSession entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<ScanSession> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<ScanSession> findAll() {
        return new ArrayList<>(storage.values());
    }
}
