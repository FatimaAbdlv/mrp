package http;

import service.TokenService;

public class AuthHelper {

    private final TokenService tokenService;

    public AuthHelper(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    // Liest "Authorization: Bearer <token>" und gibt die Benutzer-id zurück.
    // Ist der Header leer oder der Token ungültig, kommt null zurück.
    public Integer getUserId(Request request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        String token = header.substring("Bearer ".length());
        return tokenService.getUserId(token);
    }
}