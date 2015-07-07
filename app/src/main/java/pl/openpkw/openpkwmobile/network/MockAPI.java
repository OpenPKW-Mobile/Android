package pl.openpkw.openpkwmobile.network;

import pl.openpkw.openpkwmobile.models.User;

/**
 * Created by michalu on 07.07.15.
 */
public class MockAPI {
    public static User mockUser() {
        User user = new User();
        user.setFirstname("Jan");
        user.setLastname("Kowalski");
        user.setToken("1234567890987654321");
        user.setSessionActive(true);
        user.setId("1");
        return user;
    }
}
