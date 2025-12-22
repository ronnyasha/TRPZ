package ua.kpi.webcrawler.template;

import ua.kpi.webcrawler.core.DefaultContentProcessorFactory;
import ua.kpi.webcrawler.core.UrlScheduler;
import ua.kpi.webcrawler.http.HttpClient;
import ua.kpi.webcrawler.memento.CrawlCheckpointStore;
import ua.kpi.webcrawler.model.CrawlProfile;
import ua.kpi.webcrawler.model.ScanSession;
import ua.kpi.webcrawler.p2p.SocketPeerNode;
import ua.kpi.webcrawler.repository.InMemoryPageRepository;
import ua.kpi.webcrawler.repository.InMemorySessionRepository;

import java.util.List;

/**
 * Lab 9: Template Method extension that adds real P2P URL exchange.
 */
public class P2PCrawlTemplate extends DefaultCrawlTemplate {

    private final SocketPeerNode peerNode;

    public P2PCrawlTemplate(HttpClient httpClient,
                            DefaultContentProcessorFactory processorFactory,
                            InMemorySessionRepository sessionRepo,
                            InMemoryPageRepository pageRepo,
                            CrawlCheckpointStore checkpointStore,
                            SocketPeerNode peerNode) {
        super(httpClient, processorFactory, sessionRepo, pageRepo, checkpointStore);
        this.peerNode = peerNode;
    }

    @Override
    protected void beforeCrawl(CrawlProfile profile, ScanSession session, UrlScheduler scheduler) throws Exception {
        if (peerNode != null) {
            peerNode.start((url, depth) -> {
                scheduler.acceptExternalUrl(url, depth);
                System.out.println("Received URL from peer: " + url + " depth=" + depth);
            });
        }
    }

    @Override
    protected void onLinksDiscovered(CrawlProfile profile, ScanSession session, List<String> links, int depthForLinks) {
        if (peerNode == null) return;
        for (String link : links) {
            peerNode.broadcast(link, depthForLinks);
        }
    }

    @Override
    protected void afterCrawl(CrawlProfile profile, ScanSession session, UrlScheduler scheduler) throws Exception {
        if (peerNode != null) {
            peerNode.stop();
        }
    }
}
