package ua.kpi.webcrawler.model;

import java.util.List;

public class CrawlProfile {
    private Long id;
    private String name;
    private List<String> startUrls;
    private int maxDepth;
    private String domainFilter;
    private List<String> keywords;

    public CrawlProfile(Long id, String name, List<String> startUrls, int maxDepth,
                        String domainFilter, List<String> keywords) {
        this.id = id;
        this.name = name;
        this.startUrls = startUrls;
        this.maxDepth = maxDepth;
        this.domainFilter = domainFilter;
        this.keywords = keywords;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getStartUrls() {
        return startUrls;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public String getDomainFilter() {
        return domainFilter;
    }

    public List<String> getKeywords() {
        return keywords;
    }
}
