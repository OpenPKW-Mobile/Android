package pl.openpkw.openpkwmobile.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.List;

import pl.openpkw.openpkwmobile.concurrent.ResendProtocolAT;
import pl.openpkw.openpkwmobile.managers.OfflineStorage;
import pl.openpkw.openpkwmobile.models.Protocol;

/**
 * Created by michalu on 21.05.15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkStateReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
        boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
        NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

        if (noConnectivity)
            return;
        if (currentNetworkInfo != null && currentNetworkInfo.isConnectedOrConnecting()) {
            sendStoredProtocols(context);
        }
    }

    private void sendStoredProtocols(Context ctx) {
        List<Protocol> protocolList = OfflineStorage.getSavedProtocolsForUpload(ctx);
        if (protocolList == null || protocolList.isEmpty())
            return;
        ResendProtocolAT resendTask = new ResendProtocolAT(ctx,protocolList);
        resendTask.execute();
        Log.d(TAG, "Sending stored protocols");
    }
}
