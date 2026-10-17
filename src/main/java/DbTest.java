import java.sql.*;

public class DbTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/mrp";
        String user = "mrp";
        String password = "mrp";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected!");

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
                rs.next();
                System.out.println("Anzahl User: " + rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}