package ua.kpi.webcrawler.composite;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite node (group/folder).
 */
public class SiteGroup implements SiteComponent {

    private final String name;
    private final List<SiteComponent> children = new ArrayList<>();

    public SiteGroup(String name) {
        this.name = name;
    }

    public SiteGroup add(SiteComponent child) {
        children.add(child);
        return this;
    }

    public List<SiteComponent> getChildren() {
        return children;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPageCount() {
        int sum = 0;
        for (SiteComponent c : children) {
            sum += c.getPageCount();
        }
        return sum;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "+ " + name + " (" + getPageCount() + " pages)");
        for (SiteComponent c : children) {
            c.print(indent + "  ");
        }
    }
}
