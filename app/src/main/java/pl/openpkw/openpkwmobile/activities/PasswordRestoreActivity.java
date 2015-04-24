package pl.openpkw.openpkwmobile.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.fragments.LoginFragment;
import pl.openpkw.openpkwmobile.fragments.PasswordRestoreFragment;

/**
 * Created by fockeRR on 24.04.15.
 */
public class PasswordRestoreActivity extends FragmentActivity {
    private static final String PRESTORE_FRAGMENT_TAG = "PasswordRestoreFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestore);

        FragmentManager fm = getSupportFragmentManager();
        PasswordRestoreFragment loginFragment = (PasswordRestoreFragment) fm.findFragmentByTag(PRESTORE_FRAGMENT_TAG);
        if (loginFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.prestore_fragment_container, new PasswordRestoreFragment(), PRESTORE_FRAGMENT_TAG);
            ft.commit();
            fm.executePendingTransactions();
        }
    }
}
