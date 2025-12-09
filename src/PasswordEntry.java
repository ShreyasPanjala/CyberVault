public class PasswordEntry {
    private String account;
    private String username;
    private String password; // stored encrypted
    private String owner;

    public PasswordEntry(String account, String username, String password, String owner) {
        this.account = account;
        this.username = username;
        this.password = password;
        this.owner = owner;
    }

    public String getAccount() {
        return account;

    }
    public String getUsername() {
        return username;
    }

    // returns encrypted password (stored)
    public String getPassword() {
        return password;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return account + " | " + username + " | owner: " + owner;
    }
}
