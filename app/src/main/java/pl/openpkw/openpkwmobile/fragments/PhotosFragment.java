package pl.openpkw.openpkwmobile.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.OpenPKWActivity;
import pl.openpkw.openpkwmobile.activities.TakePhotosActivity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartlomiej 'baslow' Slowik on 2015.05.08.
 */
public class PhotosFragment extends Fragment {

    private PhotosAdapter photosAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        Button sendButton = (Button) view.findViewById(R.id.fragment_photos_sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TakePhotosActivity) getActivity()).switchToSendImages(SendImagesFragment.WndMode.SENDING);
            }
        });

        GridView gridView = (GridView) view.findViewById(R.id.fragment_photos_gridView);

        photosAdapter = new PhotosAdapter(view.getContext());
        gridView.setAdapter(photosAdapter);

        ((OpenPKWActivity)getActivity()).setStepNo(view.findViewById(R.id.step), 5);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllImages();

        if (photosAdapter.getCount() == 0) {
            // dialog
            new AlertDialog.Builder(getActivity()).
                    setMessage(R.string.fragment_photo_preview_dont_have_images).
                    setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((TakePhotosActivity) getActivity()).switchToSendImages(SendImagesFragment.WndMode.SENDED);
                        }
                    }).
                    setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((TakePhotosActivity) getActivity()).switchToImageTake();
                        }
                    }).
                    setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            ((TakePhotosActivity) getActivity()).switchToImageTake();
                        }
                    }).create().show();
        }
    }

    private void getAllImages() {
        String imgPath = getArguments().getString(TakePhotosActivity.IMAGE_PATH);
        File imgDir = new File(imgPath);
        File[] imgs = imgDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getAbsolutePath().endsWith(TakePhotosActivity.THUMBNAIL_EXTENSION);
            }
        });

        photosAdapter.addAll(imgs);
    }

    private class PhotosAdapter extends BaseAdapter {

        private final LayoutInflater layoutInflater;
        private final List<ImageItem> items = new ArrayList<ImageItem>();

        private ImageView imageView;

        public PhotosAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public ImageItem getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View curView = convertView;

            if (curView == null) {
                curView = layoutInflater.inflate(R.layout.image_item_view, parent, false);
            }

            ImageItem item = items.get(position);

            imageView = (ImageView) curView.findViewById(R.id.image_item_image);
            imageView.setImageBitmap(item.getBitmap());

            return curView;
        }

        public void add(String filePath) {
            items.add(new ImageItem(filePath));
        }

        public void addAll(File[] files) {
            for (File file : files) {
                add(file.getAbsolutePath());
            }
        }

        private class ImageItem {

            private String filePath;

            ImageItem(String filePath) {
                this.filePath = filePath;
            }

            Bitmap getBitmap() {
                Bitmap bmp = BitmapFactory.decodeFile(filePath);
                return bmp;
            }
        }
    }
}
