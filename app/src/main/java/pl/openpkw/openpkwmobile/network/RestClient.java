package pl.openpkw.openpkwmobile.network;

import android.content.Context;
import com.squareup.okhttp.OkHttpClient;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**The REST client
 * Created by michalu on 01.05.15.
 */
public class RestClient {

    private static API REST_CLIENT;
    private static String ROOT =
            "https://k.otwartapw.pl/opw/service";


    private RestClient() {
    }

    public static API get(Context ctx) {
        return setupRestClient(ctx);
    }

    private static API setupRestClient(Context ctx) {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                request.addHeader("User-Agent", "OpenPKWmobile-androidApp");
                request.addHeader("Accept", "application/json");
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()))
                .setRequestInterceptor(requestInterceptor)
                .setErrorHandler(new NetworkErrorHandler(ctx))
                .build();

        REST_CLIENT = restAdapter.create(API.class);
        return REST_CLIENT;
    }
}