package pl.openpkw.openpkwmobile.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.fragments.FilterCommissionsFragment;
import pl.openpkw.openpkwmobile.models.Commission;
import pl.openpkw.openpkwmobile.models.User;

/**
 * Created by Wojciech Radzioch on 09.05.15.
 */
public class FilterCommissionsActivity extends FragmentActivity {
    private static final String FILTER_COMMISSIONS_FRAGMENT_TAG = "FilterCommissionsFragment";
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_commissions);
        User user = null;
        if (getIntent() != null && getIntent().hasExtra("user")) {
            user = (User) getIntent().getSerializableExtra("user");
        }

        FragmentManager fm = getSupportFragmentManager();
        FilterCommissionsFragment filterCommissionsFragment = (FilterCommissionsFragment) fm.findFragmentByTag(FILTER_COMMISSIONS_FRAGMENT_TAG);
        if (filterCommissionsFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.filter_commissions_fragment_container, FilterCommissionsFragment.create(user), FILTER_COMMISSIONS_FRAGMENT_TAG);
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
