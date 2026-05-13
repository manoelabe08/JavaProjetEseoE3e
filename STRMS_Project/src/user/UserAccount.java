package user;

public class UserAccount {
    private final User user;
    private final String password;

    public UserAccount(User user, String password) {
        this.user = user;
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    public boolean checkPassword(String inputPassword) {
        return password.equals(inputPassword);
    }
}