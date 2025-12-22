package ua.kpi.webcrawler;

import ua.kpi.webcrawler.composite.SiteMapBuilder;
import ua.kpi.webcrawler.core.CrawlerService;
import ua.kpi.webcrawler.core.DefaultContentProcessorFactory;
import ua.kpi.webcrawler.http.HttpClient;
import ua.kpi.webcrawler.http.HttpClientProxy;
import ua.kpi.webcrawler.http.RealHttpClient;
import ua.kpi.webcrawler.model.CrawlProfile;
import ua.kpi.webcrawler.p2p.PeerEndpoint;
import ua.kpi.webcrawler.repository.InMemoryPageRepository;
import ua.kpi.webcrawler.repository.InMemoryProfileRepository;
import ua.kpi.webcrawler.repository.InMemorySessionRepository;
import ua.kpi.webcrawler.template.CrawlRunOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Web Crawler Labs Project (Topic 11: Web crawler)");
        InMemoryProfileRepository profileRepo = new InMemoryProfileRepository();
        InMemorySessionRepository sessionRepo = new InMemorySessionRepository();
        InMemoryPageRepository pageRepo = new InMemoryPageRepository();

        CrawlProfile defaultProfile = new CrawlProfile(
                1L,
                "Default profile",
                List.of("https://example.org/"),
                1,
                "example.org",
                List.of("example", "domain")
        );
        profileRepo.save(defaultProfile);

        HttpClient realClient = new RealHttpClient();
        HttpClient proxyClient = new HttpClientProxy(realClient, "proxy.example.local", 8080);

        CrawlerService crawlerService = new CrawlerService(
                proxyClient,
                new DefaultContentProcessorFactory(),
                profileRepo,
                sessionRepo,
                pageRepo
        );

        // CLI mode (useful for Lab 9 P2P)
        if (args != null && args.length > 0) {
            runCli(args, crawlerService, defaultProfile, pageRepo, sessionRepo);
            return;
        }

        // Interactive menu
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("1. Run crawl (default)");
            System.out.println("2. Run crawl with PAUSE after N pages (Lab 6: Memento)");
            System.out.println("3. Resume paused session by id (Lab 6: Memento)");
            System.out.println("4. Show sessions");
            System.out.println("5. Show site map tree (Lab 8: Composite)");
            System.out.println("6. Run P2P crawl (Lab 9)");
            System.out.println("7. Exit");
            System.out.print("Choose option: ");
            String line = scanner.nextLine();

            if ("1".equals(line)) {
                crawlerService.runProfile(defaultProfile.getId(), CrawlRunOptions.defaults());
            } else if ("2".equals(line)) {
                System.out.print("Pause after how many pages? ");
                int n = Integer.parseInt(scanner.nextLine().trim());
                crawlerService.runProfile(defaultProfile.getId(), new CrawlRunOptions(n, null));
                System.out.println("If paused, use option 3 to resume by session id.");
            } else if ("3".equals(line)) {
                System.out.print("Resume session id: ");
                long sid = Long.parseLong(scanner.nextLine().trim());
                crawlerService.runProfile(defaultProfile.getId(), new CrawlRunOptions(0, sid));
            } else if ("4".equals(line)) {
                System.out.println("Sessions:");
                sessionRepo.findAll().forEach(s -> {
                    System.out.println("Session " + s.getId() + " profile=" + s.getProfileName()
                            + " start=" + s.getStartTime() + " end=" + s.getEndTime()
                            + " status=" + s.getStatus());
                });
            } else if ("5".equals(line)) {
                System.out.print("Session id to build site-map: ");
                long sid = Long.parseLong(scanner.nextLine().trim());
                java.util.List<ua.kpi.webcrawler.model.PageData> pages = pageRepo.findBySessionId(sid);
                if (pages.isEmpty()) {
                    System.out.println("No pages for this session.");
                    continue;
                }
                ua.kpi.webcrawler.composite.SiteGroup root = new SiteMapBuilder().build(pages);
                root.print("");
            } else if ("6".equals(line)) {
                System.out.print("Local port (e.g., 9001): ");
                int port = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Peers (comma separated host:port), e.g., localhost:9002 : ");
                String peersStr = scanner.nextLine().trim();
                List<PeerEndpoint> peers = parsePeers(peersStr);
                crawlerService.runProfileP2P(defaultProfile.getId(), port, peers, CrawlRunOptions.defaults());
            } else if ("7".equals(line)) {
                break;
            }
        }
    }

    private static void runCli(String[] args,
                               CrawlerService crawlerService,
                               CrawlProfile defaultProfile,
                               InMemoryPageRepository pageRepo,
                               InMemorySessionRepository sessionRepo) throws Exception {
        String mode = arg(args, "--mode", "local");
        int pauseAfter = Integer.parseInt(arg(args, "--pauseAfter", "0"));
        String resume = arg(args, "--resumeSession", null);
        Long resumeId = resume == null ? null : Long.parseLong(resume);
        CrawlRunOptions options = new CrawlRunOptions(pauseAfter, resumeId);

        if ("p2p".equalsIgnoreCase(mode)) {
            int port = Integer.parseInt(arg(args, "--port", "9001"));
            List<PeerEndpoint> peers = parsePeers(arg(args, "--peers", ""));
            crawlerService.runProfileP2P(defaultProfile.getId(), port, peers, options);
            return;
        }

        if ("sitemap".equalsIgnoreCase(mode)) {
            long sid = Long.parseLong(arg(args, "--session", "1"));
            java.util.List<ua.kpi.webcrawler.model.PageData> pages = pageRepo.findBySessionId(sid);
            ua.kpi.webcrawler.composite.SiteGroup root = new SiteMapBuilder().build(pages);
            root.print("");
            return;
        }

        if ("sessions".equalsIgnoreCase(mode)) {
            sessionRepo.findAll().forEach(s -> {
                System.out.println("Session " + s.getId() + " status=" + s.getStatus());
            });
            return;
        }

        // default local crawl
        crawlerService.runProfile(defaultProfile.getId(), options);
    }

    private static String arg(String[] args, String key, String def) {
        for (String a : args) {
            if (a.startsWith(key + "=")) {
                return a.substring((key + "=").length());
            }
            if (a.equals(key) && def == null) {
                return null;
            }
        }
        return def;
    }

    private static List<PeerEndpoint> parsePeers(String peersStr) {
        List<PeerEndpoint> peers = new ArrayList<>();
        if (peersStr == null || peersStr.isBlank()) return peers;
        String[] parts = peersStr.split(",");
        for (String p : parts) {
            String trimmed = p.trim();
            if (trimmed.isEmpty()) continue;
            peers.add(PeerEndpoint.parse(trimmed));
        }
        return peers;
    }
}
