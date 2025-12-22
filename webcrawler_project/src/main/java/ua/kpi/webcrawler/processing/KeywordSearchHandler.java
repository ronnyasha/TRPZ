package ua.kpi.webcrawler.processing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeywordSearchHandler extends PageHandler {

    private final List<String> keywords;

    public KeywordSearchHandler(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    protected void process(PageProcessingContext ctx) {
        String text = ctx.getPlainText() == null ? "" : ctx.getPlainText().toLowerCase();
        Map<String, Integer> counts = new HashMap<>();
        for (String keyword : keywords) {
            String k = keyword.toLowerCase();
            if (k.isEmpty()) continue;
            int count = 0;
            int index = 0;
            while ((index = text.indexOf(k, index)) != -1) {
                count++;
                index += k.length();
            }
            counts.put(keyword, count);
        }
        ctx.setKeywordCounts(counts);
    }
}
