import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class AdminApprovalTest {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        Scanner sc = new Scanner(System.in);

        try {
            // Display users pending admin approval
            displayPendingApprovals();

            // Prompt admin to approve a user
            System.out.print("Enter the username of the user to approve: ");
            String username = sc.nextLine();

            // Approve the selected user
            boolean isApproved = userDAO.approveUser(username);

            if (isApproved) {
                System.out.println("User '" + username + "' approved successfully.");
            } else {
                System.out.println("Approval failed. User might not exist or is already approved.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    /**
     * Display users whose accounts are pending admin approval.
     */
    private static void displayPendingApprovals() {
        final String url = "jdbc:postgresql://localhost:5432/OOAD"; // Replace with your DB name
        final String dbUsername = "postgres"; // Replace with your PostgreSQL username
        final String dbPassword = "ShaAric@2024"; // Replace with your PostgreSQL password
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
}
