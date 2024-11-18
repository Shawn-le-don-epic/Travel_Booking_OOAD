import java.sql.*;
import java.util.Scanner;

public class AdminApprovalTest {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        Scanner sc = new Scanner(System.in);

        try {
            while (true) {
                System.out.println("\nAdmin Menu:");
                System.out.println("1. Approve Users");
                System.out.println("2. Define Available Trip Combinations");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        // Approve users
                        displayPendingApprovals();
                        System.out.print("Enter the username of the user to approve: ");
                        String username = sc.nextLine();

                        boolean isApproved = userDAO.approveUser(username);

                        if (isApproved) {
                            System.out.println("User '" + username + "' approved successfully.");
                        } else {
                            System.out.println("Approval failed. User might not exist or is already approved.");
                        }
                        break;

                    case 2:
                        // Define trip combinations
                        defineTripCombinations(sc);
                        break;

                    case 3:
                        System.out.println("Exiting Admin Menu. Goodbye!");
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

    /**
     * Display users pending admin approval.
     */
    private static void displayPendingApprovals() {
        final String url = "jdbc:postgresql://localhost:5432/OOAD";
        final String dbUsername = "postgres";
        final String dbPassword = "ShaAric@2024";
        String sql = "SELECT username, email FROM users WHERE status = false;";

        try (Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("Pending Approvals:");
            System.out.println("-------------------");
            boolean hasPendingUsers = false;

            while (rs.next()) {
                hasPendingUsers = true;
                String username = rs.getString("username");
                String email = rs.getString("email");
                System.out.println("Username: " + username + ", Email: " + email);
            }

            if (!hasPendingUsers) {
                System.out.println("No users pending admin approval.");
            }
        } catch (Exception e) {
            System.out.println("Database error while fetching pending approvals: " + e.getMessage());
        }
    }

    /**
     * Allows admin to define available trip combinations.
     */
    private static void defineTripCombinations(Scanner sc) {
        final String url = "jdbc:postgresql://localhost:5432/OOAD";
        final String dbUsername = "postgres";
        final String dbPassword = "ShaAric@2024";

        try (Connection con = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            System.out.print("Enter trip source: ");
            String source = sc.nextLine();

            System.out.print("Enter trip destination: ");
            String destination = sc.nextLine();

            System.out.print("Enter mode of transport (e.g., Bus, Train, Flight): ");
            String transportMode = sc.nextLine();

            System.out.print("Enter available date (yyyy-mm-dd): ");
            Date availableDate = Date.valueOf(sc.nextLine());

            String sql = "INSERT INTO available_trips (source, destination, transport_mode, available_date) " +
                    "VALUES (?, ?, ?, ?)";

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, source);
                pst.setString(2, destination);
                pst.setString(3, transportMode);
                pst.setDate(4, availableDate);

                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Trip combination added successfully.");
                } else {
                    System.out.println("Failed to add trip combination. Please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error defining trip combinations: " + e.getMessage());
        }
    }
}
