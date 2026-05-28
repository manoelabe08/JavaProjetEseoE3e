package user;

import exceptions.FilePersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldAddAccountAndLoginSuccessfully() {
        AuthService authService = new AuthService();
        Admin admin = new Admin("A1", "Admin", "admin@test.com");

        authService.addAccount(admin, "secret");

        User logged = authService.login("A1", "secret");

        assertNotNull(logged);
        assertEquals("A1", logged.getUID());
    }

    @Test
    void shouldReturnNullWhenLoginFails() {
        AuthService authService = new AuthService();
        Admin admin = new Admin("A1", "Admin", "admin@test.com");
        authService.addAccount(admin, "secret");

        assertNull(authService.login("A1", "wrong"));
        assertNull(authService.login("UNKNOWN", "secret"));
    }

    @Test
    void shouldChangePasswordWhenOldPasswordMatches() {
        AuthService authService = new AuthService();
        Manager manager = new Manager("M1", "Manager", "manager@test.com");
        authService.addAccount(manager, "oldPass");

        boolean changed = authService.changePassword("M1", "oldPass", "newPass");

        assertTrue(changed);
        assertNotNull(authService.login("M1", "newPass"));
        assertNull(authService.login("M1", "oldPass"));
    }

    @Test
    void shouldNotChangePasswordWhenOldPasswordIsWrong() {
        AuthService authService = new AuthService();
        Engineer engineer = new Engineer("E1", "Engineer", "eng@test.com");
        authService.addAccount(engineer, "oldPass");

        boolean changed = authService.changePassword("E1", "badOld", "newPass");

        assertFalse(changed);
        assertNotNull(authService.login("E1", "oldPass"));
    }

    @Test
    void shouldSaveAndLoadAccountsFromFile() throws Exception {
        AuthService authService = new AuthService();
        authService.addAccount(new Admin("A1", "Admin", "admin@test.com"), "pass1");
        authService.addAccount(new Manager("M1", "Manager", "manager@test.com"), "pass2");
        authService.addAccount(new Engineer("E1", "Engineer", "eng@test.com"), "pass3");

        String filePath = tempDir.resolve("users.txt").toString();
        authService.saveAccountsToFile(filePath);

        AuthService loaded = new AuthService();
        loaded.loadAccountsFromFile(filePath);

        assertEquals(3, loaded.getAllAccounts().size());
        assertNotNull(loaded.login("A1", "pass1"));
        assertNotNull(loaded.login("M1", "pass2"));
        assertNotNull(loaded.login("E1", "pass3"));
    }

    @Test
    void shouldThrowWhenLoadingMissingFile() {
        AuthService authService = new AuthService();
        String missingPath = tempDir.resolve("missing.txt").toString();

        assertThrows(FilePersistenceException.class,
                () -> authService.loadAccountsFromFile(missingPath));
    }
}