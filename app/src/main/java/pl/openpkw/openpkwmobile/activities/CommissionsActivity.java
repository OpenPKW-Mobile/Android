package pl.openpkw.openpkwmobile.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.fragments.CommissionsFragment;
import pl.openpkw.openpkwmobile.fragments.VotingFormFragment;

/**
 * Created by michalu on 28.04.15.
 */
public class CommissionsActivity extends FragmentActivity {
    private static final String COMMISSIONS_FRAGMENT_TAG = "CommissionsFragment";
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commissions);

        FragmentManager fm = getSupportFragmentManager();
        CommissionsFragment commFragment = (CommissionsFragment) fm.findFragmentByTag(COMMISSIONS_FRAGMENT_TAG);
        if (commFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.commissions_fragment_container, new CommissionsFragment(), COMMISSIONS_FRAGMENT_TAG);
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
