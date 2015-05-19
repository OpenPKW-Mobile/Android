package pl.openpkw.openpkwmobile.fragments;


import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.TakePhotosActivity;
import pl.openpkw.openpkwmobile.utils.SendImagesService;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Bartlomiej 'baslow' Slowik on 2015.05.17.
 */
public class SendImagesFragment extends Fragment {

    private final String tag = getClass().getSimpleName();

    private File[] images;

    private String commissionId;
    private String imgsDir;

    private TextView headerTextView;
    private ImageView imageView;
    private ProgressBar progressBar;
    private TextView footerTextView;
    private Button noButton;
    private Button yesButton;

    private WndMode windowMode = WndMode.SENDING;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_images, container, false);
        headerTextView = (TextView) view.findViewById(R.id.fragment_send_images_header_text);
        imageView = (ImageView) view.findViewById(R.id.fragment_send_images_imageView);
        progressBar = (ProgressBar) view.findViewById(R.id.fragment_send_images_progressBar);
        footerTextView = (TextView) view.findViewById(R.id.fragment_send_images_footer_text);
        noButton = (Button) view.findViewById(R.id.fragment_send_images_no_button);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (windowMode) {
                    case SENDED:

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
                        getActivity().finish();
                        System.exit(0);
                        break;
                }
            }
        });

        IntentFilter filter = new IntentFilter(ACTION_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        TestReceiver receiver = new TestReceiver();
        getActivity().registerReceiver(receiver, filter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getFromArguments();
        setWindowLook();

        if (!isNullOrEmpty(commissionId) && !isNullOrEmpty(imgsDir)) {
            images = listImages();
            setImage(0);
            progressBar.setMax(images.length);

            // new SendImages().execute();
            Log.d(tag, "starting service");
            Intent sendImagesServiceIntent = new Intent(getActivity(), SendImagesService.class);
            sendImagesServiceIntent.putExtra(SendImagesService.COMMISSIONID_EXTRA, commissionId);
            sendImagesServiceIntent.putExtra(SendImagesService.IMAGESLIST_EXTRA, toPathsArray(images));
            getActivity().startService(sendImagesServiceIntent);
        } else {
            // TODO @baslow: add toast message
        }
    }

    private void getFromArguments() {
        // PKW ID
        commissionId = getArguments().getString(TakePhotosActivity.COMMISSION_ID);
        // pictures directory
        imgsDir = getArguments().getString(TakePhotosActivity.IMAGE_PATH);
        // LOG
        Log.d(tag, "Send images, arguments: [" + commissionId + "] [" + imgsDir + "]");
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

    private static String[] toPathsArray(File[] images) {
        String[] pathsArray = new String[images.length];
        for (int i = 0; i < images.length; i++) {
            pathsArray[i] = images[i].getAbsolutePath();
        }
        return pathsArray;
    }

    private void setWindowLook() {
        switch (windowMode) {
            case SENDING:
                headerTextView.setText("Trwa przesyłanie zdjęć");
                footerTextView.setText("Proszę czekać");
                noButton.setVisibility(View.GONE);
                yesButton.setVisibility(View.INVISIBLE);
                break;
            case SENDED:
                headerTextView.setText(R.string.fragment_send_images_header_text_positive);
                footerTextView.setText(R.string.fragment_send_images_footer_text_positive);
                noButton.setVisibility(View.GONE);
                yesButton.setText("Zakończ");
                yesButton.setVisibility(View.VISIBLE);
        }
    }

    private enum WndMode {
        SENDING,
        SENDED,
        ERROR,
        SAVED,
    }

    private void setImage(int index) {
        if (index < images.length) {
            Bitmap bmp = BitmapFactory.decodeFile(getThumbnailPath(images[index].getAbsolutePath()));
            imageView.setImageBitmap(bmp);
        }
        else {
            imageView.setImageBitmap(null);
        }
    }

    private String getThumbnailPath(String imagePath) {
        return imagePath.replace(TakePhotosActivity.IMAGE_EXTENSION, TakePhotosActivity.THUMBNAIL_EXTENSION);
    }

    public static final String ACTION_RESPONSE = "pl.openpkw.openpkwmobile.SEND_IMAGES";


    private class TestReceiver extends BroadcastReceiver {

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
                }
            }
            setWindowLook();
        }
    }





}
