package pl.openpkw.openpkwmobile.network;

import pl.openpkw.openpkwmobile.models.User;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;

/**
 * Created by michalu on 01.05.15.
 */
public interface API {
    /**
     * a method to authenticate the user and obtain a token to use for other REST request as X-OPW-token header
     *
     * @param login
     * @param password
     * @param callback
     */
    @GET("/user/login")
    void login(@Header("X-OPW-login") String login, @Header("X-OPW-password") String password,
               Callback<User> callback);
}
