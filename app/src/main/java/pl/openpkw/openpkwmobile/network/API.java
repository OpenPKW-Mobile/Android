package pl.openpkw.openpkwmobile.network;

import pl.openpkw.openpkwmobile.models.CommissionDetails;
import pl.openpkw.openpkwmobile.models.User;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

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

    /**
     * a method to get detailed information about candidates for given commission
     * @param token
     * @param pkwId
     * @param callback
     * @return a list of detailed data about candidates and the commission
     */
    @GET("/komisja/{pkwId}")
    void getCandidates(@Header("X-OPW-login") String login, @Header("X-OPW-token") String token, @Path("pkwId") String pkwId,
                                    Callback<CommissionDetails> callback);

}
