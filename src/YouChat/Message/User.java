package YouChat.Message;

public class User {
    private String username;
    private String password;
    private String IdNumber;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String IdNumber) {
        this.username = username;
        this.password = password;
        this.IdNumber = IdNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdNumber() {
        return IdNumber;
    }

    public void setIdNumber(String IdNumber) {
        this.IdNumber = IdNumber;
    }

    public String toString() {
        return "User{username = " + username + ", password = " + password + ", IdNumber = " + IdNumber + "}";
    }
}
