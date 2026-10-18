package http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.Headers;
import java.util.Map;

public class Request {

    private final String method;
    private final String path;
    private final Map<String, String> pathParams;
    private final Headers headers;
    private final String body;

    public Request(String method, String path, Map<String, String> pathParams,
                   Headers headers, String body) {
        this.method = method;
        this.path = path;
        this.pathParams = pathParams;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getPathParam(String name) {
        return pathParams.get(name);
    }

    public String getHeader(String name) {
        return headers.getFirst(name);
    }

    public String getBody() {
        return body;
    }

    public <T> T readJson(Class<T> type) throws JsonProcessingException {
        return Json.MAPPER.readValue(body, type);
    }
}