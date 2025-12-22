package ua.kpi.webcrawler.memento;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lab 6: Caretaker that stores mementos per session.
 * For simplicity we keep it in-memory.
 */
public class SchedulerCaretaker {
    private final Map<Long, SchedulerMemento> storage = new ConcurrentHashMap<>();

    public void save(Long sessionId, SchedulerMemento memento) {
        storage.put(sessionId, memento);
    }

    public SchedulerMemento get(Long sessionId) {
        return storage.get(sessionId);
    }

    public void remove(Long sessionId) {
        storage.remove(sessionId);
    }
}
