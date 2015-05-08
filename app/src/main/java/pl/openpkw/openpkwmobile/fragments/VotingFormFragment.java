package pl.openpkw.openpkwmobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.models.Candidate;
import pl.openpkw.openpkwmobile.models.Commission;
import pl.openpkw.openpkwmobile.models.CommissionDetails;
import pl.openpkw.openpkwmobile.models.User;
import pl.openpkw.openpkwmobile.network.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by fockeRR on 28.04.15.
 */
public class VotingFormFragment extends Fragment {
    private ScrollView mScrollView;
    private TextView mCommisionNumber;
    private TextView mCommisionId;
    private TextView mCommisionName;
    private TextView mCommisionAddress;
    private TableLayout mCandidates;
    private TableLayout mGeneralData;
    private Button mNextButton;

    private EditText mAbleToVote;
    private EditText mCards;
    private EditText mValidCards;
    private EditText mInvalidVotes;
    private EditText mValidVotes;

    private CommissionDetails candidatesAndCommission = null;
    private User user = null;
    private Commission commission = null;
    private Map<Integer, Integer> votes = null;
    private Map<String, Integer> protocol = null;

    private int ableToVote;
    private int cards;
    private int validCards;
    private int invalidVotes;
    private int validVotes;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_voting_form, container, false);
        mScrollView = (ScrollView) v.findViewById(R.id.fvoting_scrollview);
        mCommisionNumber = (TextView) v.findViewById(R.id.fvoting_commision_number);
        mCommisionId = (TextView) v.findViewById(R.id.fvoting_commision_id);
        mCommisionName = (TextView) v.findViewById(R.id.fvoting_commision_name);
        mCommisionAddress = (TextView) v.findViewById(R.id.fvoting_commision_address);
        mGeneralData = (TableLayout) v.findViewById(R.id.fvoting_generaldata);
        mNextButton = (Button) v.findViewById(R.id.fvoting_next_button);
        mAbleToVote = (EditText) v.findViewById(R.id.fvoting_abletovote);
        mCards = (EditText) v.findViewById(R.id.fvoting_cards);
        mValidCards = (EditText) v.findViewById(R.id.fvoting_validcards);
        mInvalidVotes = (EditText) v.findViewById(R.id.fvoting_invalidvotes);
        mValidVotes = (EditText) v.findViewById(R.id.fvoting_validvotes);
        mCandidates = (TableLayout) v.findViewById(R.id.fvoting_candidates);

        mNextButton.setOnClickListener(onNextButtonListener);

        return v;
    }

    public static VotingFormFragment create(User user, Commission commission) {
        VotingFormFragment votingFormFragment = new VotingFormFragment();
        votingFormFragment.setRetainInstance(true);
        Bundle args = new Bundle();
        args.putParcelable("commission", commission);
        args.putSerializable("user", user);
        votingFormFragment.setArguments(args);
        return votingFormFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        temporarySaveProtocol();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (candidatesAndCommission == null) {
            if (getArguments() != null && getArguments().containsKey("commission") && getArguments().containsKey("user")) {
                user = (User) getArguments().getSerializable("user");
                commission = (Commission) getArguments().get("commission");

                RestClient.get(getActivity()).getCandidates(user.getLogin(), user.getToken(), commission.getPkwId(), new Callback<CommissionDetails>() {
                    @Override
                    public void success(CommissionDetails commissionDetails, Response response) {
                        candidatesAndCommission = commissionDetails;
                        fillLayoutWithData(candidatesAndCommission);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getActivity(), getString(R.string.network_check_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            fillLayoutWithData(candidatesAndCommission);
        }


    }

    private void fillLayoutWithData(CommissionDetails cDetails) {
        mCommisionNumber.setText(cDetails.getOkregowa().getName());
        mCommisionId.setText(cDetails.getPkwId());
        mCommisionName.setText(cDetails.getName());
        mCommisionAddress.setText(cDetails.getAddress());


        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams nameParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.7f);
        TableRow.LayoutParams votesParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.2f);
        TableRow.LayoutParams orderParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.1f);
        InputFilter[] maxVotesLength = new InputFilter[1];
        maxVotesLength[0] = new InputFilter.LengthFilter(6);
        for (Candidate candidate : cDetails.getKandydatList()) {
            TableRow candidateRow = new TableRow(getActivity());
            String pkwIdTag = String.valueOf(candidate.getPkwId());
            candidateRow.setTag(pkwIdTag);
            candidateRow.setPadding(5, 5, 5, 5);
            TextView order = new TextView(getActivity());
            TextView name = new TextView(getActivity());
            EditText numberOfVotes = new EditText(getActivity());
            //order
            order.setText(String.valueOf(candidate.getPkwId()));
            //set name
            name.setText(candidate.getFullName());
            //setMaxLength
            numberOfVotes.setMaxLines(1);
            numberOfVotes.setTag("votes");
            numberOfVotes.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            numberOfVotes.setFilters(maxVotesLength);
            numberOfVotes.setGravity(Gravity.RIGHT);
            if (protocol != null && protocol.containsKey(pkwIdTag))
                numberOfVotes.setText(String.valueOf(protocol.get(pkwIdTag)));
            candidateRow.addView(order, orderParams);
            candidateRow.addView(name, nameParams);
            candidateRow.addView(numberOfVotes, votesParams);
            candidateRow.setWeightSum(1.0f);
            mCandidates.addView(candidateRow);
        }
    }

    View.OnClickListener onNextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            validateForm(getActivity().getApplicationContext());
        }
    };

    private boolean validateForm(Context ctx) {
        boolean result = true;
        getSummary();
        getVotes();

        if (ableToVote == -1 || cards == -1 || validCards == -1 ||
                invalidVotes == -1 || validVotes == -1) {
            Toast.makeText(ctx,ctx.getString(R.string.fvoting_toast_notalldataentered), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (votes.keySet().size() != candidatesAndCommission.getKandydatList().size()) {
            Toast.makeText(ctx,ctx.getString(R.string.fvoting_toast_notallcandidatesvotesentered), Toast.LENGTH_SHORT).show();
            return false;
        }

        return result;
    }


    private void getSummary() {
        ableToVote = getInt(mAbleToVote.getText());
        cards = getInt(mCards.getText());
        validCards = getInt(mValidCards.getText());
        invalidVotes = getInt(mInvalidVotes.getText());
        validVotes = getInt(mValidVotes.getText());
    }

    private void temporarySaveProtocol() {
        if (protocol == null)
            protocol = new HashMap<String, Integer>();
        getSummary();
        getVotes();
    }

    private int getInt(Editable editable) {
        int result = -1;
        if (editable != null && editable.toString().trim().length() > 0) {
            result = Integer.parseInt(editable.toString().trim());
        }
        return result;
    }

    private void getVotes() {
        HashMap<Integer, Integer> results = new HashMap<>();
        if (mCandidates == null) {
            return;
        }

        for (int i = 0, j = mCandidates.getChildCount(); i < j; i++) {
            View view = mCandidates.getChildAt(i);
            if (view instanceof TableRow) {
                Integer pkwId = Integer.parseInt((String) view.getTag());
                EditText editWithVotes = (EditText) view.findViewWithTag("votes");
                Integer numberOfVotes = 0;
                if (pkwId != null && editWithVotes.getText() != null && editWithVotes.getText().length() > 0) {
                    numberOfVotes = Integer.parseInt(editWithVotes.getText().toString().trim());
                    results.put(pkwId, numberOfVotes);
                    protocol.put(String.valueOf(pkwId), numberOfVotes);
                }

            }
        }
        votes = results;
    }
}
