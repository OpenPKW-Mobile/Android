package pl.openpkw.openpkwmobile.app;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseCrashReporting;

/**
 * Created by michalu on 11.07.15.
 */
public class OpenpkwApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Crash Reporting
        ParseCrashReporting.enable(this);
        Parse.initialize(this, "xxx", "xxx");
    }
}
