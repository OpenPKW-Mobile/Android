package pl.openpkw.openpkwmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.fragments.RegisterFragment;

/**
 * Created by Wojciech Radzioch on 12.05.15.
 */
public class RegisterActivity extends OpenPKWActivity {
    private static final String REGISTER_FRAGMENT_TAG = "RegisterFragment";
    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FragmentManager fm = getSupportFragmentManager();
        RegisterFragment registerFragment = (RegisterFragment) fm.findFragmentByTag(REGISTER_FRAGMENT_TAG);
        if (registerFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.register_fragment_container, new RegisterFragment(), REGISTER_FRAGMENT_TAG);
            ft.commit();
            fm.executePendingTransactions();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
