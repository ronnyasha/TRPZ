package ua.kpi.webcrawler.processing;

public class RemoveScriptsHandler extends PageHandler {

    @Override
    protected void process(PageProcessingContext ctx) {
        String html = ctx.getCleanedHtml();
        String cleaned = html.replaceAll("(?is)<script.*?>.*?</script>", "");
        ctx.setCleanedHtml(cleaned);
    }
}
