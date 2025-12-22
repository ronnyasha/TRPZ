package ua.kpi.webcrawler.core;

/**
 * A simple DTO for (url, depth) pairs.
 */
public class UrlTask {
    private final String url;
    private final int depth;

    public UrlTask(String url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }
}
