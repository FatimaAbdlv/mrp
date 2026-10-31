package http;

import model.MediaEntry;
import service.MediaService;
import service.ServiceException;

public class MediaController {

    private final MediaService mediaService;
    private final AuthHelper authHelper;

    public MediaController(MediaService mediaService, AuthHelper authHelper) {
        this.mediaService = mediaService;
        this.authHelper = authHelper;
    }

    public Response create(Request request) throws Exception {
        Integer userId = authHelper.getUserId(request);
        if (userId == null) {
            return Response.error(401, "Nicht angemeldet");
        }
        MediaEntry body = request.readJson(MediaEntry.class);
        if (body == null) {
            return Response.error(400, "Body fehlt");
        }
        try {
            MediaEntry saved = mediaService.create(body, userId);
            return Response.created(saved);
        } catch (ServiceException e) {
            return Response.error(e.getStatus(), e.getMessage());
        }
    }

    public Response getAll(Request request) throws Exception {
        return Response.ok(mediaService.getAll());
    }

    public Response getById(Request request) throws Exception {
        Integer id = parseId(request);
        if (id == null) {
            return Response.error(400, "Die id muss eine Zahl sein");
        }
        try {
            return Response.ok(mediaService.getById(id));
        } catch (ServiceException e) {
            return Response.error(e.getStatus(), e.getMessage());
        }
    }

    public Response update(Request request) throws Exception {
        Integer userId = authHelper.getUserId(request);
        if (userId == null) {
            return Response.error(401, "Nicht angemeldet");
        }
        Integer id = parseId(request);
        if (id == null) {
            return Response.error(400, "Die id muss eine Zahl sein");
        }
        MediaEntry body = request.readJson(MediaEntry.class);
        if (body == null) {
            return Response.error(400, "Body fehlt");
        }
        try {
            return Response.ok(mediaService.update(id, body, userId));
        } catch (ServiceException e) {
            return Response.error(e.getStatus(), e.getMessage());
        }
    }

    public Response delete(Request request) throws Exception {
        Integer userId = authHelper.getUserId(request);
        if (userId == null) {
            return Response.error(401, "Nicht angemeldet");
        }
        Integer id = parseId(request);
        if (id == null) {
            return Response.error(400, "Die id muss eine Zahl sein");
        }
        try {
            mediaService.delete(id, userId);
            return Response.noContent();
        } catch (ServiceException e) {
            return Response.error(e.getStatus(), e.getMessage());
        }
    }

    private Integer parseId(Request request) {
        try {
            return Integer.parseInt(request.getPathParam("id"));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}