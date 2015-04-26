package info.sarihh.antiinferencehub;

import java.net.Socket;
import java.io.ObjectInputStream;

public class ClientHandler extends Thread {

    public ClientHandler(View server, Socket client) {
        this.server = server;
        this.client = client;
        queryAnalyzer = new QueryAnalyzer(server, client);
        start();
    }

    @Override
    public final void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            while (true) {
                try {
                    String query = (String) in.readObject();
                    if (query.equals("QUIT")) {
                        server.removeConnection(client);
                        break;
                    }
                    View.setOutputText("[" + client.getInetAddress().getHostAddress() + "] -> " + query);
                    queryAnalyzer.analyze(query);
                } catch (Exception e) {
                    server.sendTo(e.getMessage(), client);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private View server;
    private Socket client;
    private QueryAnalyzer queryAnalyzer;
}
