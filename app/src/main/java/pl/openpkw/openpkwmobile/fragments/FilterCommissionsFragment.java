package pl.openpkw.openpkwmobile.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.OpenPKWActivity;
import pl.openpkw.openpkwmobile.activities.VotingFormActivity;
import pl.openpkw.openpkwmobile.models.Commission;
import pl.openpkw.openpkwmobile.models.User;
import pl.openpkw.openpkwmobile.network.NetworkManager;

/**
 * Created by Wojciech Radzioch on 09.05.15.
 */
public class FilterCommissionsFragment extends Fragment {
    private EditText teritorialCodeET;
    private EditText commissionNumberET;
    private Button searchCommission;
    private User user;

    public static String COMMISSION_EXTRA = "commission_extra";
    public static String USER_EXTRA = "user_extra";
    Logger logger = Logger.getLogger(getClass().getSimpleName());

    public static FilterCommissionsFragment create(User user) {
        FilterCommissionsFragment f = new FilterCommissionsFragment();
        f.setRetainInstance(true);
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("user")) {
            user = (User) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_filter_commissions, container, false);
        teritorialCodeET = (EditText) v.findViewById(R.id.filter_commissions_edittext_teritorial_code);
        commissionNumberET = (EditText) v.findViewById(R.id.filter_commissions_edittext_coomission_number);
        searchCommission = (Button) v.findViewById(R.id.filter_commissions_search);

        ((OpenPKWActivity) getActivity()).setStepNo(v.findViewById(R.id.step), 3);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchCommission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (teritorialCodeET.getText().toString() != null && teritorialCodeET.getText().toString().length() == 6) {
                    String commissionNumberWithoutZero = commissionNumberET.getText().toString().replaceFirst("^0+(?!$)", "");
                    if (!commissionNumberWithoutZero.equals("") || !commissionNumberWithoutZero.equals("0")) {
                        new SearchCommission().execute(teritorialCodeET.getText().toString().trim(), commissionNumberWithoutZero);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getActivity().getString(R.string.filter_commissions_edittext_no_commission_number), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getActivity().getString(R.string.filter_commissions_edittext_no_teritorial_code_is_short), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    class SearchCommission extends AsyncTask<String, String, Commission> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle(null);
            progressDialog.setMessage(getActivity().getString(R.string.filter_commissions_progress_dialog_search));
            progressDialog.show();
        }

        @Override
        protected Commission doInBackground(String... params) {
            BufferedReader br = null;
            String line;
            String cvsSplitBy = "\\|";
            Commission commission = null;

            try {
                br = new BufferedReader(
                        new InputStreamReader(getActivity().getAssets().open("pollstations.csv")));
                while ((line = br.readLine()) != null) {
                    String[] commissionRow = line.split(cvsSplitBy);
                    if (commissionRow[0].trim().contains(params[0]) && commissionRow[1].trim().equals(params[1])) {
                        commission = new Commission();
                        commission.setCommissionCity(commissionRow[2]);
                        commission.setCommissionNumber(commissionRow[1]);
                        if (commissionRow[0].trim().length() > 6) {
                            commission.setPkwId(commissionRow[0].trim().substring(1) + "-" + commissionRow[1].trim());
                        } else {
                            commission.setPkwId(commissionRow[0].trim() + "-" + commissionRow[1].trim());
                        }

                        commission.setName(commissionRow[7]);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(commissionRow[4]);
                        stringBuilder.append(" ");
                        stringBuilder.append(commissionRow[5]);
                        if (!commissionRow[6].trim().equals("")) {
                            stringBuilder.append("/");
                            stringBuilder.append(commissionRow[6]);
                        }
                        stringBuilder.append(", ");
                        stringBuilder.append(commissionRow[3]);
                        stringBuilder.append(" ");
                        stringBuilder.append(commissionRow[2]);
                        commission.setAddress(stringBuilder.toString());

                        break;
                    }
                }
            } catch (IOException e) {
                logger.warning(e.getLocalizedMessage());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        logger.warning(e.getLocalizedMessage());
                    }
                }
            }
            return commission;
        }

        @Override
        protected void onPostExecute(Commission commission) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (commission != null) {
                if (getActivity() != null && NetworkManager.isOnline(getActivity().getApplicationContext())) {
                    Intent votingFormActivity = new Intent(getActivity(), VotingFormActivity.class);
                    votingFormActivity.putExtra(COMMISSION_EXTRA, commission);
                    votingFormActivity.putExtra(USER_EXTRA, user);
                    startActivity(votingFormActivity);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            getActivity().getString(R.string.network_check_internet_connection),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), getActivity().getString(R.string.filter_commissions_no_commission_found), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

}
