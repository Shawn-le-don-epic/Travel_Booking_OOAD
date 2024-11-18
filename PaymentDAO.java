import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class PaymentDAO {
    private final String url = "jdbc:postgresql://localhost:5432/OOAD";
    private final String username = "postgres";
    private final String password = "ShaAric@2024";

    public boolean makePayment(int tripId, String paymentMethod, String paymentDetails) {
        String sql = "INSERT INTO payments (trip_id, payment_method, payment_details) VALUES (?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, tripId);
            pst.setString(2, paymentMethod);
            pst.setString(3, paymentDetails);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Error making payment: " + e.getMessage());
            return false;
        }
    }
}
