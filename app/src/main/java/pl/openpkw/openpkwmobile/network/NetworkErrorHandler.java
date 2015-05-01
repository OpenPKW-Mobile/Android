package pl.openpkw.openpkwmobile.network;

import android.content.Context;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.network.exceptions.NoInternetException;
import pl.openpkw.openpkwmobile.network.exceptions.UnauthorizedException;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by michalu on 01.05.15.
 */
public class NetworkErrorHandler implements ErrorHandler {
    private Context ctx;
    public NetworkErrorHandler(Context applicationContext) {
        this.ctx = applicationContext;
    }
    
    @Override
    public Throwable handleError(RetrofitError cause) {
        Response r = cause.getResponse();
        if (r != null && r.getStatus() == 401) {
            return new UnauthorizedException(ctx.getString(R.string.login_error_incorrectloginorpassword));
        } else
            return new NoInternetException(ctx.getString(R.string.login_error_nointernetconnection));
    }
}
