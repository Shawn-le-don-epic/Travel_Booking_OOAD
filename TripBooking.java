import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Scanner;

public class TripBooking {
    private int userId;
    private String source;
    private String destination;
    private String transportMode;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private boolean hasAccommodation;
    private String status;

    // Constructor
    public TripBooking(int userId, String source, String destination, String transportMode, LocalDate departureDate, LocalDate returnDate, boolean hasAccommodation) {
        this.userId = userId;
        this.source = source;
        this.destination = destination;
        this.transportMode = transportMode;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.hasAccommodation = hasAccommodation;
        this.status = "Active";
    }

    public String getDestination() {
        return destination;
    }

    public String getSource() {
        return source;
    }

    public boolean hasAccommodation() {
        return hasAccommodation;
    }

    // Method to book a trip
    public void bookTrip() {
        // Step 1: Get payment details
        Scanner sc = new Scanner(System.in);
        System.out.print("Select payment method (UPI, Credit/Debit Card, Net Banking): ");
        String paymentMethod = sc.nextLine();

        // Step 2: Process payment
        Payment payment = new Payment(paymentMethod);
        if (payment.processPayment()) {
            System.out.println("Payment successful! Booking the trip...");
            // Step 3: Insert trip details into the database
            insertTripDetails();
        } else {
            System.out.println("Payment failed! Try again.");
        }
    }

    // Insert trip details into the database
    protected void insertTripDetails() {
        try {
            // Connection setup
            String url = "jdbc:postgresql://localhost:5432/OOAD";
            String username = "postgres";
            String password = "ShaAric@2024";
            Connection con = DriverManager.getConnection(url, username, password);

            String sql = "INSERT INTO trips (user_id, source, destination, transport_mode, departure_date, return_date, has_accommodation, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, source);
            pstmt.setString(3, destination);
            pstmt.setString(4, transportMode);
            pstmt.setDate(5, Date.valueOf(departureDate));
            pstmt.setDate(6, returnDate != null ? Date.valueOf(returnDate) : null);
            pstmt.setBoolean(7, hasAccommodation); // Using setBoolean for boolean field
            pstmt.setString(8, status);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Test Flag1");

            if (rowsAffected > 0) {
                System.out.println("Trip booked successfully!");
            } else {
                System.out.println("Error occurred while booking the trip.");
            }

            pstmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
