package pl.openpkw.openpkwmobile.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.fragments.LoginFragment;

/**
 * Created by fockeRR on 21.04.15.
 */
public class LoginActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment_container, loginFragment).commit();

    }
}
