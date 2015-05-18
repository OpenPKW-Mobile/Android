package pl.openpkw.openpkwmobile.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.style.LineHeightSpan;
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
import org.w3c.dom.Text;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.TakePhotosActivity;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Created by Bartlomiej 'baslow' Slowik on 2015.05.17.
 */
public class SendImagesFragment extends Fragment {

    private final String tag = getClass().getSimpleName();

    private String commissionId;
    private String imgsDir;

    private File[] images;

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
        yesButton = (Button) view.findViewById(R.id.fragment_send_images_yes_button);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getFromArguments();
        setWindowLook();

        if (!isNullOrEmpty(commissionId) && !isNullOrEmpty(imgsDir)) {
            images = listImages();
            progressBar.setMax(images.length);
            new SendImages().execute();
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

    private void setWindowLook() {
        switch (windowMode) {
            case SENDING:
                headerTextView.setText("Trwa przesyłanie zdjęć");
                footerTextView.setText("Proszę czekać");
                noButton.setVisibility(View.GONE);
                yesButton.setVisibility(View.GONE);
                break;
            case SENDED:
                headerTextView.setText(R.string.fragment_send_images_header_text_positive);
                footerTextView.setText(R.string.fragment_send_images_footer_text_positive);
                noButton.setVisibility(View.GONE);
                yesButton.setText("Zakończ");
                yesButton.setVisibility(View.VISIBLE);
        }
    }

    private class SendImages extends AsyncTask<Void, Integer, Boolean> {

        private final static String PKWID_KEY = "pkwId";
        private final static String FILENAME_KEY = "fileName";

        private final HttpClient httpClient = new DefaultHttpClient();

        private int imgCounter = 1;

        @Override
        protected void onPreExecute() {
            setImage(0);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return sendImages(images);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int index = values[0];
            setImage(index);
            progressBar.setProgress(index);
        }

        /**
         * change texts on window
         * @param aBoolean
         */
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean == true) {
                windowMode = WndMode.SENDED;
            }
            else {
                windowMode = WndMode.ERROR;
            }
            setWindowLook();
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

        private HttpResponse sendHttpRequest(HttpPost httpPost, AbstractHttpEntity entity) throws IOException {
            httpPost.setEntity(entity);
            return httpClient.execute(httpPost);
        }

        private JSONObject makeMetadataJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put(PKWID_KEY, commissionId);
            json.put(FILENAME_KEY, "image_" + commissionId + "_" + imgCounter + TakePhotosActivity.IMAGE_EXTENSION);
            return json;
        }

        /**
         * Send single image
         */
        private boolean sendImage(File imagePath) {
            boolean ret = false;
            try {
                JSONObject metadataJson = makeMetadataJson();
                String metadataJsonString = metadataJson.toString();
                Log.d(tag, "metadata request json: " + metadataJsonString);

                StringEntity entity = new StringEntity(metadataJsonString);
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                HttpResponse response = sendHttpRequest(new HttpPost("http://openpkw.nazwa.pl/api/image-metadata.php"), entity);
                String responseString = EntityUtils.toString(response.getEntity());
                Log.d(tag, "response: " + responseString);

                JSONObject jsonResp = new JSONObject(responseString);
                String uploadUrl = jsonResp.getString("uploadUrl");
                Log.d(tag, "uploadURL: " + uploadUrl);

                // -----

                FileEntity imgEntity = new FileEntity(imagePath.getAbsoluteFile(), "image/jpeg");

                HttpResponse uploadResponse = sendHttpRequest(new HttpPost(uploadUrl), imgEntity);
                String uploadResponseString = EntityUtils.toString(uploadResponse.getEntity());
                Log.d(tag, "upload response: " + uploadResponseString);

                JSONObject uploadJsonResp = new JSONObject(uploadResponseString);
                int bytes = uploadJsonResp.getInt("bytesCount");
                int imgLen = (int) imagePath.length();
                Log.d(tag, "uploaded bytes: " + bytes + " image length: " + imgLen);
                if (imgLen == bytes) {
                    ret = true;
                }
            }
            catch (JSONException | IOException ex) {
                Log.e(tag, "Nie można uploadować zdjęć na serwer", ex);
            }
            return ret;
        }

        /**
         * Send all images
         */
        private boolean sendImages(File[] filesToSend) {
            boolean ret = true;
            for (File fileToSend : filesToSend) {
                ret = sendImage(fileToSend);
                if (ret == false) {
                    break;
                }
                publishProgress(imgCounter);
                imgCounter++;
            }
            if (ret == true) {
                // TODO @baslow: delete all files and directory
            }
            return ret;
        }
    }

    private enum WndMode {
        SENDING,
        SENDED,
        ERROR,
        SAVED,
    }
}
