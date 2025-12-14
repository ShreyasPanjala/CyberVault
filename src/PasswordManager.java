import java.io.*;

public class PasswordManager {

    private PasswordEntry[] entries = new PasswordEntry[500];
    private int count = 0;
    private final String FILENAME = "vaultdata.txt";

    // ---------- Encryption (Caesar shift +3) ----------
    public String encrypt(String pass) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pass.length(); i++) {
            char c = pass.charAt(i);
            c += 3;
            sb.append(c);
        }
        return sb.toString();
    }

    // ---------- Decryption (Caesar shift -3) ----------
    public String decrypt(String encrypted) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < encrypted.length(); i++) {
            char c = encrypted.charAt(i);
            c -= 3;
            sb.append(c);
        }
        return sb.toString();
    }

    // Basic validation: disallow commas (we use comma-separated file)
    private void validateField(String s, String fieldName) {
        if (s == null) throw new IllegalArgumentException(fieldName + " cannot be null");
        if (s.contains(",")) throw new IllegalArgumentException(fieldName + " cannot contain comma (,)");
        if (s.trim().isEmpty()) throw new IllegalArgumentException(fieldName + " cannot be empty");
    }

    // Add password for a specific owner (owner = username or "admin")
    public void addPassword(String account, String username, String password, String owner) {
        validateField(account, "Account");
        validateField(username, "Username");
        validateField(password, "Password");
        validateField(owner, "Owner");

        if (count >= entries.length) {
            System.out.println("Storage full. Cannot add more entries.");
            return;
        }
        String encrypted = encrypt(password);
        entries[count] = new PasswordEntry(account, username, encrypted, owner);
        count++;
        System.out.println("Password stored securely for owner: " + owner);
    }

    // View entries: admin sees all, user sees own only
    public void viewAll(boolean isAdmin, String currentUser) {
        if (count == 0) {
            System.out.println("No passwords stored yet.");
            return;
        }
        boolean printed = false;
        for (int i = 0; i < count; i++) {
            PasswordEntry e = entries[i];
            if (isAdmin || e.getOwner().equalsIgnoreCase(currentUser)) {
                printed = true;
                System.out.println("------------------------------");
                System.out.println("Account: " + e.getAccount());
                System.out.println("Username: " + e.getUsername());
                System.out.println("Owner: " + e.getOwner());
                // decrypted view allowed only for admin or owner
                System.out.println("Password: " + (isAdmin || e.getOwner().equalsIgnoreCase(currentUser)
                        ? decrypt(e.getPassword()) : "********"));
            }
        }
        if (!printed) {
            System.out.println("No entries available for user: " + currentUser);
        }
        System.out.println("------------------------------");
    }

    // Search returns index or -1; respects ownership unless admin
    public int search(String account, boolean isAdmin, String currentUser) {
        for (int i = 0; i < count; i++) {
            PasswordEntry e = entries[i];
            if (e.getAccount().equalsIgnoreCase(account)) {
                if (isAdmin || e.getOwner().equalsIgnoreCase(currentUser)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public PasswordEntry getEntry(int index) {
        if (index < 0 || index >= count) return null;
        return entries[index];
    }

    // Delete: admin can delete any; user only own entries
    public void delete(String account, boolean isAdmin, String currentUser) {
        int index = -1;
        for (int i = 0; i < count; i++) {
            PasswordEntry e = entries[i];
            if (e.getAccount().equalsIgnoreCase(account)) {
                if (isAdmin || e.getOwner().equalsIgnoreCase(currentUser)) {
                    index = i;
                    break;
                }
            }
        }
        if (index == -1) {
            System.out.println("No matching entry found (or access denied).");
            return;
        }
        // shift left
        for (int i = index; i < count - 1; i++) {
            entries[i] = entries[i + 1];
        }
        entries[count - 1] = null;
        count--;
        System.out.println("Deleted successfully.");
    }

    // ---------------- File Persistence ----------------
    // Save all entries (encrypted) to vaultdata.txt
    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {
            for (int i = 0; i < count; i++) {
                PasswordEntry e = entries[i];
                // CSV: account,username,encryptedPassword,owner
                String line = e.getAccount() + "," + e.getUsername() + "," + e.getPassword() + "," + e.getOwner();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Load entries from file on startup
    public void loadFromFile() {
        File f = new File(FILENAME);
        if (!f.exists()) {
            // No data yet, start fresh
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                // split into 4 parts (account, username, encryptedPass, owner)
                String[] parts = line.split(",", 4);
                if (parts.length < 4) {
                    // malformed line, skip
                    System.out.println("Skipping malformed data line.");
                    continue;
                }
                String account = parts[0];
                String username = parts[1];
                String encrypted = parts[2];
                String owner = parts[3];
                // Basic validation: avoid loading if array full
                if (count < entries.length) {
                    entries[count] = new PasswordEntry(account, username, encrypted, owner);
                    count++;
                } else {
                    System.out.println("Storage full while loading file. Some entries skipped.");
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            // not expected as we checked exists(), but handle anyway
            System.out.println("Data file not found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("Error reading data file: " + e.getMessage());
        }
    }

    public int getCount() { return count; }
}
