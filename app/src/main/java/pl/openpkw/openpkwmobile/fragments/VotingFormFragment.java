package pl.openpkw.openpkwmobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.FilterCommissionsActivity;
import pl.openpkw.openpkwmobile.activities.TakePhotosActivity;
import pl.openpkw.openpkwmobile.concurrent.GetCommisionDetailsCallback;
import pl.openpkw.openpkwmobile.concurrent.GetCommissionDetailsAT;
import pl.openpkw.openpkwmobile.managers.OfflineStorage;
import pl.openpkw.openpkwmobile.models.Candidate;
import pl.openpkw.openpkwmobile.models.Commission;
import pl.openpkw.openpkwmobile.models.CommissionDetails;
import pl.openpkw.openpkwmobile.models.Protocol;
import pl.openpkw.openpkwmobile.models.User;
import pl.openpkw.openpkwmobile.network.RestClient;
import pl.openpkw.openpkwmobile.views.CustomAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fockeRR on 28.04.15.
 */
public class VotingFormFragment extends Fragment {
    public static final int DIALOG_FRAGMENT = 1;
    public static final String TAG = "VotingFormFragment";

    private ScrollView mScrollView;
//    private TextView mCommisionNumber;
    private TextView mCommisionId;
    private TextView mCommisionName;
    private TextView mCommisionAddress;
    private TextView mCandidatesHeader;
    private TableLayout mCandidates;
    private LinearLayout mGeneralData;
    private TextView mSoftError1;
    private TextView mSoftError2;
    private TextView mSoftError3;
    private LinearLayout mSoftContainer;
    private RelativeLayout mProgress;

    private Button mNextButton;
    private Button mChangeCommisionBtn;

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
    private GetCommisionDetailsCallback callback;

    private int ableToVote;
    private int cards;
    private int validCards;
    private int invalidVotes;
    private int validVotes;
    private int totalVotes;
    private int click;

    private int terit_code;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_voting_form, container, false);
        mScrollView = (ScrollView) v.findViewById(R.id.fvoting_scrollview);
//        mCommisionNumber = (TextView) v.findViewById(R.id.fvoting_commision_number);
        mCommisionId = (TextView) v.findViewById(R.id.fvoting_commision_id);
        mCommisionName = (TextView) v.findViewById(R.id.fvoting_commision_name);
        mCommisionAddress = (TextView) v.findViewById(R.id.fvoting_commision_address);
        mGeneralData = (LinearLayout) v.findViewById(R.id.fvoting_generaldata);
        mNextButton = (Button) v.findViewById(R.id.fvoting_next_button);
        mChangeCommisionBtn = (Button) v.findViewById(R.id.fvoting_change_commision_button);
        mAbleToVote = (EditText) v.findViewById(R.id.fvoting_abletovote);
        mCards = (EditText) v.findViewById(R.id.fvoting_cards);
        mValidCards = (EditText) v.findViewById(R.id.fvoting_validcards);
        mInvalidVotes = (EditText) v.findViewById(R.id.fvoting_invalidvotes);
        mValidVotes = (EditText) v.findViewById(R.id.fvoting_validvotes);
        mCandidates = (TableLayout) v.findViewById(R.id.fvoting_candidates);
        mCandidatesHeader = (TextView) v.findViewById(R.id.tvcoting_candidates_heading);
        mSoftContainer = (LinearLayout) v.findViewById(R.id.ll_soft_error_container);
        mProgress = (RelativeLayout) v.findViewById(R.id.fvoting_progress);
        mSoftError1 = (TextView) v.findViewById(R.id.tvSoftError1);
        mSoftError2 = (TextView) v.findViewById(R.id.tvSoftError2);
        mSoftError3 = (TextView) v.findViewById(R.id.tvSoftError3);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNextButton.setOnClickListener(onNextButtonListener);
        mChangeCommisionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: nice to have a confirmation dialog here!
                Intent changeCommision = new Intent(getActivity(), FilterCommissionsActivity.class);
                changeCommision.putExtra("user", user);
                startActivity(changeCommision);
                getActivity().finish();
            }
        });

        /**
         * callback which handles data passed from the called asynctask below}
         */
        callback = new GetCommisionDetailsCallback() {
            @Override
            public void onError(Exception e) {
                Toast.makeText(getActivity(), getString(R.string.network_check_internet_connection), Toast.LENGTH_SHORT).show();
                mProgress.setVisibility(View.GONE);
                mScrollView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(CommissionDetails commissionDetails) {
                candidatesAndCommission = commissionDetails;
                fillLayoutWithData(candidatesAndCommission);
                mProgress.setVisibility(View.GONE);
                mScrollView.setVisibility(View.VISIBLE);
            }
        };

        click = 0;
        if (candidatesAndCommission == null) {
            if (getArguments() != null && getArguments().containsKey("commission") && getArguments().containsKey("user")) {
                user = (User) getArguments().getSerializable("user");
                commission = (Commission) getArguments().get("commission");
                mProgress.setVisibility(View.VISIBLE);
                // executing background job
                GetCommissionDetailsAT backgroundJob = new GetCommissionDetailsAT(getActivity().getApplicationContext(), user, commission.getPkwId(), callback);
                backgroundJob.execute();
            }
        } else {
            fillLayoutWithData(candidatesAndCommission);
            mProgress.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        temporarySaveProtocol();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    private void fillLayoutWithData(CommissionDetails cDetails) {
       // mCommisionNumber.setText(cDetails.getOkregowa().getName());
        mCommisionId.setText(cDetails.getPkwId());
        mCommisionName.setText(cDetails.getName());
        mCommisionAddress.setText(cDetails.getAddress());

        terit_code = Integer.parseInt(cDetails.getPkwId().substring(0, 6));


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
            if (protocol != null && protocol.containsKey("k" + pkwIdTag))
                numberOfVotes.setText(String.valueOf(protocol.get("k" + pkwIdTag)));
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
            click++;
            Log.d(TAG, "clicks = " + click);

            if (validateForm(getActivity().getApplicationContext())) {
                Log.d(TAG, "validForm = OK");

                if (softErrorsValidation()) {
                    // Run Activity!
                    runTakePhoto();
                    Log.d(TAG, "validForm = OK / SoftValidation OK = RUN APP");
                } else {
                    if (click == 1) {
                        // Do nothing, errors were showed
                        Log.d(TAG, "validForm = OK / SoftValidation NOK = WAIT");
                    } else {
                        // Run activity - soft errors were showed!
                        runTakePhoto();
                        Log.d(TAG, "validForm = OK / SoftValidation NOK = RUN APP");
                    }
                }
            } else {
                Log.d(TAG, "validForm = NOK");
            }
        }

        ;
    };

    private void runTakePhoto() {
        RestClient.get(getActivity().getApplicationContext()).submitProtocol(user.getLogin(), user.getToken(), commission.getPkwId(), protocol, new Callback<Void>() {

            @Override
            public void success(Void aVoid, Response response) {
                Context ctx = getActivity().getApplicationContext();
                Toast.makeText(ctx, ctx.getString(R.string.fvoting_protocol_successfully_sent), Toast.LENGTH_LONG).show();

                Intent takePhoto = new Intent(getActivity(), TakePhotosActivity.class);
                takePhoto.putExtra(TakePhotosActivity.COMMISSION_ID, commission.getCommissionNumber());
                takePhoto.putExtra(TakePhotosActivity.PKW_ID, commission.getPkwId());
                startActivity(takePhoto);
                getActivity().finish();
            }

            @Override
            public void failure(RetrofitError error) {
                OfflineStorage.addProtocolForUpload(getActivity().getApplicationContext(), new Protocol(protocol, commission.getPkwId()));
                Context ctx = getActivity().getApplicationContext();
                Toast.makeText(ctx, ctx.getString(R.string.fvoting_protocol_send_later), Toast.LENGTH_LONG).show();

                Intent takePhoto = new Intent(getActivity(), TakePhotosActivity.class);
                startActivity(takePhoto);
                getActivity().finish();
            }
        });
    }

    private boolean validateForm(Context ctx) {
        if (areFieldsFilled(ctx)) {
            if (validation1(ctx) && validation3(ctx) && validation5(ctx)) {
                hideallErrors();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /*Liczba osób, którym wydano karty do głosowania (pkt. 4),
    nie może być większa od liczby wyborców uprawnionych do
    głosowania (pkt. 1).*/
    private boolean validation1(Context ctx) {
        if (cards > ableToVote) {
            showDialog(ctx, getResources().getString(R.string.validation_1));
            mCards.setError(getResources().getString(R.string.error));
            mScrollView.fullScroll(View.FOCUS_UP);
            return false;
        } else {
            hideallErrors();
            return true;
        }
    }

    /*Liczba kart wyjętych z urny (pkt. 9)
    musi być równa sumie liczby kart nieważnych (pkt. 10)
    i liczby kart ważnych (pkt. 11).*/
    private boolean validation2(Context ctx) {
        if (cards != (validVotes + invalidVotes)) {
            showDialog(ctx, getResources().getString(R.string.validation_2));
            mCards.setError(getResources().getString(R.string.error));
            mInvalidVotes.setError(getResources().getString(R.string.error));
            mValidVotes.setError(getResources().getString(R.string.error));
            mScrollView.fullScroll(View.FOCUS_UP);
            return false;
        }
        hideallErrors();
        return true;
    }

    /*Liczba kart ważnych (pkt. 11) musi być równa sumie
    liczby głosów nieważnych (pkt. 12) i liczby głosów ważnych
    oddanych na wszystkich kandydatów (pkt. 13).*/
    private boolean validation3(Context ctx) {
        if (validCards != (totalVotes + invalidVotes)) {
            showDialog(ctx, getResources().getString(R.string.validation_3));
            mInvalidVotes.setError(getResources().getString(R.string.error));
            mValidVotes.setError(getResources().getString(R.string.error));
            mCandidatesHeader.setError(getResources().getString(R.string.error));
            mScrollView.fullScroll(View.FOCUS_UP);
            return false;
        } else {
            hideallErrors();
            return true;
        }
    }

    /*P8 Suma głosów oddanych na wszystkich kandydatów
    (pkt. 14 pole RAZEM) nie może być różna od sumy głosów oddanych
     na poszczególnych kandydatów (w pkt. 14).	*/
    private boolean validation4(Context ctx) {
        if (totalVotes != validVotes) {
            showDialog(ctx, getResources().getString(R.string.validation_4));
            mCandidatesHeader.setError(getResources().getString(R.string.error));
            mScrollView.fullScroll(View.FOCUS_UP);
            return false;
        }
        hideallErrors();
        return true;
    }

    /*P9 Suma głosów oddanych na wszystkich kandydatów (w pkt. 14)
    musi być równa liczbie głosów ważnych (w pkt. 13).*/
    private boolean validation5(Context ctx) {
        if (totalVotes != validVotes) {
            showDialog(ctx, getResources().getString(R.string.validation_5));
            mValidVotes.setError(getResources().getString(R.string.error));
            mCandidatesHeader.setError(getResources().getString(R.string.error));
            mScrollView.fullScroll(View.FOCUS_UP);
            return false;
        } else {
            hideallErrors();
            return true;
        }
    }

    /* Wszystkie liczby nie mogą byc większe niż 3000 (za wyjątkiem ZAGRANICY) */
    private boolean validation6(Context ctx) {
        int limit = getResources().getInteger(R.integer.max_values);
        Log.d(TAG, "limit =" + limit);
        String error = getResources().getString(R.string.error_limit) + " " + limit;

        if (!abroad()) {
            Log.d(TAG, "ABROAD = NO");
            if (ableToVote > limit) {
                mAbleToVote.setError(error);
                mScrollView.fullScroll(View.FOCUS_UP);
                return false;
            } else if (cards > limit) {
                mCards.setError(error);
                mScrollView.fullScroll(View.FOCUS_UP);
                return false;
            } else if (validCards > limit) {
                mValidCards.setError(error);
                mScrollView.fullScroll(View.FOCUS_UP);
                return false;
            } else if (invalidVotes > limit) {
                mInvalidVotes.setError(error);
                mScrollView.fullScroll(View.FOCUS_UP);
                return false;
            } else if (validVotes > limit) {
                mValidVotes.setError(error);
                mScrollView.fullScroll(View.FOCUS_UP);
                return false;
            } else {
                return false;
            }
        } else {
            hideallErrors();
            Log.d(TAG, "ABROAD = YES");
            return true;
        }
    }

    private boolean abroad() {
        int[] abroad_teri_codes = getResources().getIntArray(R.array.abroad_locations_array);


        for (int i = 0; i < abroad_teri_codes.length; i++) {
            if (terit_code == abroad_teri_codes[i]) {
                Log.d(TAG, "teri_codes = " + abroad_teri_codes[i]);
                return true;
            }
        }
        return false;
    }


    private void hideallErrors() {
        mAbleToVote.setError(null);
        mCards.setError(null);
        mValidCards.setError(null);
        mInvalidVotes.setError(null);
        mValidVotes.setError(null);
        mCandidatesHeader.setError(null);
    }

    private boolean softErrorsValidation() {
        if (cards > (0.9 * ableToVote)) {
            mSoftContainer.setVisibility(View.VISIBLE);
            mSoftError1.setVisibility(View.VISIBLE);
            return false;
        } else if (cards > ableToVote) {
            mSoftContainer.setVisibility(View.VISIBLE);
            mSoftError2.setVisibility(View.VISIBLE);
            return false;
        } else if (validCards > ableToVote) {
            mSoftContainer.setVisibility(View.VISIBLE);
            mSoftError3.setVisibility(View.VISIBLE);
            return false;
        } else {
            mSoftContainer.setVisibility(View.GONE);
            mSoftError1.setVisibility(View.GONE);
            mSoftError2.setVisibility(View.GONE);
            mSoftError3.setVisibility(View.GONE);
            return true;
        }
    }


    private void showDialog(Context ctx, String msg) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");

        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        DialogFragment df = CustomAlertDialog.newInstance(getResources().getString(R.string.alert_dialog_title), msg);
        df.setTargetFragment(this, DIALOG_FRAGMENT);
        df.show(getFragmentManager().beginTransaction(), "dialog");
    }

    private boolean areFieldsFilled(Context ctx) {
        getSummary();
        getVotes();

        if (ableToVote == -1 || cards == -1 || validCards == -1 ||
                invalidVotes == -1 || validVotes == -1) {
            Toast.makeText(ctx, ctx.getString(R.string.fvoting_toast_notalldataentered), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (votes.keySet().size() != candidatesAndCommission.getKandydatList().size()) {
            Toast.makeText(ctx, ctx.getString(R.string.fvoting_toast_notallcandidatesvotesentered), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void getSummary() {
        if (protocol == null)
            protocol = new HashMap<String, Integer>();
        ableToVote = getInt(mAbleToVote.getText());
        cards = getInt(mCards.getText());
        validCards = getInt(mValidCards.getText());
        invalidVotes = getInt(mInvalidVotes.getText());
        validVotes = getInt(mValidVotes.getText());

        protocol.put("glosujacych", ableToVote);
        protocol.put("glosowWaznych", validVotes);
        protocol.put("glosowNieWaznych", invalidVotes);
        protocol.put("kartWaznych", validCards);
        protocol.put("uprawnionych", cards);
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
        totalVotes = 0;
        HashMap<Integer, Integer> results = new HashMap<>();
        if (mCandidates == null) {
            return;
        }
        if (protocol == null)
            protocol = new HashMap<String, Integer>();

        for (int i = 0, j = mCandidates.getChildCount(); i < j; i++) {
            View view = mCandidates.getChildAt(i);
            if (view instanceof TableRow) {
                if (view.getTag() != null) {
                    int pkwId = Integer.parseInt((String) view.getTag());
                    EditText editWithVotes = (EditText) view.findViewWithTag("votes");
                    Integer numberOfVotes = 0;
                    if (editWithVotes.getText() != null && editWithVotes.getText().toString().trim().length() > 0) {
                        numberOfVotes = Integer.parseInt(editWithVotes.getText().toString().trim());
                        results.put(pkwId, numberOfVotes);
                        protocol.put("k" + String.valueOf(pkwId), numberOfVotes);
                        totalVotes += numberOfVotes;
                    }
                }

            }
        }
        votes = results;
    }
}
