package pl.openpkw.openpkwmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.OpenPKWActivity;


public class SummaryFragment extends Fragment {

    public final static String SUMMARY_FRAGMENT_TAG = "SUMMARY_FRAGMENT_TAG";


    public SummaryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);


        ((OpenPKWActivity)getActivity()).setStepNo(view.findViewById(R.id.step),6);
        return view;
    }
}
