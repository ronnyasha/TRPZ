package ua.kpi.webcrawler.http;

public class HttpClientProxy implements HttpClient {

    private final HttpClient realClient;
    private final String proxyHost;
    private final int proxyPort;

    public HttpClientProxy(HttpClient realClient, String proxyHost, int proxyPort) {
        this.realClient = realClient;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    @Override
    public String get(String url) throws Exception {
        // For demo: just log usage. In real case we would configure system/network proxy.
        System.out.println("[Proxy " + proxyHost + ":" + proxyPort + "] GET " + url);
        return realClient.get(url);
    }
}
