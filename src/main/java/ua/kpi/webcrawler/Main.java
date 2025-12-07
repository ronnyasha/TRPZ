package ua.kpi.webcrawler;

import ua.kpi.webcrawler.core.CrawlerService;
import ua.kpi.webcrawler.core.DefaultContentProcessorFactory;
import ua.kpi.webcrawler.http.HttpClient;
import ua.kpi.webcrawler.http.HttpClientProxy;
import ua.kpi.webcrawler.http.RealHttpClient;
import ua.kpi.webcrawler.model.CrawlProfile;
import ua.kpi.webcrawler.repository.InMemoryPageRepository;
import ua.kpi.webcrawler.repository.InMemoryProfileRepository;
import ua.kpi.webcrawler.repository.InMemorySessionRepository;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Web Crawler Labs Project (Topic 11: Web crawler)");
        InMemoryProfileRepository profileRepo = new InMemoryProfileRepository();
        InMemorySessionRepository sessionRepo = new InMemorySessionRepository();
        InMemoryPageRepository pageRepo = new InMemoryPageRepository();

        // Create default profile
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

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("1. Run crawl with default profile");
            System.out.println("2. Show sessions");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            String line = scanner.nextLine();
            if ("1".equals(line)) {
                crawlerService.runProfile(defaultProfile.getId());
            } else if ("2".equals(line)) {
                System.out.println("Sessions:");
                sessionRepo.findAll().forEach(s -> {
                    System.out.println("Session " + s.getId() + " profile=" + s.getProfileName()
                            + " status=" + s.getStatus());
                });
            } else if ("3".equals(line)) {
                break;
            }
        }
        System.out.println("Bye.");
    }
}
