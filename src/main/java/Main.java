import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/ping", exchange -> {
            byte[] antwort = "pong".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, antwort.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(antwort);
            }
        });

        server.start();
        System.out.println("Server läuft auf http://localhost:8080/ping");
    }
}