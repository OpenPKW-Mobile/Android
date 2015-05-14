package pl.openpkw.openpkwmobile.network;

import java.util.Map;

import pl.openpkw.openpkwmobile.models.CommissionDetails;
import pl.openpkw.openpkwmobile.models.User;
import pl.openpkw.openpkwmobile.models.UserRegister;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
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



    @GET("/available/{email}")
    void checkIsEmailExists(@Path("email") String email, Callback<String> callback);

    @Headers("Content-Type: application/json")
    @POST("/user/register")
    void register(@Header("X-OPW-API-client") String apiClient, @Header("X-OPW-API-token") String apiToken, @Body UserRegister userRegister, Callback<String> callback);
     */
    @GET("/komisja/{pkwId}")
    void getCandidates(@Header("X-OPW-login") String login, @Header("X-OPW-token") String token, @Path("pkwId") String pkwId,
                       Callback<CommissionDetails> callback);

    /**
     * a method to send protocol to backend
     *
     * @param login
     * @param token
     */
    @POST("/komisja/{pkwId}/protokol")
    void submitProtocol(@Header("X-OPW-login") String login, @Header("X-OPW-token") String token,
                        @Path("pkwId") String pkwId, @Body Map<String, Integer> map, Callback<Void> callback);
}
