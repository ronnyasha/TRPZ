package ua.kpi.webcrawler.core;

import ua.kpi.webcrawler.http.HttpClient;
import ua.kpi.webcrawler.model.CrawlProfile;
import ua.kpi.webcrawler.model.PageData;
import ua.kpi.webcrawler.model.ScanSession;
import ua.kpi.webcrawler.processing.PageHandler;
import ua.kpi.webcrawler.processing.PageProcessingContext;
import ua.kpi.webcrawler.repository.InMemoryPageRepository;
import ua.kpi.webcrawler.repository.InMemoryProfileRepository;
import ua.kpi.webcrawler.repository.InMemorySessionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerService {

    private final HttpClient httpClient;
    private final DefaultContentProcessorFactory processorFactory;
    private final InMemoryProfileRepository profileRepo;
    private final InMemorySessionRepository sessionRepo;
    private final InMemoryPageRepository pageRepo;

    public CrawlerService(HttpClient httpClient,
                          DefaultContentProcessorFactory processorFactory,
                          InMemoryProfileRepository profileRepo,
                          InMemorySessionRepository sessionRepo,
                          InMemoryPageRepository pageRepo) {
        this.httpClient = httpClient;
        this.processorFactory = processorFactory;
        this.profileRepo = profileRepo;
        this.sessionRepo = sessionRepo;
        this.pageRepo = pageRepo;
    }

    public void runProfile(Long profileId) {
        CrawlProfile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + profileId));
        ScanSession session = sessionRepo.createSession(profile.getName());
        System.out.println("Started session " + session.getId() + " for profile " + profile.getName());

        UrlScheduler scheduler = new UrlScheduler(profile.getDomainFilter(), profile.getMaxDepth());
        scheduler.addStartUrls(profile.getStartUrls());

        try {
            String item;
            while ((item = scheduler.nextUrl()) != null) {
                String[] parts = item.split("\|");
                String url = parts[0];
                int depth = Integer.parseInt(parts[1]);
                System.out.println("Fetching " + url + " depth=" + depth);
                String html;
                try {
                    html = httpClient.get(url);
                } catch (Exception e) {
                    System.out.println("Failed to fetch " + url + ": " + e.getMessage());
                    continue;
                }

                PageHandler chain = processorFactory.buildChain(profile);
                PageProcessingContext ctx = new PageProcessingContext(html);
                chain.handle(ctx);

                PageData pageData = pageRepo.create(session.getId(), url, ctx.getPlainText(), ctx.getKeywordCounts());
                System.out.println("Saved page " + pageData.getId() + " keywordStats=" + pageData.getKeywordCounts());

                List<String> links = extractLinks(html);
                scheduler.scheduleDiscoveredLinks(links, depth);
            }
            session.markCompleted();
            System.out.println("Session " + session.getId() + " completed.");
        } catch (Exception e) {
            session.markFailed();
            System.out.println("Session " + session.getId() + " failed: " + e.getMessage());
        }
    }

    private List<String> extractLinks(String html) {
        List<String> links = new ArrayList<>();
        Pattern p = Pattern.compile("href=\"(http[s]?://[^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(html);
        while (m.find()) {
            links.add(m.group(1));
        }
        return links;
    }
}
