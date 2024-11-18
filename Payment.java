import java.util.Scanner;

public class Payment {

    private String paymentMethod;
    private String paymentDetails;

    public Payment(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }


    // Method to initiate payment process
    public boolean processPayment() {
        Scanner sc = new Scanner(System.in);

        switch (paymentMethod) {
            case "UPI":
                System.out.print("Enter your UPI ID: ");
                paymentDetails = sc.nextLine();
                return validateUPI(paymentDetails);

            case "Credit/Debit Card":
                System.out.print("Enter your card number: ");
                paymentDetails = sc.nextLine();
                return validateCardNumber(paymentDetails);

            case "Net Banking":
                System.out.print("Enter your bank account number: ");
                paymentDetails = sc.nextLine();
                return validateBankAccount(paymentDetails);

            default:
                System.out.println("Invalid payment method.");
                return false;
        }
    }

    // Validate UPI ID format
    private boolean validateUPI(String upi) {
        // Simple regex for UPI format (example: user@bank)
        if (upi.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+$")) {
            System.out.println("UPI payment successful!");
            return true;
        } else {
            System.out.println("Invalid UPI ID format.");
            return false;
        }
    }

    // Validate Credit/Debit Card number (example: 16 digits)
    private boolean validateCardNumber(String cardNumber) {
        if (cardNumber.matches("^\\d{16}$")) {
            System.out.println("Card payment successful!");
            return true;
        } else {
            System.out.println("Invalid card number format.");
            return false;
        }
    }

    // Validate Bank Account Number (example: 10-16 digits)
    private boolean validateBankAccount(String accountNumber) {
        if (accountNumber.matches("^\\d{10,16}$")) {
            System.out.println("Net Banking payment successful!");
            return true;
        } else {
            System.out.println("Invalid bank account number format.");
            return false;
        }
    }
}
