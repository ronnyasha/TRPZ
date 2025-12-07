package ua.kpi.webcrawler.core;

import java.net.URI;
import java.util.*;

public class UrlScheduler {
    private final Queue<String> queue = new ArrayDeque<>();
    private final Set<String> visited = new HashSet<>();
    private final String domainFilter;
    private final int maxDepth;

    public UrlScheduler(String domainFilter, int maxDepth) {
        this.domainFilter = domainFilter;
        this.maxDepth = maxDepth;
    }

    public void addStartUrls(List<String> urls) {
        for (String u : urls) {
            enqueue(u, 0);
        }
    }

    private void enqueue(String url, int depth) {
        if (depth > maxDepth) return;
        if (visited.contains(url)) return;
        if (!matchesDomain(url)) return;
        queue.add(url + "|" + depth);
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

    public String nextUrl() {
        String item = queue.poll();
        if (item == null) return null;
        String[] parts = item.split("\|");
        String url = parts[0];
        int depth = Integer.parseInt(parts[1]);
        visited.add(url);
        return item;
    }

    public void scheduleDiscoveredLinks(List<String> links, int currentDepth) {
        int nextDepth = currentDepth + 1;
        for (String link : links) {
            enqueue(link, nextDepth);
        }
    }
}
