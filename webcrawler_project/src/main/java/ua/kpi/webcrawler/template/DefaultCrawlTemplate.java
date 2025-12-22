package ua.kpi.webcrawler.template;

import ua.kpi.webcrawler.core.DefaultContentProcessorFactory;
import ua.kpi.webcrawler.http.HttpClient;
import ua.kpi.webcrawler.model.CrawlProfile;
import ua.kpi.webcrawler.model.PageData;
import ua.kpi.webcrawler.model.ScanSession;
import ua.kpi.webcrawler.processing.PageHandler;
import ua.kpi.webcrawler.processing.PageProcessingContext;
import ua.kpi.webcrawler.memento.CrawlCheckpointStore;
import ua.kpi.webcrawler.repository.InMemoryPageRepository;
import ua.kpi.webcrawler.repository.InMemorySessionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default crawl implementation using Template Method (Lab 7).
 */
public class DefaultCrawlTemplate extends AbstractCrawlTemplate {

    protected final HttpClient httpClient;
    protected final DefaultContentProcessorFactory processorFactory;

    public DefaultCrawlTemplate(HttpClient httpClient,
                               DefaultContentProcessorFactory processorFactory,
                               InMemorySessionRepository sessionRepo,
                               InMemoryPageRepository pageRepo,
                               CrawlCheckpointStore checkpointStore) {
        super(sessionRepo, pageRepo, checkpointStore);
        this.httpClient = httpClient;
        this.processorFactory = processorFactory;
    }

    @Override
    protected String fetch(String url) throws Exception {
        System.out.println("Fetching " + url);
        return httpClient.get(url);
    }

    @Override
    protected PageData processAndPersist(ScanSession session, CrawlProfile profile, String url, String html) throws Exception {
        PageHandler chain = processorFactory.buildChain(profile);
        PageProcessingContext ctx = new PageProcessingContext(html);
        chain.handle(ctx);

        PageData pageData = pageRepo.create(session.getId(), url, ctx.getPlainText(), ctx.getKeywordCounts());
        System.out.println("Saved page " + pageData.getId() + " keywordStats=" + pageData.getKeywordCounts());
        return pageData;
    }

    @Override
    protected List<String> discoverLinks(String html) {
        List<String> links = new ArrayList<>();
        Pattern p = Pattern.compile("href=\"(http[s]?://[^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(html);
        while (m.find()) {
            links.add(m.group(1));
        }
        return links;
    }
}
