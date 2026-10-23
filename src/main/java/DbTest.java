import java.sql.*;

public class DbTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/mrp";
        String user = "mrp";
        String password = "mrp";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected!");

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users")) {
                resultSet.next();
                System.out.println("Amount of Users: " + resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}