package ua.kpi.webcrawler.core;

import ua.kpi.webcrawler.model.CrawlProfile;
import ua.kpi.webcrawler.processing.*;

public class DefaultContentProcessorFactory {

    public PageHandler buildChain(CrawlProfile profile) {
        PageHandler removeScripts = new RemoveScriptsHandler();
        PageHandler removeAds = new RemoveAdsHandler();
        PageHandler extractText = new ExtractTextHandler();
        PageHandler keywordSearch = new KeywordSearchHandler(profile.getKeywords());
        removeScripts.setNext(removeAds)
                .setNext(extractText)
                .setNext(keywordSearch);
        return removeScripts;
    }
}
