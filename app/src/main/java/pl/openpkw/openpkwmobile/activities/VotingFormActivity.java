package pl.openpkw.openpkwmobile.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.fragments.VotingFormFragment;
import pl.openpkw.openpkwmobile.models.Commission;
import pl.openpkw.openpkwmobile.models.User;

/**
 * Created by michalu on 28.04.15.
 */
public class VotingFormActivity extends FragmentActivity {
    private static final String VOTINGFORM_FRAGMENT_TAG = "VotingFormFragment";
    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_form);
        User user = null;
        Commission commission = null;
        if (getIntent() != null && getIntent().hasExtra("user_extra") && getIntent().hasExtra("commission_extra")) {
            user = (User) getIntent().getSerializableExtra("user_extra");
            commission = (Commission) getIntent().getParcelableExtra("commission_extra");
        }
        if (user != null && commission != null) {
            FragmentManager fm = getSupportFragmentManager();
            VotingFormFragment fvFragment = (VotingFormFragment) fm.findFragmentByTag(VOTINGFORM_FRAGMENT_TAG);
            if (fvFragment == null) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fvoting_fragment_container, VotingFormFragment.create(user, commission), VOTINGFORM_FRAGMENT_TAG);
                ft.commit();
                fm.executePendingTransactions();
            }
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
