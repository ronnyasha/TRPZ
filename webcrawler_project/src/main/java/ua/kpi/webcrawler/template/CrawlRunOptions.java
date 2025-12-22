package ua.kpi.webcrawler.template;

/**
 * Options for running a crawl.
 */
public class CrawlRunOptions {
    private final int pauseAfterPages;     // 0 = no pause
    private final Long resumeSessionId;    // null = new run

    public CrawlRunOptions(int pauseAfterPages, Long resumeSessionId) {
        this.pauseAfterPages = pauseAfterPages;
        this.resumeSessionId = resumeSessionId;
    }

    public int getPauseAfterPages() {
        return pauseAfterPages;
    }

    public Long getResumeSessionId() {
        return resumeSessionId;
    }

    public static CrawlRunOptions defaults() {
        return new CrawlRunOptions(0, null);
    }
}
