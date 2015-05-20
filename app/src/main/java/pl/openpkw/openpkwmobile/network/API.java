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
     * an asynchronous method to authenticate the user and obtain a token to use for other REST request as X-OPW-token header
     *
     * @param login
     * @param password
     * @param callback
     */
    @GET("/user/login")
    void login(@Header("X-OPW-login") String login, @Header("X-OPW-password") String password,
               Callback<User> callback);

    /**
     * a synchronous method to authenticate the user and obtain a token to use for other REST request as X-OPW-token header
     *
     * @param login
     * @param password
     */
    @GET("/user/login")
    User login(@Header("X-OPW-login") String login, @Header("X-OPW-password") String password);

    @GET("/user/available/{email}")
    void checkIsEmailExists(@Header("X-OPW-API-client") String apiClient, @Header("X-OPW-API-token") String apiToken, @Path("email") String email, Callback<String> callback);

    @Headers("Content-Type: application/json")
    @POST("/user/register")
    void register(@Header("X-OPW-API-client") String apiClient, @Header("X-OPW-API-token") String apiToken, @Body UserRegister userRegister, Callback<String> callback);

    /**
     * an async method to get commission details with candidates list from the API
     *
     * @param login
     * @param token
     * @param pkwId
     * @param callback
     */

    @GET("/komisja/{pkwId}")
    void getCandidates(@Header("X-OPW-login") String login, @Header("X-OPW-token") String token, @Path("pkwId") String pkwId,
                       Callback<CommissionDetails> callback);

    /**
     * a synchronous method to get commission details with candidates list from the API
     *
     * @param login
     * @param token
     * @param pkwId
     * @return
     */
    @GET("/komisja/{pkwId}")
    CommissionDetails getCandidates(@Header("X-OPW-login") String login, @Header("X-OPW-token") String token, @Path("pkwId") String pkwId);

    /**
     * a method to send protocol to backend
     *
     * @param login
     * @param token
     */
    @POST("/komisja/{pkwId}/protokol")
    void submitProtocol(@Header("X-OPW-login") String login, @Header("X-OPW-token") String token,
                        @Path("pkwId") String pkwId, @Body Map<String, Integer> map, Callback<Void> callback);

    /**
     * a synchronous method to send protocol to backend
     *
     * @param login
     * @param token
     */
    @POST("/komisja/{pkwId}/protokol")
    Void submitProtocol(@Header("X-OPW-login") String login, @Header("X-OPW-token") String token,
                        @Path("pkwId") String pkwId, @Body Map<String, Integer> map);
}
