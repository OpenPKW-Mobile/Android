package pl.openpkw.openpkwmobile.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.fragments.VotingFormFragment;

/**
 * Created by michalu on 28.04.15.
 */
public class VotingFormActivity extends OpenPKWActivity {
    private static final String VOTINGFORM_FRAGMENT_TAG = "VotingFormFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_form);

        FragmentManager fm = getSupportFragmentManager();
        VotingFormFragment fvFragment = (VotingFormFragment) fm.findFragmentByTag(VOTINGFORM_FRAGMENT_TAG);
        if (fvFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fvoting_fragment_container, new VotingFormFragment(), VOTINGFORM_FRAGMENT_TAG);
            ft.commit();
            fm.executePendingTransactions();
        }
    }
}
