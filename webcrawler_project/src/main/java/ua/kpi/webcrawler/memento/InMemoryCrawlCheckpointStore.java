package ua.kpi.webcrawler.memento;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory checkpoint store.
 */
public class InMemoryCrawlCheckpointStore implements CrawlCheckpointStore {
    private final Map<Long, CrawlCheckpoint> checkpoints = new ConcurrentHashMap<>();

    @Override
    public void save(CrawlCheckpoint checkpoint) {
        checkpoints.put(checkpoint.getSessionId(), checkpoint);
    }

    @Override
    public CrawlCheckpoint load(Long sessionId) {
        return checkpoints.get(sessionId);
    }

    @Override
    public void remove(Long sessionId) {
        checkpoints.remove(sessionId);
    }
}
