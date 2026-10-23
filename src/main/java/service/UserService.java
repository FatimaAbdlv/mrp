package service;

import model.User;
import repository.UserRepository;

import java.sql.SQLException;

public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public User register(String username, String password) throws SQLException {
        checkNotEmpty(username, password);
        if (userRepository.findByUsername(username) != null) {
            throw new ServiceException(409, "Username ist schon vergeben");
        }
        User user = new User(null, username, PasswordHasher.hash(password));
        return userRepository.save(user);
    }

    public String login(String username, String password) throws SQLException {
        checkNotEmpty(username, password);
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPasswordHash().equals(PasswordHasher.hash(password))) {
            throw new ServiceException(401, "Username oder Passwort falsch");
        }
        return tokenService.createToken(user.getId());
    }

    private void checkNotEmpty(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ServiceException(400, "Username und Passwort sind Pflicht");
        }
    }
}