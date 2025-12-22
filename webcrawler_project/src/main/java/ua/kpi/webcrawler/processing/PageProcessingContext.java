package ua.kpi.webcrawler.processing;

import java.util.HashMap;
import java.util.Map;

public class PageProcessingContext {
    private final String rawHtml;
    private String cleanedHtml;
    private String plainText;
    private Map<String, Integer> keywordCounts = new HashMap<>();

    public PageProcessingContext(String rawHtml) {
        this.rawHtml = rawHtml;
        this.cleanedHtml = rawHtml;
    }

    public String getRawHtml() {
        return rawHtml;
    }

    public String getCleanedHtml() {
        return cleanedHtml;
    }

    public void setCleanedHtml(String cleanedHtml) {
        this.cleanedHtml = cleanedHtml;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public Map<String, Integer> getKeywordCounts() {
        return keywordCounts;
    }

    public void setKeywordCounts(Map<String, Integer> keywordCounts) {
        this.keywordCounts = keywordCounts;
    }
}
