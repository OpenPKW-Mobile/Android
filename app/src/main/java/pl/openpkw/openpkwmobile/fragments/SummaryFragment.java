package pl.openpkw.openpkwmobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.OpenPKWActivity;


public class SummaryFragment extends Fragment {

    public final static String SUMMARY_FRAGMENT_TAG = "SUMMARY_FRAGMENT_TAG";

    public enum STATUS {SUCCESS, FAIL}

    private STATUS status = STATUS.SUCCESS;

    TextView info;
    TextView detail;
    ImageView logo;
    Button button;

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public SummaryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        info = (TextView) view.findViewById(R.id.summary_info);
        detail = (TextView) view.findViewById(R.id.summary_detail);
        logo = (ImageView) view.findViewById(R.id.summary_pkw_logo);
        button = (Button) view.findViewById(R.id.summary_button_finish);

        status=STATUS.FAIL;

        ((OpenPKWActivity) getActivity()).setStepNo(view.findViewById(R.id.step), 6);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(status == STATUS.SUCCESS){
            onSuccess();
        }else if(status == STATUS.FAIL){
            onFailure();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    getActivity().finish();
            }
        });
    }

    private void onSuccess() {
        info.setText(getString(R.string.summary_info_success));
        detail.setText(getString(R.string.summary_detail_success));
        button.setText(getString(R.string.summary_finish));
        logo.setVisibility(View.VISIBLE);
    }

    private void onFailure() {
        info.setText(getString(R.string.summary_info_fialure));
        detail.setText(getString(R.string.summary_detail_failure));
        button.setText(getString(R.string.summary_finish));
        logo.setVisibility(View.INVISIBLE);
    }

    private void onRetry() {
    }
}
