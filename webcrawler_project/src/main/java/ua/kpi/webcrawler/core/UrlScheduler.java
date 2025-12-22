package ua.kpi.webcrawler.core;

import ua.kpi.webcrawler.memento.SchedulerMemento;

import java.net.URI;
import java.util.*;

/**
 * Schedules URLs for crawling with simple domain filtering and depth limiting.
 * Updated for labs 6-9: thread-safe operations + Memento support + external URL acceptance for P2P.
 */
public class UrlScheduler {

    private final Queue<UrlTask> queue = new ArrayDeque<>();
    private final Set<String> seen = new HashSet<>();

    private final String domainFilter;
    private final int maxDepth;

    public UrlScheduler(String domainFilter, int maxDepth) {
        this.domainFilter = domainFilter;
        this.maxDepth = maxDepth;
    }

    public synchronized void addStartUrls(List<String> urls) {
        for (String url : urls) {
            enqueue(url, 0);
        }
    }

    public synchronized UrlTask nextTask() {
        return queue.poll();
    }

    /**
     * Backward compatible API used in earlier labs.
     */
    public synchronized String nextUrl() {
        UrlTask task = nextTask();
        if (task == null) return null;
        return task.getUrl() + "|" + task.getDepth();
    }

    public synchronized void scheduleDiscoveredLinks(List<String> links, int currentDepth) {
        int nextDepth = currentDepth + 1;
        for (String link : links) {
            enqueue(link, nextDepth);
        }
    }

    /**
     * Lab 9: accept URLs from other peers.
     */
    public synchronized void acceptExternalUrl(String url, int depth) {
        enqueue(url, depth);
    }

    /**
     * Lab 6: save internal scheduler state.
     */
    public synchronized SchedulerMemento createMemento() {
        List<String> queued = new ArrayList<>();
        for (UrlTask t : queue) {
            queued.add(t.getUrl() + "|" + t.getDepth());
        }
        return new SchedulerMemento(queued, new HashSet<>(seen));
    }

    /**
     * Lab 6: restore internal scheduler state from memento.
     */
    public synchronized void restore(SchedulerMemento memento) {
        queue.clear();
        seen.clear();
        seen.addAll(memento.getSeenUrls());

        for (String item : memento.getQueuedItems()) {
            String[] parts = item.split("\\|");
            if (parts.length != 2) continue;
            String url = parts[0];
            int depth;
            try {
                depth = Integer.parseInt(parts[1]);
            } catch (Exception e) {
                continue;
            }
            queue.add(new UrlTask(url, depth));
        }
    }

    private void enqueue(String url, int depth) {
        if (url == null) return;
        if (depth > maxDepth) return;
        if (!matchesDomain(url)) return;
        if (seen.add(url)) {
            queue.add(new UrlTask(url, depth));
        }
    }

    private boolean matchesDomain(String urlStr) {
        try {
            URI uri = new URI(urlStr);
            String host = uri.getHost();
            if (host == null) return false;
            return host.contains(domainFilter);
        } catch (Exception e) {
            return false;
        }
    }
}
