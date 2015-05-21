package pl.openpkw.openpkwmobile.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.fragments.SummaryFragment;


public class SummaryActivity extends OpenPKWActivity {
    private static final String SUMMARY_ACTIVITY_TAG = "SummaryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        FragmentManager fm = getSupportFragmentManager();
        SummaryFragment summaryFragment = (SummaryFragment) fm.findFragmentByTag(SummaryFragment.SUMMARY_FRAGMENT_TAG);
        if (summaryFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.summary_fragment_container, new SummaryFragment(), SummaryFragment.SUMMARY_FRAGMENT_TAG);
            ft.commit();
            fm.executePendingTransactions();
        }

    }
}
