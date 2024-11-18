import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();
        TripDAO tripDAO = new TripDAO();
        boolean isLoggedIn = false;

        try {
            while (true) {
                System.out.println("\nWelcome to the Travel Booking System");
                System.out.println("1. Create New User Account");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1: // Create new user account
                        System.out.print("Enter your username: ");
                        String newUsername = sc.nextLine();

                        System.out.print("Enter your password: ");
                        String newPassword = sc.nextLine();

                        System.out.print("Enter your email: ");
                        String newEmail = sc.nextLine();

                        boolean isUserInserted = userDAO.insertUser(newUsername, newPassword, newEmail);
                        if (isUserInserted) {
                            System.out.println("User account created successfully! Please wait for admin approval.");
                        } else {
                            System.out.println("Error creating user account. Try again.");
                        }
                        break;

                    case 2: // User login
                        System.out.print("Enter your username: ");
                        String username = sc.nextLine();

                        System.out.print("Enter your password: ");
                        String password = sc.nextLine();

                        isLoggedIn = userDAO.loginUser(username, password);
                        if (isLoggedIn) {
                            System.out.println("Login successful! Welcome, " + username + ".");
                            postLoginMenu(sc, tripDAO, username);
                        } else {
                            System.out.println("Invalid credentials or account not yet approved. Please try again.");
                        }
                        break;

                    case 3: // Exit
                        System.out.println("Exiting the system. Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    private static void postLoginMenu(Scanner sc, TripDAO tripDAO, String username) {
        while (true) {
            System.out.println("\nUser Menu:");
            System.out.println("1. Book a Trip");
            System.out.println("2. View Booked Trips");
            System.out.println("3. Cancel a Trip");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1: // Book a trip
                    handleTripBooking(sc, tripDAO, username);
                    break;

                case 2: // View booked trips
                    int userId = getUserIdFromUsername(username);
                    if (userId != -1) {
                        List<String> trips = tripDAO.viewBookedTrips(userId);
                        if (trips.isEmpty()) {
                            System.out.println("No trips booked yet.");
                        } else {
                            System.out.println("\nYour Booked Trips:");
                            trips.forEach(System.out::println);
                        }
                    }
                    break;

                case 3: // Cancel a trip
                    System.out.print("Enter the Trip ID to cancel: ");
                    int tripId = Integer.parseInt(sc.nextLine());
                    boolean isCanceled = tripDAO.cancelTrip(tripId);
                    if (isCanceled) {
                        System.out.println("Trip canceled successfully.");
                    } else {
                        System.out.println("Error canceling trip. Ensure the Trip ID is correct.");
                    }
                    break;

                case 4: // Logout
                    System.out.println("Logged out successfully.");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /*private static void handleTripBooking(Scanner sc, TripDAO tripDAO, String username) {
        try {
            System.out.print("Enter trip source: ");
            String source = sc.nextLine();

            System.out.print("Enter trip destination: ");
            String destination = sc.nextLine();

            System.out.print("Enter mode of transport (e.g., Bus, Train, Flight): ");
            String transportMode = sc.nextLine();

            System.out.print("Enter departure date (yyyy-mm-dd): ");
            LocalDate departureDate = LocalDate.parse(sc.nextLine());

            System.out.print("Do you need accommodation? (true/false): ");
            boolean hasAccommodation = Boolean.parseBoolean(sc.nextLine());

            // Book the trip
            boolean isBooked = tripDAO.bookTrip(username, source, destination, transportMode, java.sql.Date.valueOf(departureDate), null, hasAccommodation);
            if (isBooked) {
                System.out.println("Trip booked successfully!");
            } else {
                System.out.println("Error booking trip. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error during trip booking: " + e.getMessage());
        }
    }*/

    private static void handleTripBooking(Scanner sc, TripDAO tripDAO, String username) {
        try {

            int userId = getUserIdFromUsername(username);
            if (userId == -1) {
                System.out.println("Error: Unable to fetch user ID. Please try again.");
                return;
            }


            System.out.print("Enter trip source: ");
            String source = sc.nextLine();

            System.out.print("Enter trip destination: ");
            String destination = sc.nextLine();

            System.out.print("Enter mode of transport (e.g., Bus, Train, Flight): ");
            String transportMode = sc.nextLine();

            System.out.print("Enter departure date (yyyy-mm-dd): ");
            LocalDate departureDate = LocalDate.parse(sc.nextLine());

            System.out.print("Do you need accommodation? (true/false): ");
            boolean hasAccommodation = Boolean.parseBoolean(sc.nextLine());

            // Book the trip with status "Pending Payment"
            boolean isBooked = tripDAO.bookTrip(username, source, destination, transportMode,
                    java.sql.Date.valueOf(departureDate), null, hasAccommodation);

            if (isBooked) {
                System.out.println("Trip booked successfully with status: 'Pending Payment'.");

                // Fetch the trip ID of the newly booked trip
                int tripId = tripDAO.getLatestTripIdForUserId(userId);
                if (tripId != -1) {
                    // Prompt for payment
                    System.out.println("\nChoose payment method: UPI, Credit/Debit Card, Net Banking");
                    String paymentMethod = sc.nextLine();
                    Payment payment = new Payment(paymentMethod);
                    boolean isPaymentSuccessful = payment.processPayment();

                    if (isPaymentSuccessful) {
                        // Update trip status to "Confirmed"
                        boolean isUpdated = tripDAO.updateTripStatus(tripId, "Confirmed");
                        if (isUpdated) {
                            System.out.println("Trip status updated to 'Confirmed'.");
                        }

                        // Insert payment details into 'payments' table
                        boolean isPaymentInserted = tripDAO.insertPayment(tripId, paymentMethod, payment.getPaymentDetails());
                        if (isPaymentInserted) {
                            System.out.println("Payment recorded successfully!");
                        } else {
                            System.out.println("Error recording payment. Please contact support.");
                        }
                    } else {
                        System.out.println("Payment failed. Trip status remains 'Pending Payment'.");
                    }
                } else {
                    System.out.println("Error: Could not fetch trip details.");
                }
            } else {
                System.out.println("Error booking trip. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error during trip booking: " + e.getMessage());
        }
    }


    // Mock method to simulate fetching user ID from username
    private static int getUserIdFromUsername(String username) {
        final String url = "jdbc:postgresql://localhost:5432/OOAD"; // Replace with your DB name
        final String dbUsername = "postgres"; // Replace with your PostgreSQL username
        final String dbPassword = "ShaAric@2024"; // Replace with your PostgreSQL password
        String sql = "SELECT user_id FROM users WHERE username = '" + username + "';";

        try (Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("user_id");
            } else {
                System.out.println("Error: User not found for username: " + username);
                return -1; // Return an invalid ID if the user is not found
            }

        } catch (Exception e) {
            System.out.println("Database error while fetching user ID: " + e.getMessage());
            return -1; // Return an invalid ID in case of an exception
        }
    }
}
