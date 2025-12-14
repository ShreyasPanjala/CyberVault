import java.util.InputMismatchException;
import java.util.Scanner;

public class CyberVault {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        PasswordManager pm = new PasswordManager();

        // ---------- Predefined users & admin----------
        String[] users = { "Samrat", "Sujit" };          // two users
        String[] userPasswords = { "1234", "5678" };  // their passwords

        String adminUser = "sr_230607";   // admin username
        String adminPass = "admin@1234";       // admin password
        // ----------------------------------------------------------------

        // Load persistent data (if any) - handle exceptions inside manager
        pm.loadFromFile();

        System.out.println("=== Welcome to CyberVault ===");
        System.out.println("Login type:");
        System.out.println("1. Admin");
        System.out.println("2. User");
        System.out.print("Choose (1 or 2): ");

        int loginType = 2; // default to user
        try {
            loginType = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Defaulting to User mode.");
        }

        boolean isAdmin = false;
        String currentUser = "guest";

        if (loginType == 1) {
            // Admin login
            System.out.print("Enter admin username: ");
            String aUser = sc.nextLine();
            System.out.print("Enter admin password: ");
            String aPass = sc.nextLine();

            if (aUser.equals(adminUser) && aPass.equals(adminPass)) {
                isAdmin = true;
                currentUser = adminUser;
                System.out.println("Admin login successful.");
            } else {
                System.out.println("Admin login failed. Starting as Guest user.");
                isAdmin = false;
                currentUser = "guest";
            }
        } else {
            // User login
            System.out.print("Enter username: ");
            String u = sc.nextLine();
            System.out.print("Enter password: ");
            String upass = sc.nextLine();

            boolean found = false;
            for (int i = 0; i < users.length; i++) {
                if (users[i].equalsIgnoreCase(u) && userPasswords[i].equals(upass)) {
                    found = true;
                    currentUser = users[i];
                    isAdmin = false;
                    break;
                }
            }
            if (!found) {
                System.out.println("User login failed. Starting as Guest (limited access).");
                currentUser = "guest";
            } else {
                System.out.println("User login successful. Welcome, " + currentUser);
            }
        }

        // ---------- Main menu ----------
        mainLoop:
        while (true) {
            System.out.println("\n=== CyberVault Menu ===");
            System.out.println("1. Add Password");
            System.out.println("2. View Passwords");
            System.out.println("3. Search Password");
            System.out.println("4. Delete Password");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = -1;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                continue;
            }

            switch (choice) {

                case 1:
                    try {
                        System.out.print("Enter account name: ");
                        String account = sc.nextLine();

                        System.out.print("Enter username for the account: ");
                        String accUser = sc.nextLine();

                        System.out.print("Enter password for the account: ");
                        String accPass = sc.nextLine();

                        String ownerForEntry = currentUser;
                        if (isAdmin) {
                            System.out.print("Enter owner for this entry (username): ");
                            String ownerInput = sc.nextLine();
                            // allow admin to assign owner; basic validation:
                            if (ownerInput == null || ownerInput.trim().isEmpty())
                                ownerForEntry = currentUser;
                            else
                                ownerForEntry = ownerInput;
                        }
                        pm.addPassword(account, accUser, accPass, ownerForEntry);

                        // Save after add
                        pm.saveToFile();
                    } catch (IllegalArgumentException iae) {
                        System.out.println("Input error: " + iae.getMessage());
                    } catch (Exception ex) {
                        System.out.println("Unexpected error when adding: " + ex.getMessage());
                    }
                    break;

                case 2:
                    // View passwords
                    pm.viewAll(isAdmin, currentUser);
                    break;

                case 3:
                    System.out.print("Enter account name to search: ");
                    String searchAcc = sc.nextLine();
                    int idx = pm.search(searchAcc, isAdmin, currentUser);

                    if (idx == -1) {
                        System.out.println("Not found or access denied.");
                    } else {
                        PasswordEntry e = pm.getEntry(idx);
                        System.out.println("Account: " + e.getAccount());
                        System.out.println("Username: " + e.getUsername());
                        System.out.println("Owner: " + e.getOwner());
                        if (isAdmin || e.getOwner().equalsIgnoreCase(currentUser)) {
                            System.out.println("Password: " + pm.decrypt(e.getPassword()));
                        } else {
                            System.out.println("Password: ********");
                        }
                    }
                    break;

                case 4:
                    System.out.print("Enter account name to delete: ");
                    String delAcc = sc.nextLine();
                    pm.delete(delAcc, isAdmin, currentUser);
                    // Save after delete
                    pm.saveToFile();
                    break;

                case 5:
                    System.out.println("Saving data and exiting. Goodbye!");
                    pm.saveToFile(); // final save
                    break mainLoop;

                default:
                    System.out.println("Invalid choice. Choose between 1 and 5.");
            }
        }
    }
}
