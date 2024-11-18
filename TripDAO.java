import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripDAO {
    private final String url = "jdbc:postgresql://localhost:5432/OOAD";
    private final String username = "postgres";
    private final String password = "ShaAric@2024";

    public List<Trip> viewAllTripsByUser(String username) {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT t.trip_id, t.source, t.destination, t.transport_mode, t.departure_date, t.return_date, t.has_accommodation, t.status "
                + "FROM trips t JOIN users u ON t.user_id = u.user_id "
                + "WHERE u.username = '" + username + "' "
                + "ORDER BY t.departure_date;";

        try (Connection con = DriverManager.getConnection(url, this.username, this.password);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Trip trip = new Trip(
                        rs.getInt("trip_id"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getString("transport_mode"),
                        rs.getDate("departure_date"),
                        rs.getDate("return_date"),
                        rs.getBoolean("has_accommodation"),
                        rs.getString("status")
                );
                trips.add(trip);
            }
        } catch (Exception e) {
            System.out.println("Error fetching trips: " + e.getMessage());
        }
        return trips;
    }

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
            System.out.println("Error recording payment: " + e.getMessage());
            return false;
        }
    }

    public int getLatestTripIdForUserId(int userId) {
        final String sql = "SELECT trip_id FROM trips WHERE user_id = ? ORDER BY departure_date DESC LIMIT 1";
        try (Connection con = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("trip_id");
            }
        } catch (Exception e) {
            System.out.println("Error fetching latest trip ID: " + e.getMessage());
        }
        return -1; // Return an invalid ID in case of an error
    }


    public boolean updateTripStatus(int tripId, String status) {
        final String sql = "UPDATE trips SET status = ? WHERE trip_id = ?";
        try (Connection con = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setInt(2, tripId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error updating trip status: " + e.getMessage());
        }
        return false;
    }

    public boolean insertPayment(int tripId, String paymentMethod, String paymentDetails) {
        final String sql = "INSERT INTO payments (trip_id, payment_method, payment_details) VALUES (?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, tripId);
            pst.setString(2, paymentMethod);
            pst.setString(3, paymentDetails);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error inserting payment details: " + e.getMessage());
        }
        return false;
    }

    public boolean bookTripUsingAvailableTripId(String username, int tripId) {
        String sql = "INSERT INTO trips (user_id, source, destination, transport_mode, departure_date, status) " +
                "SELECT u.user_id, a.source, a.destination, a.transport_mode, a.available_date, 'Pending Payment' " +
                "FROM available_trips a, users u WHERE a.trip_id = ? AND u.username = ?";

        try (Connection con = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, tripId);
            pst.setString(2, username);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.out.println("Error booking trip: " + e.getMessage());
            return false;
        }
    }






    public boolean bookTrip(String username, String source, String destination, String transportMode,
                            Date departureDate, Date returnDate, boolean hasAccommodation) {
        String sql = "INSERT INTO trips (user_id, source, destination, transport_mode, departure_date, return_date, has_accommodation, status) "
                + "VALUES ((SELECT user_id FROM users WHERE username = ?), ?, ?, ?, ?, ?, ?, 'Active')";

        try (Connection con = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, source);
            pst.setString(3, destination);
            pst.setString(4, transportMode);
            pst.setDate(5, departureDate);
            pst.setDate(6, returnDate);
            pst.setBoolean(7, hasAccommodation);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Error booking trip: " + e.getMessage());
            return false;
        }
    }

    // Method to cancel a trip
    public boolean cancelTrip(int tripId) {
        String sql = "DELETE FROM trips WHERE trip_id = ?;";

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, tripId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Return true if the trip was successfully deleted

        } catch (Exception e) {
            System.out.println("Error while canceling trip: " + e.getMessage());
            return false; // Return false if an exception occurs
        }
    }

    // Method to retrieve booked trips for a specific user in chronological order
    public List<String> viewBookedTrips(int userId) {
        List<String> trips = new ArrayList<>();
        String sql = "SELECT trip_id, source, destination, departure_date, return_date " +
                "FROM trips WHERE user_id = ? ORDER BY departure_date";

        try (Connection con = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int tripId = rs.getInt("trip_id");
                String source = rs.getString("source");
                String destination = rs.getString("destination");
                String departureDate = rs.getString("departure_date");
                String returnDate = rs.getString("return_date");

                String tripDetails = String.format("Trip ID: %d, From: %s, To: %s, Departure: %s, Return: %s",
                        tripId, source, destination, departureDate, returnDate != null ? returnDate : "N/A");
                trips.add(tripDetails);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving trips: " + e.getMessage());
        }

        return trips;
    }

    public List<String> viewCanceledTrips(int userId) {
        List<String> canceledTrips = new ArrayList<>();
        String sql = "SELECT trip_id, source, destination, departure_date, return_date, cancellation_date " +
                "FROM canceled_trips WHERE user_id = ? ORDER BY departure_date";

        try (Connection con = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int tripId = rs.getInt("trip_id");
                String source = rs.getString("source");
                String destination = rs.getString("destination");
                String departureDate = rs.getString("departure_date");
                String returnDate = rs.getString("return_date");
                String cancellationDate = rs.getString("cancellation_date");

                String tripDetails = String.format("Trip ID: %d, From: %s, To: %s, Departure: %s, Return: %s, Canceled On: %s",
                        tripId, source, destination, departureDate,
                        returnDate != null ? returnDate : "N/A", cancellationDate);
                canceledTrips.add(tripDetails);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving canceled trips: " + e.getMessage());
        }

        return canceledTrips;
    }
}
