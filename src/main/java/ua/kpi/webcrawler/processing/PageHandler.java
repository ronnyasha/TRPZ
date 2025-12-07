package ua.kpi.webcrawler.processing;

public abstract class PageHandler {
    protected PageHandler next;

    public PageHandler setNext(PageHandler next) {
        this.next = next;
        return next;
    }

    public void handle(PageProcessingContext ctx) {
        process(ctx);
        if (next != null) {
            next.handle(ctx);
        }
    }

    protected abstract void process(PageProcessingContext ctx);
}
