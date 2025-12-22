package ua.kpi.webcrawler.composite;

/**
 * Lab 8: Component in Composite pattern for representing a site map tree.
 */
public interface SiteComponent {
    String getName();
    int getPageCount();
    void print(String indent);
}
