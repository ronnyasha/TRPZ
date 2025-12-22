package ua.kpi.webcrawler.p2p;

/**
 * Simple value object for peer host/port.
 */
public class PeerEndpoint {
    private final String host;
    private final int port;

    public PeerEndpoint(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public static PeerEndpoint parse(String s) {
        String[] parts = s.trim().split(":");
        if (parts.length != 2) throw new IllegalArgumentException("Bad peer endpoint: " + s);
        return new PeerEndpoint(parts[0], Integer.parseInt(parts[1]));
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
