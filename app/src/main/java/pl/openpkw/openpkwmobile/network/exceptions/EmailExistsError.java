package pl.openpkw.openpkwmobile.network.exceptions;

/**
 * Created by michalu on 01.05.15.
 */
public class EmailExistsError extends Exception {
    public EmailExistsError(String message) {
        super(message);
    }
}
