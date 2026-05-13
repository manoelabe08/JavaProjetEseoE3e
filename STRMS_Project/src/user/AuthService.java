package user;

import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private final List<UserAccount> accounts;

    public AuthService() {
        this.accounts = new ArrayList<>();
    }

    public void addAccount(User user, String password) {
        accounts.add(new UserAccount(user, password));
    }

    public User login(String id, String password) {
        for (UserAccount account : accounts) {
            if (account.getUser().getUID().equals(id) && account.checkPassword(password)) {
                return account.getUser();
            }
        }
        return null;
    }
}