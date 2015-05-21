package pl.openpkw.openpkwmobile.activities;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.fragments.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Bartlomiej 'baslow' Slowik on 2015.05.02.
 */
public class TakePhotosActivity extends OpenPKWActivity {

    public final static String IMAGE_EXTENSION = ".jpg";
    public final static String THUMBNAIL_EXTENSION = ".m.jpg";

    public final static int SAVING_BUFFER = 8 * 1024;

    public final static int THUMBNAIL_COMPRESSION = 75;

    // 3 MPX 2048x1536
    public final static int MAX_PICTURE_HEIGHT = 1536;
    public final static int MAX_PICTURE_WIDTH = 2048;

    public final static String PKW_ID = "pkwId";
    public final static String COMMISSION_ID = "commissionId";
    public final static String IMAGE_DATA = "imageData";
    public final static String IMAGE_PATH = "imagePath";

    public final static SimpleDateFormat NAME_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

    private final static String TAKEPHOTOS_FRAGMENT_TAG = "TakePhotosFragment";
    private final static String TAKEPHOTOS_INTERNAL_FRAGMENT_TAG = "TakePhotosInternalFragment";

    private FragmentManager fragmentManager;

    private TakePhotoFragment takePhotoFragment;
    private PhotoPreviewFragment photoPreviewFragment;
    private PhotosFragment photosFragment;
    private SendImagesFragment sendImagesFragment;

    // get from previous activity or get from server
    private String commissionId = "789";
    private String pkwId = "123456-789";

    private File outDir;

    public void switchToImageTake() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_take_photos_inner_container, takePhotoFragment, TAKEPHOTOS_INTERNAL_FRAGMENT_TAG);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    public void switchToImagePreview(byte[] data) {
        Bundle bundle = new Bundle();
        bundle.putByteArray(IMAGE_DATA, data);
        bundle.putString(IMAGE_PATH, outDir.getAbsolutePath());

        photoPreviewFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_take_photos_inner_container, photoPreviewFragment, TAKEPHOTOS_INTERNAL_FRAGMENT_TAG);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    public void switchToPhotos() {
        Bundle bundle = new Bundle();
        bundle.putString(IMAGE_PATH, outDir.getAbsolutePath());

        photosFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_take_photos_inner_container, photosFragment, TAKEPHOTOS_INTERNAL_FRAGMENT_TAG);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    public void switchToSendImages() {
        Bundle bundle = new Bundle();
        bundle.putString(PKW_ID, pkwId);
        bundle.putString(COMMISSION_ID, commissionId);
        bundle.putString(IMAGE_PATH, outDir.getAbsolutePath());

        sendImagesFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_take_photos_inner_container, sendImagesFragment, TAKEPHOTOS_INTERNAL_FRAGMENT_TAG);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pkwId = getIntent().getStringExtra(PKW_ID);
        commissionId = getIntent().getStringExtra(COMMISSION_ID);

        setContentView(R.layout.activity_take_photos);

        prepareDirectoriy();

        fragmentManager = getSupportFragmentManager();

        TakePhotosFragment takePhotosFragmentContainer = (TakePhotosFragment) fragmentManager.findFragmentByTag(TAKEPHOTOS_FRAGMENT_TAG);
        if (takePhotosFragmentContainer == null) {
            takePhotosFragmentContainer = new TakePhotosFragment();

            Bundle bundle = new Bundle();
            bundle.putString(PKW_ID, pkwId);
            bundle.putString(COMMISSION_ID, commissionId);
            takePhotosFragmentContainer.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.activity_take_photos_inner_container, takePhotosFragmentContainer, TAKEPHOTOS_FRAGMENT_TAG);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
        }

        if (takePhotoFragment == null) {
            takePhotoFragment = new TakePhotoFragment();
        }

        if (photoPreviewFragment == null) {
            photoPreviewFragment = new PhotoPreviewFragment();
        }

        if (photosFragment == null) {
            photosFragment = new PhotosFragment();
        }

        if (sendImagesFragment == null) {
            sendImagesFragment = new SendImagesFragment();
        }

        switchToImageTake();
    }

    private void prepareDirectoriy() {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            String dirName = pkwId + "_" + NAME_FORMAT.format(new Date(System.currentTimeMillis()));
            outDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), dirName);
            boolean dirExist = outDir.exists();

            if (!dirExist) {
                dirExist = outDir.mkdirs();
            }

            if (!dirExist) {
                Toast.makeText(getApplication(), R.string.activity_take_photos_error_message_cannot_create_directory, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplication(), R.string.activity_take_photos_error_message_no_external_memory, Toast.LENGTH_LONG).show();
        }
    }
}
