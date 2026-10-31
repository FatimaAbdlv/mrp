import com.sun.net.httpserver.HttpServer;
import http.AuthHelper;
import http.MediaController;
import http.Response;
import http.Router;
import http.UserController;
import repository.MediaRepository;
import repository.UserRepository;
import service.MediaService;
import service.TokenService;
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
        TokenService tokenService = new TokenService();
        UserService userService = new UserService(userRepository, tokenService);
        AuthHelper authHelper = new AuthHelper(tokenService);
        UserController userController = new UserController(userService, authHelper);
        router.add("POST", "/api/users/register", userController::register);
        router.add("POST", "/api/users/login", userController::login);
        router.add("GET", "/api/me", userController::me);

        MediaRepository mediaRepository = new MediaRepository();
        MediaService mediaService = new MediaService(mediaRepository);
        MediaController mediaController = new MediaController(mediaService, authHelper);
        router.add("POST", "/api/media", mediaController::create);
        router.add("GET", "/api/media", mediaController::getAll);
        router.add("GET", "/api/media/{id}", mediaController::getById);
        router.add("PUT", "/api/media/{id}", mediaController::update);
        router.add("DELETE", "/api/media/{id}", mediaController::delete);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", router);
        server.start();
        System.out.println("Server läuft auf http://localhost:8080");
    }
}