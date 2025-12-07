package ua.kpi.webcrawler.processing;

public class RemoveAdsHandler extends PageHandler {

    @Override
    protected void process(PageProcessingContext ctx) {
        String html = ctx.getCleanedHtml();
        // Very simplified removal of divs with class "ad"
        String cleaned = html.replaceAll("(?is)<div[^>]*class=\"ad\".*?>.*?</div>", "");
        ctx.setCleanedHtml(cleaned);
    }
}
