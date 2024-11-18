import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserDAO {
    private final String url = "jdbc:postgresql://localhost:5432/OOAD"; // Replace with your DB name
    private final String username = "postgres"; // Replace with your PostgreSQL username
    private final String password = "ShaAric@2024"; // Replace with your PostgreSQL password

    public boolean insertUser(String username, String password, String email) {
        String sql = "INSERT INTO users (username, password, email) VALUES ('" + username + "', '" + password + "', '" + email + "');";

        try (Connection con = DriverManager.getConnection(url, this.username, this.password);
             Statement st = con.createStatement()) {

            int rowsAffected = st.executeUpdate(sql);  // Execute the insert operation
            return rowsAffected > 0;  // Return true if insertion was successful

        } catch (Exception e) {
            System.out.println("Error inserting user: " + e.getMessage());
            return false;
        }
    }

    public boolean loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "' AND status = true;";

        try (Connection con = DriverManager.getConnection(url, this.username, this.password);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            return rs.next();  // Returns true if there is a result, meaning login credentials are valid and user is approved

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }

    public boolean approveUser(String username) {
        String sql = "UPDATE users SET status = true WHERE username = '" + username + "';";

        try (Connection con = DriverManager.getConnection(url, this.username, this.password);
             Statement st = con.createStatement()) {

            int rowsUpdated = st.executeUpdate(sql);  // Execute the update operation
            return rowsUpdated > 0;  // Return true if at least one row was updated

        } catch (Exception e) {
            System.out.println("Approval error: " + e.getMessage());
            return false;
        }
    }

}
