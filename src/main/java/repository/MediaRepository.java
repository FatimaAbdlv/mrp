package repository;

import model.MediaEntry;
import model.MediaType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MediaRepository {

    public MediaEntry save(MediaEntry media) throws SQLException {
        String sql = "INSERT INTO media_entries "
                + "(title, description, media_type, release_year, genres, age_restriction, creator_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, media.getTitle());
            statement.setString(2, media.getDescription());
            statement.setString(3, media.getMediaType().name());
            statement.setInt(4, media.getReleaseYear());
            statement.setString(5, String.join(",", media.getGenres()));
            statement.setInt(6, media.getAgeRestriction());
            statement.setInt(7, media.getCreatorId());
            ResultSet result = statement.executeQuery();
            result.next();
            media.setId(result.getInt(1));
        }
        return media;
    }

    public void update(MediaEntry media) throws SQLException {
        String sql = "UPDATE media_entries SET title = ?, description = ?, media_type = ?, "
                + "release_year = ?, genres = ?, age_restriction = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, media.getTitle());
            statement.setString(2, media.getDescription());
            statement.setString(3, media.getMediaType().name());
            statement.setInt(4, media.getReleaseYear());
            statement.setString(5, String.join(",", media.getGenres()));
            statement.setInt(6, media.getAgeRestriction());
            statement.setInt(7, media.getId());
            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM media_entries WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public List<MediaEntry> findAll() throws SQLException {
        String sql = "SELECT * FROM media_entries ORDER BY id";
        List<MediaEntry> list = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                list.add(toMediaEntry(result));
            }
        }
        return list;
    }

    public MediaEntry findById(int id) throws SQLException {
        String sql = "SELECT * FROM media_entries WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return toMediaEntry(result);
            }
            return null;
        }
    }

    private MediaEntry toMediaEntry(ResultSet result) throws SQLException {
        MediaEntry media = new MediaEntry();
        media.setId(result.getInt("id"));
        media.setTitle(result.getString("title"));
        media.setDescription(result.getString("description"));
        media.setMediaType(MediaType.valueOf(result.getString("media_type")));
        media.setReleaseYear(result.getInt("release_year"));
        String genres = result.getString("genres");
        if (genres != null && !genres.isBlank()) {
            media.setGenres(new ArrayList<>(Arrays.asList(genres.split(","))));
        }
        media.setAgeRestriction(result.getInt("age_restriction"));
        media.setCreatorId(result.getInt("creator_id"));
        return media;
    }
}