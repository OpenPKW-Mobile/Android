package pl.openpkw.openpkwmobile.utils;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
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
import pl.openpkw.openpkwmobile.activities.TakePhotosActivity;
import pl.openpkw.openpkwmobile.fragments.SendImagesFragment;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Bartlomiej 'baslow' Slowik on 2015.05.18.
 */
public class SendImagesService extends IntentService {

    public final static String IMAGESLIST_EXTRA = "imagesList";
    public final static String COMMISSIONID_EXTRA = "commissionId";
    public final static String PKWID_EXTRA = "pkwId";

    public static final String IMAGECOUNTER_EXTRA = "imageCounter";
    public static final String FINISHED_EXTRA = "finished";
    public static final String FINISHEDSUCCESS_EXTRA = "finishedSuccess";

    private final static String PKWID_KEY = "pkwId";
    private final static String FILENAME_KEY = "fileName";

    private final HttpClient httpClient = new DefaultHttpClient();

    private String pkwId;
    private File imgsDir;

    private int imgCounter = 1;

    private final String tag = getClass().getSimpleName();

    public SendImagesService() {
        super("SendImagesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        pkwId = intent.getStringExtra(PKWID_EXTRA);
        String path = intent.getStringExtra(IMAGESLIST_EXTRA);

        imgsDir = new File(path);
        File[] images = imgsDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.getAbsolutePath().endsWith(TakePhotosActivity.THUMBNAIL_EXTENSION);
            }
        });

        Log.d(tag, "service started, images count: " + images.length);

        boolean ret = sendImages(images);
        publishFinished(ret);
    }

    private HttpResponse sendHttpRequest(HttpPost httpPost, AbstractHttpEntity entity) throws IOException {
        httpPost.setEntity(entity);
        return httpClient.execute(httpPost);
    }

    private JSONObject makeMetadataJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(PKWID_KEY, pkwId);
        json.put(FILENAME_KEY, "image_" + pkwId + "_" + imgCounter + TakePhotosActivity.IMAGE_EXTENSION);
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

            StringEntity entity = new StringEntity(metadataJsonString);
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            HttpResponse response = sendHttpRequest(new HttpPost("http://openpkw.nazwa.pl/api/image-metadata.php"), entity);
            String responseString = EntityUtils.toString(response.getEntity());

            JSONObject jsonResp = new JSONObject(responseString);
            String uploadUrl = jsonResp.getString("uploadUrl");

            // -----

            FileEntity imgEntity = new FileEntity(imagePath.getAbsoluteFile(), "image/jpeg");

            HttpResponse uploadResponse = sendHttpRequest(new HttpPost(uploadUrl), imgEntity);
            String uploadResponseString = EntityUtils.toString(uploadResponse.getEntity());

            JSONObject uploadJsonResp = new JSONObject(uploadResponseString);
            int bytes = uploadJsonResp.getInt("bytesCount");
            int imgLen = (int) imagePath.length();
            Log.d(tag, "uploaded bytes: " + bytes + " image length: " + imgLen);
            if (imgLen == bytes) {
                ret = true;
            }
        } catch (JSONException | IOException ex) {
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
            if (imgsDir != null) {
                File[] list = imgsDir.listFiles();
                for (File file : list) {
                    file.delete();
                }
                imgsDir.delete();
            }
        }
        return ret;
    }

    private void publishFinished(boolean isSuccess) {
        Intent intent = makeBroadcastIntent(true, isSuccess, -1);
        sendBroadcast(intent);
    }

    private void publishProgress(int imageCounter) {
        Intent intent = makeBroadcastIntent(false, false, imageCounter);
        sendBroadcast(intent);
    }

    private Intent makeBroadcastIntent() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SendImagesFragment.ACTION_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        return broadcastIntent;
    }

    private Intent makeBroadcastIntent(boolean isFinished, boolean isSuccess, int counter) {
        Intent broadcastIntent = makeBroadcastIntent();
        broadcastIntent.putExtra(FINISHED_EXTRA, isFinished);
        broadcastIntent.putExtra(FINISHEDSUCCESS_EXTRA, isSuccess);
        broadcastIntent.putExtra(IMAGECOUNTER_EXTRA, counter);
        return broadcastIntent;
    }
}
