package service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TokenService {

    private final Map<String, Integer> tokens = new ConcurrentHashMap<>();

    public String createToken(int userId) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, userId);
        return token;
    }

    public Integer getUserId(String token) {
        return tokens.get(token);
    }
}