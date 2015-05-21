package pl.openpkw.openpkwmobile.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Bartlomiej 'baslow' Slowik on 2015.05.20.
 */
public class SendImagesReceiver extends BroadcastReceiver {

    private final String tag = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(tag, "alarm received");
    }
}
