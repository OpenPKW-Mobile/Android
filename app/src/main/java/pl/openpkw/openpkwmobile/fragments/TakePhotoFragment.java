package pl.openpkw.openpkwmobile.fragments;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.TakePhotosActivity;

import java.io.IOException;
import java.util.List;

/**
 * Created by Bartlomiej 'baslow' Slowik on 2015.05.02.
 */
@SuppressWarnings("deprecation")
public class TakePhotoFragment extends Fragment {

    private final String tag = getClass().getCanonicalName();

    private final SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(tag, "Surface created.");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(tag, "Surface destroyed.");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(tag, "Surface changed.");
            restartPreview(holder);
        }

        private void restartPreview(SurfaceHolder holder) {
            if (null != holder.getSurface()) {

                stopPreview();

                setCameraParameters(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());

                try {
                    camera.setPreviewDisplay(holder);
                } catch (IOException ex) {
                    Toast.makeText(getActivity(), R.string.fragment_take_photo_error_cannot_start_preview, Toast.LENGTH_LONG).show();
                }

                startPreview();
            }
        }

        private void setCameraParameters(int surfaceWidth, int surfaceHeight) {

            Camera.Parameters cameraParameters = camera.getParameters();

            // preview size
            Camera.Size bestSize = findBestPreviewSize(cameraParameters, surfaceWidth, surfaceHeight);
            cameraParameters.setPreviewSize(bestSize.width, bestSize.height);

            setSurfaceSize(bestSize, surfaceWidth, surfaceHeight);

            // image size
            bestSize = findBestPictureSize(cameraParameters, TakePhotosActivity.MAX_PICTURE_WIDTH, TakePhotosActivity.MAX_PICTURE_HEIGHT);
            cameraParameters.setPictureSize(bestSize.width, bestSize.height);

            // continuous auto-focus
            cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            // flash mode
            cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);

            // rotate 90
            cameraParameters.setRotation(90);

            camera.setParameters(cameraParameters);
            camera.setDisplayOrientation(90);
        }

        private void setSurfaceSize(Camera.Size size, int maxWidth, int maxHeight) {

            int bestWidth = size.height;
            int bestHeight = size.width;

            float widthRatio = (float) bestWidth / maxWidth;
            float heightRatio = (float) bestHeight / maxHeight;

            float ratio = widthRatio;
            if (heightRatio > widthRatio) {
                ratio = heightRatio;
            }

            int width = (int) (bestWidth / ratio);
            int height = (int) (bestHeight / ratio);

            RelativeLayout.LayoutParams layProp = new RelativeLayout.LayoutParams(width, height);
            surfaceView.setLayoutParams(layProp);
        }

        private Camera.Size findBestPreviewSize(Camera.Parameters cameraParameters, int width, int height) {
            List<Camera.Size> sizes = cameraParameters.getSupportedPreviewSizes();
            Camera.Size bestSize = null;
            for (Camera.Size size : sizes) {
                int cameraWidth = size.height;
                int camerHeight = size.width;

                if ((size.width <= width && size.height <= height) || (size.height <= width && size.width <= height)) {
                    if (bestSize == null) {
                        bestSize = size;
                    }
                    else {
                        int resultArea = bestSize.width * bestSize.height;
                        int newArea = size.width * size.height;

                        if (newArea > resultArea) {
                            bestSize = size;
                        }
                    }
                }
            }

            return bestSize;
        }

        private Camera.Size findBestPictureSize(Camera.Parameters cameraParameters, int maxWidth, int maxHeight) {
            List<Camera.Size> sizes = cameraParameters.getSupportedPictureSizes();
            Camera.Size bestSize = null;
            for (Camera.Size size : sizes) {
                if ((size.width <= maxWidth && size.height <= maxHeight) || (size.height <= maxWidth && size.width <= maxHeight)) {
                    if (bestSize == null) {
                        bestSize = size;
                    }
                    else {
                        int bestArea = bestSize.width * bestSize.height;
                        int newArea = size.width * size.height;
                        if (newArea > bestArea) {
                            bestSize = size;
                        }
                    }
                }
            }
            return bestSize;
        }
    };

    private final Camera.ShutterCallback cameraShutterCallback = new Camera.ShutterCallback() {

        @Override
        public void onShutter() {
            // TODO baslow: do nothing?
        }
    };

    private final Camera.PictureCallback cameraPictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(tag, "take picture... camera callback");
            ((TakePhotosActivity) getActivity()).switchToImagePreview(data);
        }
    };

    private final Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.d(tag, "take picture... auto focus callback");
            camera.takePicture(cameraShutterCallback, null, cameraPictureCallback);
        }
    };

    private boolean isInProgress;

    private boolean isPreviewing;

    private Camera camera;

    private SurfaceView surfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_take_photo, container, false);

        surfaceView = (SurfaceView) view.findViewById(R.id.fragment_take_photo_surfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceHolderCallback);

        final Button takePictureButton = (Button) view.findViewById(R.id.fragment_take_photo_take_picture);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInProgress) {
                    isInProgress = true;
                    takeAPicture();
                }
            }
        });

        Button finishButton = (Button) view.findViewById(R.id.fragment_take_photo_finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInProgress) {
                    ((TakePhotosActivity) getActivity()).switchToPhotos();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        isInProgress = false;

        releaseCamera();
        setupCamera();
    }

    @Override
    public void onPause() {
        stopPreview();
        releaseCamera();
        super.onPause();
    }

    private void takeAPicture() {
        boolean hasAutoFocus = false;
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            List<String> supportedFocusModes = camera.getParameters().getSupportedFocusModes();
            hasAutoFocus = supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        if (hasAutoFocus) {
            Log.d(tag, "take a picture with auto focus");
            camera.autoFocus(autoFocusCallback);
        } else {
            Log.d(tag, "take a picture without auto focus");
            camera.takePicture(cameraShutterCallback, null, cameraPictureCallback);
        }
    }

    private void startPreview() {
        if (null != camera) {
            try {
                camera.startPreview();
                isPreviewing = true;
            } catch (Exception e) {
                Log.e(tag, "Nie można uruchomić podglądu", e);
            }
        }
    }

    private void stopPreview() {
        if (null != camera && isPreviewing) {
            try {
                camera.stopPreview();
                isPreviewing = false;
            } catch (Exception e) {
                Log.e(tag, "Nie można zatrzymać podglądu");
            }
        }
    }

    private void setupCamera() {
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (RuntimeException ex) {
            Toast.makeText(getActivity(), R.string.fragment_take_photo_error_cannot_start_camera, Toast.LENGTH_LONG).show();
        }
    }

    private void releaseCamera() {
        if (null != camera) {
            camera.release();
            camera = null;
        }
    }
}
