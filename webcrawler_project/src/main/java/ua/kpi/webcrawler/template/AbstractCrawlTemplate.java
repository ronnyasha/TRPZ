package ua.kpi.webcrawler.template;

import ua.kpi.webcrawler.core.UrlScheduler;
import ua.kpi.webcrawler.core.UrlTask;
import ua.kpi.webcrawler.memento.CrawlCheckpoint;
import ua.kpi.webcrawler.memento.CrawlCheckpointStore;
import ua.kpi.webcrawler.model.CrawlProfile;
import ua.kpi.webcrawler.model.PageData;
import ua.kpi.webcrawler.model.ScanSession;
import ua.kpi.webcrawler.repository.InMemoryPageRepository;
import ua.kpi.webcrawler.repository.InMemorySessionRepository;

import java.util.List;

/**
 * Lab 7: Template Method. Defines the skeleton of the crawl algorithm.
 * Lab 6: integrates checkpointing (Memento) via UrlScheduler.
 */
public abstract class AbstractCrawlTemplate {

    protected final InMemorySessionRepository sessionRepo;
    protected final InMemoryPageRepository pageRepo;
    protected final CrawlCheckpointStore checkpointStore;

    protected AbstractCrawlTemplate(InMemorySessionRepository sessionRepo,
                                   InMemoryPageRepository pageRepo,
                                   CrawlCheckpointStore checkpointStore) {
        this.sessionRepo = sessionRepo;
        this.pageRepo = pageRepo;
        this.checkpointStore = checkpointStore;
    }

    /**
     * The template method (final algorithm).
     */
    public final ScanSession execute(CrawlProfile profile, CrawlRunOptions options) throws Exception {
        ScanSession session;
        UrlScheduler scheduler = new UrlScheduler(profile.getDomainFilter(), profile.getMaxDepth());
        int processed = 0;

        if (options != null && options.getResumeSessionId() != null) {
            Long resumeId = options.getResumeSessionId();
            session = sessionRepo.findById(resumeId)
                    .orElseThrow(() -> new IllegalArgumentException("No session found with id=" + resumeId));
            CrawlCheckpoint checkpoint = checkpointStore.load(resumeId);
            if (checkpoint == null) {
                throw new IllegalStateException("No checkpoint found for session id=" + resumeId);
            }
            scheduler.restore(checkpoint.getSchedulerMemento());
            processed = checkpoint.getPagesProcessed();
            session.markResumed();
            sessionRepo.save(session);
        } else {
            session = sessionRepo.createSession(profile.getName());
            scheduler.addStartUrls(profile.getStartUrls());
        }

        beforeCrawl(profile, session, scheduler);

        try {
            UrlTask task;
            while ((task = scheduler.nextTask()) != null) {
                String url = task.getUrl();
                int depth = task.getDepth();

                String html = fetch(url);
                PageData page = processAndPersist(session, profile, url, html);
                processed++;

                List<String> links = discoverLinks(html);
                scheduler.scheduleDiscoveredLinks(links, depth);
                onLinksDiscovered(profile, session, links, depth + 1);

                if (options != null && options.getPauseAfterPages() > 0 && processed >= options.getPauseAfterPages()) {
                    checkpointStore.save(new CrawlCheckpoint(session.getId(), profile.getId(), scheduler.createMemento(), processed));
                    session.markPaused();
                    sessionRepo.save(session);
                    afterCrawl(profile, session, scheduler);
                    return session;
                }
            }

            session.markCompleted();
            sessionRepo.save(session);
            checkpointStore.remove(session.getId());
            afterCrawl(profile, session, scheduler);
            return session;
        } catch (Exception e) {
            session.markFailed();
            sessionRepo.save(session);
            afterCrawl(profile, session, scheduler);
            throw e;
        }
    }

    /**
     * Step 1: fetch HTML by URL.
     */
    protected abstract String fetch(String url) throws Exception;

    /**
     * Step 2-3: process and persist.
     */
    protected abstract PageData processAndPersist(ScanSession session, CrawlProfile profile, String url, String html) throws Exception;

    /**
     * Step 4: discover links.
     */
    protected abstract List<String> discoverLinks(String html);

    /**
     * Hook: additional behavior after link discovery (Lab 9 uses it for broadcast).
     */
    protected void onLinksDiscovered(CrawlProfile profile, ScanSession session, List<String> links, int depthForLinks) {
        // default: no-op
    }

    /**
     * Hook: before crawl start (Lab 9 starts peer server).
     */
    protected void beforeCrawl(CrawlProfile profile, ScanSession session, UrlScheduler scheduler) throws Exception {
        // default: no-op
    }

    /**
     * Hook: after crawl end (Lab 9 stops peer server).
     */
    protected void afterCrawl(CrawlProfile profile, ScanSession session, UrlScheduler scheduler) throws Exception {
        // default: no-op
    }
}
