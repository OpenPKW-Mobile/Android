package pl.openpkw.openpkwmobile.utils;

import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.Toast;


/**
 * Created by Wojciech Radzioch on 2015-05-23.
 */
public class CustomToast {

    public static void showToast (String message, int seconds, Activity activity) {
        final Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        toast.show();
        new CountDownTimer((seconds-1) * 1000, 1000)
        {
            public void onTick(long millisUntilFinished) {toast.show();}
            public void onFinish() {toast.show();}

        }.start();
    }
}
