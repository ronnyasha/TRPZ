package ua.kpi.webcrawler.memento;

/**
 * Lab 6: Checkpoint that allows pausing and resuming a crawl run.
 */
public class CrawlCheckpoint {
    private final Long sessionId;
    private final Long profileId;
    private final SchedulerMemento schedulerMemento;
    private final int pagesProcessed;

    public CrawlCheckpoint(Long sessionId, Long profileId, SchedulerMemento schedulerMemento, int pagesProcessed) {
        this.sessionId = sessionId;
        this.profileId = profileId;
        this.schedulerMemento = schedulerMemento;
        this.pagesProcessed = pagesProcessed;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public SchedulerMemento getSchedulerMemento() {
        return schedulerMemento;
    }

    public int getPagesProcessed() {
        return pagesProcessed;
    }
}
