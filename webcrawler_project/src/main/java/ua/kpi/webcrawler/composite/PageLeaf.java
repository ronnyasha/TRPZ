package ua.kpi.webcrawler.composite;

/**
 * Leaf node for a single crawled page.
 */
public class PageLeaf implements SiteComponent {

    private final String url;

    public PageLeaf(String url) {
        this.url = url;
    }

    @Override
    public String getName() {
        return url;
    }

    @Override
    public int getPageCount() {
        return 1;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "- " + url);
    }
}
