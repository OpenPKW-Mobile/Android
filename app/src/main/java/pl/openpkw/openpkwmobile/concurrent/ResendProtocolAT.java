package pl.openpkw.openpkwmobile.concurrent;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import pl.openpkw.openpkwmobile.managers.OfflineStorage;
import pl.openpkw.openpkwmobile.models.Protocol;
import pl.openpkw.openpkwmobile.models.User;
import pl.openpkw.openpkwmobile.network.RestClient;

/**
 * Created by michalu on 21.05.15.
 */
public class ResendProtocolAT extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "ResendProtocolAT";
    private Context ctx;
    private List<Protocol> protocols;

    public ResendProtocolAT(Context ctx, List<Protocol> protocols) {
        this.ctx = ctx;
        this.protocols = protocols;
    }

    @Override
    protected Void doInBackground(Void... params) {
        User user = OfflineStorage.getLastLoggedUser(ctx);
        if (ctx == null || user == null || protocols == null)
            cancel(true);
        else {
            try {
                if (!OfflineStorage.isTokenValid(user.getSessionTimeout(), ctx))
                    user = RestClient.get(ctx).login(user.getLogin(), user.getPassword());
                for (Protocol p : protocols) {
                    try {
                        RestClient.get(ctx).submitProtocol(user.getLogin(), user.getToken(), p.getPkwId(), p.getResults());
                        OfflineStorage.removeProtocol(ctx, p);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
        return null;
    }
}
