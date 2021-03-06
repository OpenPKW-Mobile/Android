package pl.openpkw.openpkwmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.OpenPKWActivity;
import pl.openpkw.openpkwmobile.fragments.SplashFragment;

/**
 * Created by Wojciech Radzioch on 2015-04-21.
 */
public class SplashActivity extends OpenPKWActivity {

    public static final int SPLASH_TIME = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SplashFragment())
                    .commit();
        }

        Thread splashThread = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        };
        splashThread.start();
    }
}