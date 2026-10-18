package repository;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public User save(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?) RETURNING id";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            ResultSet result = statement.executeQuery();
            result.next();
            user.setId(result.getInt(1));
        }
        return user;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash FROM users WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new User(
                        result.getInt("id"),
                        result.getString("username"),
                        result.getString("password_hash"));
            }
            return null;
        }
    }
}