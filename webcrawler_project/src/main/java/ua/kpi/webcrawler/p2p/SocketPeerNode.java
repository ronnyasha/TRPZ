package ua.kpi.webcrawler.p2p;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Lab 9: real P2P node over TCP sockets.
 * Protocol: each line is "url|depth".
 */
public class SocketPeerNode {

    private final int localPort;
    private final List<PeerEndpoint> peers;

    private volatile boolean running = false;
    private ServerSocket serverSocket;
    private Thread acceptThread;

    public SocketPeerNode(int localPort, List<PeerEndpoint> peers) {
        this.localPort = localPort;
        this.peers = peers;
    }

    public void start(BiConsumer<String, Integer> onUrlReceived) throws Exception {
        if (running) return;
        running = true;
        serverSocket = new ServerSocket(localPort);
        serverSocket.setSoTimeout(1000);

        acceptThread = new Thread(() -> {
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    handleConnection(socket, onUrlReceived);
                } catch (SocketTimeoutException e) {
                    // ignore, loop again
                } catch (Exception e) {
                    if (running) {
                        System.out.println("Peer server error: " + e.getMessage());
                    }
                }
            }
        }, "p2p-accept-" + localPort);
        acceptThread.setDaemon(true);
        acceptThread.start();
        System.out.println("P2P peer started on port " + localPort + ", peers=" + peers);
    }

    private void handleConnection(Socket socket, BiConsumer<String, Integer> onUrlReceived) {
        Thread t = new Thread(() -> {
            try (Socket s = socket;
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length != 2) continue;
                    String url = parts[0];
                    int depth;
                    try {
                        depth = Integer.parseInt(parts[1]);
                    } catch (Exception e) {
                        continue;
                    }
                    onUrlReceived.accept(url, depth);
                }
            } catch (Exception ignored) {
            }
        }, "p2p-conn");
        t.setDaemon(true);
        t.start();
    }

    public void broadcast(String url, int depth) {
        String msg = url + "|" + depth;
        for (PeerEndpoint peer : peers) {
            try (Socket socket = new Socket(peer.getHost(), peer.getPort());
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                out.write(msg);
                out.newLine();
                out.flush();
            } catch (Exception e) {
                // peers may be offline; ignore to keep crawling
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (Exception ignored) {}
        try {
            if (acceptThread != null) acceptThread.join(1500);
        } catch (Exception ignored) {}
        System.out.println("P2P peer stopped on port " + localPort);
    }
}
