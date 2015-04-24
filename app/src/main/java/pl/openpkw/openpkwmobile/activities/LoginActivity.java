package pl.openpkw.openpkwmobile.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.fragments.LoginFragment;

/**
 * Created by fockeRR on 21.04.15.
 */
public class LoginActivity extends OpenPKWActivity {
    private static final String LOGIN_FRAGMENT_TAG = "LoginFragment";
    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentManager fm = getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) fm.findFragmentByTag(LOGIN_FRAGMENT_TAG);
        if (loginFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.login_fragment_container, new LoginFragment(), LOGIN_FRAGMENT_TAG);
            ft.commit();
            fm.executePendingTransactions();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.fragment_login_twotaptoexit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);
    }
}
