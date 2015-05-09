package pl.openpkw.openpkwmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.TakePhotosActivity;

/**
 * Created by Bartlomiej 'baslow' Slowik on 2015.05.02.
 */
public class TakePhotosFragment extends Fragment {

    private TextView commissionIdTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_take_photos, container, false);

        commissionIdTextView = (TextView) view.findViewById(R.id.fragment_take_photos_commission_id);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        String commissionId = getArguments().getString(TakePhotosActivity.COMMISSION_ID);
        commissionIdTextView.setText(commissionId);
    }
}
