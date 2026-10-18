import com.sun.net.httpserver.HttpServer;
import http.Response;
import http.Router;
import http.UserController;
import repository.UserRepository;
import service.UserService;

import java.net.InetSocketAddress;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        Router router = new Router();


        router.add("GET", "/ping", request -> Response.ok(Map.of("message", "pong")));
        router.add("GET", "/hello/{name}",
                request -> Response.ok(Map.of("greeting", "Hallo " + request.getPathParam("name"))));
        router.add("POST", "/echo", request -> Response.created(request.readJson(Map.class)));


        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);
        router.add("POST", "/api/users/register", userController::register);
        router.add("POST", "/api/users/login", userController::login);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", router);
        server.start();
        System.out.println("Server läuft auf http://localhost:8080");
    }
}