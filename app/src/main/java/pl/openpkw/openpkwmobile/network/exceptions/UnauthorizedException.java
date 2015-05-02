package pl.openpkw.openpkwmobile.network.exceptions;

import retrofit.RetrofitError;

/**
 * Created by michalu on 01.05.15.
 */
public class UnauthorizedException extends Exception {
    public UnauthorizedException(String message) {
        super(message);
    }
}
