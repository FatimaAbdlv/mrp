package service;

public class ServiceException extends RuntimeException {

    private final int status;

    private ServiceException(int status, String message) {
        super(message);
        this.status = status;
    }

    public static ServiceException badRequest(String message) {
        return new ServiceException(400, message);
    }

    public static ServiceException unauthorized(String message) {
        return new ServiceException(401, message);
    }

    public static ServiceException forbidden(String message) {
        return new ServiceException(403, message);
    }

    public static ServiceException notFound(String message) {
        return new ServiceException(404, message);
    }

    public static ServiceException conflict(String message) {
        return new ServiceException(409, message);
    }

    public int getStatus() {
        return status;
    }
}