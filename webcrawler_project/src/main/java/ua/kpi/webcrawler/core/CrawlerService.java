package ua.kpi.webcrawler.core;

import ua.kpi.webcrawler.http.HttpClient;
import ua.kpi.webcrawler.model.CrawlProfile;
import ua.kpi.webcrawler.model.ScanSession;
import ua.kpi.webcrawler.memento.InMemoryCrawlCheckpointStore;
import ua.kpi.webcrawler.p2p.PeerEndpoint;
import ua.kpi.webcrawler.p2p.SocketPeerNode;
import ua.kpi.webcrawler.repository.InMemoryPageRepository;
import ua.kpi.webcrawler.repository.InMemoryProfileRepository;
import ua.kpi.webcrawler.repository.InMemorySessionRepository;
import ua.kpi.webcrawler.template.CrawlRunOptions;
import ua.kpi.webcrawler.template.DefaultCrawlTemplate;
import ua.kpi.webcrawler.template.P2PCrawlTemplate;

import java.util.List;

public class CrawlerService {

    private final HttpClient httpClient;
    private final DefaultContentProcessorFactory processorFactory;
    private final InMemoryProfileRepository profileRepo;
    private final InMemorySessionRepository sessionRepo;
    private final InMemoryPageRepository pageRepo;

    // Lab 6 checkpoint store (in-memory)
    private final InMemoryCrawlCheckpointStore checkpointStore = new InMemoryCrawlCheckpointStore();

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

    public ScanSession runProfile(Long profileId) throws Exception {
        return runProfile(profileId, CrawlRunOptions.defaults());
    }

    public ScanSession runProfile(Long profileId, CrawlRunOptions options) throws Exception {
        CrawlProfile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found id=" + profileId));

        DefaultCrawlTemplate template = new DefaultCrawlTemplate(
                httpClient,
                processorFactory,
                sessionRepo,
                pageRepo,
                checkpointStore
        );
        return template.execute(profile, options);
    }

    public ScanSession runProfileP2P(Long profileId,
                                    int localPort,
                                    List<PeerEndpoint> peers,
                                    CrawlRunOptions options) throws Exception {
        CrawlProfile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found id=" + profileId));

        SocketPeerNode node = new SocketPeerNode(localPort, peers);

        P2PCrawlTemplate template = new P2PCrawlTemplate(
                httpClient,
                processorFactory,
                sessionRepo,
                pageRepo,
                checkpointStore,
                node
        );

        return template.execute(profile, options);
    }

    public InMemoryCrawlCheckpointStore getCheckpointStore() {
        return checkpointStore;
    }
}
