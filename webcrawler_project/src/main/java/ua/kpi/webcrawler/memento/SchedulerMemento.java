package ua.kpi.webcrawler.memento;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Lab 6: Memento for UrlScheduler state (queued items + set of seen URLs).
 * Immutable.
 */
public class SchedulerMemento {
    private final List<String> queuedItems;
    private final Set<String> seenUrls;

    public SchedulerMemento(List<String> queuedItems, Set<String> seenUrls) {
        this.queuedItems = List.copyOf(queuedItems);
        this.seenUrls = Set.copyOf(seenUrls);
    }

    public List<String> getQueuedItems() {
        return Collections.unmodifiableList(queuedItems);
    }

    public Set<String> getSeenUrls() {
        return Collections.unmodifiableSet(seenUrls);
    }
}
