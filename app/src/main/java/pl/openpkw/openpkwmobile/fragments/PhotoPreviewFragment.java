package pl.openpkw.openpkwmobile.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.TakePhotosActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by Bartlomiej 'baslow' Slowik on 2015.05.02.
 */
public class PhotoPreviewFragment extends Fragment {

    private final String tag = getClass().getSimpleName();

    private ProgressBar progressBar;
    private ImageView imageView;
    private Bitmap bitmap;

    private volatile boolean isButtonLock;
    private AsyncTask<Bitmap, Void, Void> saveImageTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_preview, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.fragment_photo_preview_progressBar);
        imageView = (ImageView) view.findViewById(R.id.fragment_photo_preview_imageView);

        Button repeatButton = (Button) view.findViewById(R.id.fragment_photo_preview_repeat_button);
        repeatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isButtonLock) {
                    ((TakePhotosActivity) getActivity()).switchToImageTake();
                }
            }
        });

        Button nextButton = (Button) view.findViewById(R.id.fragment_photo_preview_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isButtonLock) {
                    saveImageTask.execute(bitmap);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        isButtonLock = false;

        AsyncTask<byte[], Void, Bitmap> rotateTask = makeRotateAsyncTask();
        saveImageTask = makeSaveAsyncTask();

        byte[] imageData = getArguments().getByteArray(TakePhotosActivity.IMAGE_DATA);
        if (imageData.length > 0) {
            rotateTask.execute(imageData);
        }
    }

    private AsyncTask<byte[], Void, Bitmap> makeRotateAsyncTask() {
        return new ImageAsyncTask<byte[], Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(byte[]... params) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(params[0], 0, params[0].length);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);

                Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                ByteBuffer byteBuffer = ByteBuffer.allocate(bmp.getByteCount());
                bmp.copyPixelsToBuffer(byteBuffer);

                return bmp;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                PhotoPreviewFragment.this.bitmap = bitmap;
                imageView.setImageBitmap(bitmap);
                super.onPostExecute(bitmap);
            }
        };
    }

    private AsyncTask<Bitmap, Void, Void> makeSaveAsyncTask() {
        return new ImageAsyncTask<Bitmap, Void, Void>() {

            @Override
            protected Void doInBackground(Bitmap... params) {

                ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, TakePhotosActivity.IMAGE_COMPRESSION, imageStream);
                byte[] imageData = imageStream.toByteArray();

                Bitmap thumbnail = Bitmap.createScaledBitmap(bitmap,
                        (int) (bitmap.getWidth() * TakePhotosActivity.THUMBNAIL_RATIO),
                        (int) (bitmap.getHeight() * TakePhotosActivity.THUMBNAIL_RATIO), true);

                ByteArrayOutputStream tumbnailStream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, TakePhotosActivity.THUMBNAIL_COMPRESSION, tumbnailStream);
                byte[] thumbnailData = tumbnailStream.toByteArray();

                String fileName = TakePhotosActivity.NAME_FORMAT.format(new Date(System.currentTimeMillis()));

                String imageDir = getArguments().getString(TakePhotosActivity.IMAGE_PATH);

                File imageFile = new File(imageDir, fileName + TakePhotosActivity.IMAGE_EXTENSION);
                File thumbnailFile = new File(imageDir, fileName + TakePhotosActivity.THUMBNAIL_EXTENSION);

                try {
                    FileOutputStream destImageStream = new FileOutputStream(imageFile);
                    FileOutputStream destThumbnailStream = new FileOutputStream(thumbnailFile);

                    destImageStream.write(imageData);
                    destThumbnailStream.write(thumbnailData);

                    destImageStream.close();
                    destThumbnailStream.close();
                } catch (Exception ex) {
                    Log.d(tag, "exception: " + ex.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ((TakePhotosActivity) getActivity()).switchToImageTake();
            }
        };
    }

    private abstract class ImageAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isButtonLock = true;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Result result) {
            progressBar.setVisibility(View.INVISIBLE);
            isButtonLock = false;
            super.onPostExecute(result);
        }
    }
}
