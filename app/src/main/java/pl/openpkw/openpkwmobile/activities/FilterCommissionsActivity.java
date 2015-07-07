package pl.openpkw.openpkwmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.fragments.FilterCommissionsFragment;
import pl.openpkw.openpkwmobile.models.User;

/**
 * Created by Wojciech Radzioch on 09.05.15.
 */
public class FilterCommissionsActivity extends OpenPKWActivity {
    private static final String FILTER_COMMISSIONS_FRAGMENT_TAG = "FilterCommissionsFragment";

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
