package pl.openpkw.openpkwmobile.fragments;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.TakePhotosActivity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by Bartlomiej 'baslow' Slowik on 2015.05.02.
 */
public class PhotoPreviewFragment extends Fragment {

    private final String tag = getClass().getSimpleName();

    private byte[] imgData;
    private Bitmap thumbnailBmp;

    private ProgressBar progressBar;
    private ImageView imageView;

    private boolean isButtonLock;
    private AsyncTask<Void, Void, Void> saveImageTask;

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
                    saveImageTask.execute();
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

                imgData = params[0];

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeByteArray(params[0], 0, params[0].length, options);

                final int width = options.outWidth;
                final int height = options.outHeight;
                int inSampleSize = 1;

                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                final int reqWidth = size.x;
                final int reqHeiht = size.y;

                if (width > reqWidth && height > reqHeiht) {
                    final int halfWidth = width / 2;
                    final int halfHeight = height / 2;

                    while ((halfWidth / inSampleSize) > reqWidth && (halfHeight / inSampleSize) > reqHeiht) {
                        inSampleSize *= 2;
                    }
                }

                options.inSampleSize = inSampleSize;
                options.inJustDecodeBounds = false;

                Bitmap bitmap = BitmapFactory.decodeByteArray(params[0], 0, params[0].length, options);
                Bitmap bmp = bitmap;

                if (bitmap.getWidth() > bitmap.getHeight()) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }

                return bmp;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                thumbnailBmp = bitmap;
                imageView.setImageBitmap(thumbnailBmp);
                super.onPostExecute(bitmap);
            }
        };
    }

    private AsyncTask<Void, Void, Void> makeSaveAsyncTask() {
        return new ImageAsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... vParams) {

                int imgDataLength = imgData.length;
                if (imgDataLength > 0) {
                    String imageDir = getArguments().getString(TakePhotosActivity.IMAGE_PATH);
                    String fileName = TakePhotosActivity.NAME_FORMAT.format(new Date(System.currentTimeMillis()));

                    File imageFile = new File(imageDir, fileName + TakePhotosActivity.IMAGE_EXTENSION);
                    File thumbnailFile = new File(imageDir, fileName + TakePhotosActivity.THUMBNAIL_EXTENSION);

                    Display display = getActivity().getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    float place = size.x / 3.5f;

                    int imgWidth = (int) place;
                    int imgHeight = (int) ((place * ((float) thumbnailBmp.getHeight()) / thumbnailBmp.getWidth()));

                    Bitmap thumbnail = Bitmap.createScaledBitmap(thumbnailBmp,
                            imgWidth, imgHeight, true);

                    ByteArrayOutputStream tumbnailStream = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, TakePhotosActivity.THUMBNAIL_COMPRESSION, tumbnailStream);
                    byte[] thumbnailData = tumbnailStream.toByteArray();

                    try {
                        BufferedOutputStream destImageStream = new BufferedOutputStream(new FileOutputStream(imageFile));
                        BufferedOutputStream destThumbnailStream = new BufferedOutputStream(new FileOutputStream(thumbnailFile));

                        int offset = 0;
                        int length = TakePhotosActivity.SAVING_BUFFER;

                        while (imgDataLength > offset) {
                            if (offset + length > imgDataLength) {
                                length = imgDataLength - offset;
                            }
                            destImageStream.write(imgData, offset, length);
                            offset += length;
                        }

                        destThumbnailStream.write(thumbnailData);

                        destImageStream.close();
                        destThumbnailStream.close();

                    } catch (Exception ex) {
                        Log.d(tag, "Exception during image saving: " + ex.toString());
                    }

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
