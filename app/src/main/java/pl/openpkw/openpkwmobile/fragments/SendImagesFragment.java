package pl.openpkw.openpkwmobile.fragments;


import android.animation.Animator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.*;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.FilterCommissionsActivity;
import pl.openpkw.openpkwmobile.activities.OpenPKWActivity;
import pl.openpkw.openpkwmobile.activities.TakePhotosActivity;
import pl.openpkw.openpkwmobile.utils.SendImagesLaterReceiver;
import pl.openpkw.openpkwmobile.utils.SendImagesService;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by Bartlomiej 'baslow' Slowik on 2015.05.17.
 */
public class SendImagesFragment extends Fragment {

    private final static int ALARM_PERIOD = 1 * 60 * 1000;

    private final String tag = getClass().getSimpleName();

    private File[] images;

    private String commissionId;
    private String pkwId;
    private String imgsDir;

    private TextView headerTextView;
    private ImageView imageView;
    private ProgressBar progressBar;
    private TextView footerTextView;
    private Button noButton;
    private Button yesButton;
    private RelativeLayout rlfinal;

    private SendImageReceiver receiver;

    private WndMode windowMode = WndMode.SENDING;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_images, container, false);
        headerTextView = (TextView) view.findViewById(R.id.fragment_send_images_header_text);
        imageView = (ImageView) view.findViewById(R.id.fragment_send_images_imageView);
        progressBar = (ProgressBar) view.findViewById(R.id.fragment_send_images_progressBar);
        footerTextView = (TextView) view.findViewById(R.id.fragment_send_images_footer_text);
        rlfinal = (RelativeLayout) view.findViewById(R.id.fragment_send_images_final);
        noButton = (Button) view.findViewById(R.id.fragment_send_images_no_button);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (windowMode) {
                    case SENDED:
                    case POST_ERROR:
                        Intent commissionIntent = new Intent(getActivity(), FilterCommissionsActivity.class);
                        startActivity(commissionIntent);
                        getActivity().finish();
                        break;
                    case ERROR:
                        startService();
                        break;
                }
            }
        });
        yesButton = (Button) view.findViewById(R.id.fragment_send_images_yes_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (windowMode) {
                    case SENDED:
                    case POST_ERROR:
                        getActivity().finish();
                        break;
                    case ERROR:

                        Intent intent = new Intent(getActivity(), SendImagesLaterReceiver.class);

                        if (PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_NO_CREATE) == null) {
                            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
                            alarmManager.setRepeating(
                                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                    SystemClock.elapsedRealtime() + ALARM_PERIOD,
                                    ALARM_PERIOD,
                                    pendingIntent);
                            Log.d(tag, "alarm has been set");
                        } else {
                            Log.d(tag, "alarm already set");
                        }

                        windowMode = WndMode.POST_ERROR;
                        setWindowLook();

                        break;
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // register receiver
        IntentFilter filter = new IntentFilter(ACTION_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new SendImageReceiver();
        getActivity().registerReceiver(receiver, filter);

        getFromArguments();
        setWindowLook();

        if (!isNullOrEmpty(pkwId) && !isNullOrEmpty(commissionId) && !isNullOrEmpty(imgsDir)) {


            startService();
        } else {
            // TODO @baslow: add toast message
        }
    }

    private void startService() {
        Log.d(tag, "starting service");
        images = listImages();
        setImage(0);
        progressBar.setProgress(0);
        progressBar.setMax(images.length);
        windowMode = WndMode.SENDING;
        setWindowLook();

        Intent sendImagesServiceIntent = new Intent(getActivity(), SendImagesService.class);
        sendImagesServiceIntent.putExtra(SendImagesService.PKWID_EXTRA, pkwId);
        sendImagesServiceIntent.putExtra(SendImagesService.IMAGESLIST_EXTRA, imgsDir);
        getActivity().startService(sendImagesServiceIntent);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    private void getFromArguments() {
        // PKW ID
        pkwId = getArguments().getString(TakePhotosActivity.PKW_ID);
        // COMMISSION ID
        commissionId = getArguments().getString(TakePhotosActivity.COMMISSION_ID);
        // pictures directory
        imgsDir = getArguments().getString(TakePhotosActivity.IMAGE_PATH);
        // LOG
        Log.d(tag, "Send images, arguments: [" + pkwId + "] [" + commissionId + "] [" + imgsDir + "]");
    }

    private static boolean isNullOrEmpty(String str) {
        boolean ret = true;
        if (str != null && str.trim().length() > 0) {
            ret = false;
        }
        return ret;
    }

    private File[] listImages() {
        return new File(imgsDir).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                boolean ret = false;
                if (!pathname.getAbsolutePath().endsWith(TakePhotosActivity.THUMBNAIL_EXTENSION)) {
                    ret = true;
                }
                return ret;
            }
        });
    }

    private void setWindowLook() {
        switch (windowMode) {
            case SENDING:
                rlfinal.setVisibility(View.INVISIBLE);
                headerTextView.setVisibility(View.VISIBLE);
                headerTextView.setText(R.string.fragment_send_images_header_in_progress);
                footerTextView.setVisibility(View.VISIBLE);
                footerTextView.setText(R.string.fragment_send_images_footer_in_progress);
                noButton.setVisibility(View.INVISIBLE);
                yesButton.setVisibility(View.INVISIBLE);
                break;
            case SENDED:
                rlfinal.setVisibility(View.VISIBLE);
                headerTextView.setVisibility(View.VISIBLE);
                headerTextView.setText(R.string.fragment_send_images_header_text_positive);
                footerTextView.setVisibility(View.VISIBLE);
                footerTextView.setText(R.string.fragment_send_images_footer_text_positive);
                noButton.setText(R.string.fragment_send_images_next_commission);
                noButton.setVisibility(View.VISIBLE);
                yesButton.setText(R.string.fragment_send_images_finish);
                yesButton.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                rlfinal.setVisibility(View.INVISIBLE);
                headerTextView.setVisibility(View.INVISIBLE);
                footerTextView.setVisibility(View.VISIBLE);
                footerTextView.setText(R.string.fragment_send_images_header_connection_error);
                noButton.setVisibility(View.VISIBLE);
                noButton.setText(R.string.fragment_send_images_try_next_time);
                yesButton.setVisibility(View.VISIBLE);
                yesButton.setText(R.string.fragment_send_images_save);
                break;
            case POST_ERROR:
                rlfinal.setVisibility(View.VISIBLE);
                headerTextView.setVisibility(View.VISIBLE);
                headerTextView.setText(R.string.fragment_send_images_saved);
                footerTextView.setVisibility(View.VISIBLE);
                footerTextView.setText(R.string.fragment_send_images_will_send_later);
                noButton.setText(R.string.fragment_send_images_next_commission);
                noButton.setVisibility(View.VISIBLE);
                yesButton.setText(R.string.fragment_send_images_finish);
                yesButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    private enum WndMode {
        SENDING,
        SENDED,
        ERROR,
        POST_ERROR,
    }

    private void setImage(int index) {
        if (index < images.length) {
            Bitmap bmp = BitmapFactory.decodeFile(getThumbnailPath(images[index].getAbsolutePath()));
            imageView.setImageBitmap(bmp);
        } else {
            imageView.setImageBitmap(null);
        }
    }

    private String getThumbnailPath(String imagePath) {
        return imagePath.replace(TakePhotosActivity.IMAGE_EXTENSION, TakePhotosActivity.THUMBNAIL_EXTENSION);
    }

    public static final String ACTION_RESPONSE = "pl.openpkw.openpkwmobile.SEND_IMAGES";


    private class SendImageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            boolean finished = intent.getBooleanExtra(SendImagesService.FINISHED_EXTRA, false);
            boolean success = intent.getBooleanExtra(SendImagesService.FINISHEDSUCCESS_EXTRA, false);
            int count = intent.getIntExtra(SendImagesService.IMAGECOUNTER_EXTRA, -1);

            Log.d(tag, "received... finished:" + finished + " success:" + success + " counter:" + count);

            if (finished == false) {
                progressBar.setProgress(count);
                setImage(count);
            }

            if (finished == true) {
                if (success == true) {
                    windowMode = WndMode.SENDED;
                } else {
                    windowMode = WndMode.ERROR;
                    Toast.makeText(getActivity(), "Nie można wysłać zdjęć na serwer", Toast.LENGTH_LONG).show();
                }
            }
            setWindowLook();
        }
    }

}
