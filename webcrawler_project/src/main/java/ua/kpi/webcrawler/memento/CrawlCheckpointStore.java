package ua.kpi.webcrawler.memento;

/**
 * Storage for crawl checkpoints.
 */
public interface CrawlCheckpointStore {
    void save(CrawlCheckpoint checkpoint);
    CrawlCheckpoint load(Long sessionId);
    void remove(Long sessionId);
}
