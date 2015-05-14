package pl.openpkw.openpkwmobile.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Wojciech Radzioch on 2015-05-14.
 */
public class EmailValidator {

    public static boolean isEmailValid(String email) {
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
