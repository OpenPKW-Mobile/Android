package pl.openpkw.openpkwmobile.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.fragments.RegisterFragment;

/**
 * Created by Wojciech Radzioch on 12.05.15.
 */
public class RegisterActivity extends OpenPKWActivity {
    private static final String REGISTER_FRAGMENT_TAG = "RegisterFragment";


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
}
