package pl.openpkw.openpkwmobile.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Bartlomiej 'baslow' Slowik on 2015.05.20.
 */
public class SendImagesLaterReceiver extends BroadcastReceiver {

    private final String tag = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        File outDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] list = outDir.listFiles();
        if (list.length > 0) {
            for (File dir : list) {
                if (dir.isDirectory()) {
                    String pkwId = extractPkwId(dir.getName());
                    Log.d(tag, "pkwid: " + pkwId + " dir: " + dir.getAbsolutePath());

                    startSendingService(context, pkwId, dir.getAbsolutePath());
                }
            }
        }
        else {
            removeAlarm(context);
        }
    }

    private void removeAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent inte = new Intent(context, SendImagesLaterReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, inte, 0);
        alarmManager.cancel(pendingIntent);
        Log.d(tag, "Alarm removed...");
    }

    private void startSendingService(Context context, String pkwId, String imgsDir) {
        Intent sendImagesServiceIntent = new Intent(context, SendImagesService.class);
        sendImagesServiceIntent.putExtra(SendImagesService.PKWID_EXTRA, pkwId);
        sendImagesServiceIntent.putExtra(SendImagesService.IMAGESLIST_EXTRA, imgsDir);
        context.startService(sendImagesServiceIntent);
    }

    private static String extractPkwId(String dirName) {
        int index = dirName.indexOf("_");
        return dirName.substring(0, index);
    }
}
