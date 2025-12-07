package ua.kpi.webcrawler.processing;

public class ExtractTextHandler extends PageHandler {

    @Override
    protected void process(PageProcessingContext ctx) {
        String html = ctx.getCleanedHtml();
        String text = html.replaceAll("(?is)<style.*?>.*?</style>", " ");
        text = text.replaceAll("(?is)<[^>]+>", " ");
        ctx.setPlainText(text);
    }
}
