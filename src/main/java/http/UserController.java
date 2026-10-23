package http;

import model.User;
import service.ServiceException;
import service.UserService;

import java.util.Map;

public class UserController {

    private final UserService userService;
    private final AuthHelper authHelper;

    public UserController(UserService userService, AuthHelper authHelper) {
        this.userService = userService;
        this.authHelper = authHelper;
    }

    public Response register(Request request) throws Exception {
        AuthRequest body = request.readJson(AuthRequest.class);
        if (body == null) {
            return Response.error(400, "Body fehlt");
        }
        try {
            User user = userService.register(body.getUsername(), body.getPassword());
            return Response.created(Map.of("id", user.getId(), "username", user.getUsername()));
        } catch (ServiceException e) {
            return Response.error(e.getStatus(), e.getMessage());
        }
    }

    public Response login(Request request) throws Exception {
        AuthRequest body = request.readJson(AuthRequest.class);
        if (body == null) {
            return Response.error(400, "Body fehlt");
        }
        try {
            String token = userService.login(body.getUsername(), body.getPassword());
            return Response.ok(Map.of("token", token));
        } catch (ServiceException e) {
            return Response.error(e.getStatus(), e.getMessage());
        }
    }

    public Response me(Request request) {
        Integer userId = authHelper.getUserId(request);
        if (userId == null) {
            return Response.error(401, "Nicht angemeldet");
        }
        return Response.ok(Map.of("userId", userId));
    }
}