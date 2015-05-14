package pl.openpkw.openpkwmobile.fragments;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;
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
                    Log.d(tag, "Setting preview...");
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

            // image size
            bestSize = null;
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

        private Camera.Size findBestPreviewSize(Camera.Parameters cameraParameters, int maxWidth, int maxHeight) {
            List<Camera.Size> sizes = cameraParameters.getSupportedPreviewSizes();
            Camera.Size bestSize = sizes.remove(0);
            for (Camera.Size size : sizes) {
                if ((size.width * size.height) > (bestSize.width * bestSize.height)
                        && (size.width <= maxWidth) && (size.height <= maxHeight)) {
                    bestSize = size;
                }
            }
            return bestSize;
        }

        private Camera.Size findBestPictureSize(Camera.Parameters cameraParameters, int maxWidth, int maxHeight) {
            List<Camera.Size> sizes = cameraParameters.getSupportedPictureSizes();
            Camera.Size bestSize = sizes.remove(0);
            float bestRatio = (float) maxWidth / maxHeight;
            for (Camera.Size size : sizes) {
                int sizeArea = size.width * size.height;
                float sizeRatio = (float) size.width / size.height;
                if (sizeArea > (bestSize.width * bestSize.height) && sizeArea <= (maxWidth * maxHeight) && sizeRatio <= bestRatio) {
                    bestSize = size;
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
            Log.d(tag, "take picture... camera callback");
            camera.takePicture(cameraShutterCallback, null, cameraPictureCallback);
        }
    };

    private boolean isInProgress;

    private boolean isPreviewing;

    private Camera camera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_take_photo, container, false);

        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.fragment_take_photo_surfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceHolderCallback);

        Button takePictureButton = (Button) view.findViewById(R.id.fragment_take_photo_take_picture);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInProgress) {
                    isInProgress = true;
                    camera.autoFocus(autoFocusCallback);
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
        Log.d(tag, "onPause");
        stopPreview();
        releaseCamera();
        super.onPause();
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
