package pl.openpkw.openpkwmobile.concurrent;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import pl.openpkw.openpkwmobile.models.CommissionDetails;
import pl.openpkw.openpkwmobile.models.User;
import pl.openpkw.openpkwmobile.network.RestClient;

/**
 * AsyncTask dedicated for obtaining commission details including candidates list
 * It communicates with the fragment using {@see GetCommisionDetailsCallback}
 * <p/>
 * Created by michalu on 19.05.15.
 */
public class GetCommissionDetailsAT extends AsyncTask<Void, Void, GetCommissionDetailsAT.ATResult> {

    private Context ctx;
    private WeakReference<GetCommisionDetailsCallback> callback;
    private User user;
    private String pkwId;

    public GetCommissionDetailsAT(Context context, User user, String pkwId, GetCommisionDetailsCallback callback) {
        this.ctx = context;
        this.user = user;
        this.pkwId = pkwId;
        this.callback = new WeakReference<GetCommisionDetailsCallback>(callback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ATResult doInBackground(Void... params) {
        ATResult result = new ATResult();
        CommissionDetails commission = null;
        try {
            commission = RestClient.get(ctx).getCandidates(user.getLogin(), user.getToken(), pkwId);
            result.commissionDetails = commission;
        } catch (Exception e) {
            result.error = e;
        }
        return result;
    }

    @Override
    protected void onPostExecute(ATResult atResult) {
        super.onPostExecute(atResult);
        if (callback != null) {
            if (atResult.commissionDetails != null) {
                callback.get().onSuccess(atResult.commissionDetails);
            } else if (atResult.error != null) {
                callback.get().onError(atResult.error);
            }
        }

    }

    @Override
    protected void onCancelled(ATResult atResult) {
        super.onCancelled(atResult);
    }

    public class ATResult {
        private CommissionDetails commissionDetails;
        private Exception error;
    }
}
