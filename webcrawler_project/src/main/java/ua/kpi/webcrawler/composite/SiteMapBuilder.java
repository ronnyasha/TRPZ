package ua.kpi.webcrawler.composite;

import ua.kpi.webcrawler.model.PageData;

import java.net.URI;
import java.util.*;

/**
 * Builds a site-map tree (Composite) from crawled pages.
 */
public class SiteMapBuilder {

    public SiteGroup build(List<PageData> pages) {
        SiteGroup root = new SiteGroup("root");
        Map<String, SiteGroup> hosts = new HashMap<>();

        for (PageData p : pages) {
            String url = p.getUrl();
            try {
                URI uri = new URI(url);
                String host = uri.getHost() == null ? "unknown-host" : uri.getHost();
                SiteGroup hostGroup = hosts.computeIfAbsent(host, h -> {
                    SiteGroup g = new SiteGroup(h);
                    root.add(g);
                    return g;
                });

                String path = uri.getPath() == null ? "" : uri.getPath();
                if (path.isEmpty() || "/".equals(path)) {
                    hostGroup.add(new PageLeaf(url));
                    continue;
                }

                String[] parts = path.split("/");
                SiteGroup current = hostGroup;
                for (String part : parts) {
                    if (part == null || part.isBlank()) continue;
                    SiteGroup next = findOrCreateGroup(current, part);
                    current = next;
                }
                current.add(new PageLeaf(url));
            } catch (Exception e) {
                // fallback
                root.add(new PageLeaf(url));
            }
        }

        return root;
    }

    private SiteGroup findOrCreateGroup(SiteGroup parent, String name) {
        for (SiteComponent c : parent.getChildren()) {
            if (c instanceof SiteGroup && c.getName().equals(name)) {
                return (SiteGroup) c;
            }
        }
        SiteGroup g = new SiteGroup(name);
        parent.add(g);
        return g;
    }
}
