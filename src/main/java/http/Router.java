package http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router implements HttpHandler {

    private record Route(String method, String[] segments, Handler handler) {
    }

    private final List<Route> routes = new ArrayList<>();

    public void add(String method, String pattern, Handler handler) {
        routes.add(new Route(method, split(pattern), handler));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Response response;
        try {
            response = route(exchange);
        } catch (JsonProcessingException e) {
            response = Response.error(400, "Ungültiges JSON");
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.error(500, "Interner Serverfehler");
        }
        send(exchange, response);
    }

    private Response route(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathSegments = split(path);
        boolean pathFound = false;

        for (Route route : routes) {
            Map<String, String> params = match(route.segments(), pathSegments);
            if (params == null) {
                continue;
            }
            pathFound = true;
            if (route.method().equals(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8);
                Request request = new Request(method, path, params,
                        exchange.getRequestHeaders(), body);
                return route.handler().handle(request);
            }
        }

        if (pathFound) {
            return Response.error(405, "Methode nicht erlaubt");
        }
        return Response.error(404, "Pfad nicht gefunden");
    }

    private Map<String, String> match(String[] pattern, String[] path) {
        if (pattern.length != path.length) {
            return null;
        }
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i].startsWith("{") && pattern[i].endsWith("}")) {
                params.put(pattern[i].substring(1, pattern[i].length() - 1), path[i]);
            } else if (!pattern[i].equals(path[i])) {
                return null;
            }
        }
        return params;
    }

    private static String[] split(String path) {
        String trimmed = path.replaceAll("^/+|/+$", "");
        return trimmed.isEmpty() ? new String[0] : trimmed.split("/");
    }

    private void send(HttpExchange exchange, Response response) throws IOException {
        if (response.body() == null) {
            exchange.sendResponseHeaders(response.status(), -1);
        } else {
            byte[] bytes = Json.MAPPER.writeValueAsBytes(response.body());
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(response.status(), bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }
}