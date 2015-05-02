package pl.openpkw.openpkwmobile.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.fragments.TakePhotoFragment;
import pl.openpkw.openpkwmobile.fragments.TakePhotosFragment;

/**
 * Created by Bartlomiej 'baslow' Slowik on 2015.05.02.
 */
public class TakePhotosActivity extends OpenPKWActivity {

    private final static String TAKEPHOTOS_FRAGMENT_TAG = "TakePhotosFragment";
    private final static String TAKEPHOTO_FRAGMENT_TAG = "TakePhotoFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photos);

        FragmentManager fragmentManager = getSupportFragmentManager();
        TakePhotosFragment takePhotosFragment = (TakePhotosFragment) fragmentManager.findFragmentByTag(TAKEPHOTOS_FRAGMENT_TAG);
        if (takePhotosFragment == null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.activity_take_photos_inner_container, new TakePhotosFragment(), TAKEPHOTOS_FRAGMENT_TAG);
            ft.commit();
            fragmentManager.executePendingTransactions();
        }

        TakePhotoFragment takePhotoFragment = (TakePhotoFragment) fragmentManager.findFragmentByTag(TAKEPHOTO_FRAGMENT_TAG);
        if (takePhotoFragment == null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_take_photos_inner_container, new TakePhotoFragment(), TAKEPHOTO_FRAGMENT_TAG);
            ft.commit();
            fragmentManager.executePendingTransactions();
        }
    }
}
