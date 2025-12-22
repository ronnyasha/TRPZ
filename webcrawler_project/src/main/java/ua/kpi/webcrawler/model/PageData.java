package ua.kpi.webcrawler.model;

import java.util.Map;

public class PageData {
    private Long id;
    private Long sessionId;
    private String url;
    private String plainText;
    private Map<String, Integer> keywordCounts;

    public PageData(Long id, Long sessionId, String url, String plainText, Map<String, Integer> keywordCounts) {
        this.id = id;
        this.sessionId = sessionId;
        this.url = url;
        this.plainText = plainText;
        this.keywordCounts = keywordCounts;
    }

    public Long getId() {
        return id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public String getUrl() {
        return url;
    }

    public String getPlainText() {
        return plainText;
    }

    public Map<String, Integer> getKeywordCounts() {
        return keywordCounts;
    }
}
