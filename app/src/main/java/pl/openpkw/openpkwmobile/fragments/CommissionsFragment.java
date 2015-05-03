package pl.openpkw.openpkwmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.CommissionsActivity;
import pl.openpkw.openpkwmobile.activities.VotingFormActivity;
import pl.openpkw.openpkwmobile.models.Commission;
import pl.openpkw.openpkwmobile.models.User;
import pl.openpkw.openpkwmobile.views.adapters.CommissionsArrayAdapter;

/**
 * Created by fockeRR on 28.04.15.
 */
public class CommissionsFragment extends ListFragment {

    private TextView mElectionInfo;
    private Button mAddCommission;
    private Button mNext;

    private ListView mCommissions;
    private CommissionsArrayAdapter commissionsArrayAdapter;
    private ArrayList<Commission> commissions = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_commissions, container, false);
        mElectionInfo = (TextView) v.findViewById(R.id.commissions_info_election);
        mCommissions = (ListView) v.findViewById(android.R.id.list);
        mAddCommission = (Button) v.findViewById(R.id.commissions_add);
        mNext = (Button) v.findViewById(R.id.commissions_next);


        commissionsArrayAdapter = new CommissionsArrayAdapter(getActivity(), R.layout.row_commission, commissions);

        this.setListAdapter(commissionsArrayAdapter);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    Intent fvIntent = new Intent(getActivity(), VotingFormActivity.class);
                    fvIntent.putExtra("commission", commissionsArrayAdapter.getSelected());
                    startActivity(fvIntent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.commissions_toast_selectcommision), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mAddCommission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        populateDummmyData();
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Commission item = (Commission) getListAdapter().getItem(position);
        commissionsArrayAdapter.setSelectedId(item.getId());
        getListView().invalidateViews();

    }

    private boolean validate() {
        return commissionsArrayAdapter.getSelected() != null;
    }

    private void populateDummmyData() {
        mElectionInfo.setText("Wybory na Prezydenta Rzeczypospolitej Polskiej\n" +
                "10 Maja 2015 - I Tura");

        Commission com1 = new Commission();
        com1.setId(1);
        com1.setPkwId("106101-1");
        com1.setName("Obwodowa Komisja Wyborcza nr: 2");
        com1.setAddress("Szkoła Podstawowa nr 313 im. Bolka i Lolka ul. Kantowa 32, 91-220 Łódź");
        commissions.add(com1);

        Commission com2 = new Commission();
        com2.setId(2);
        com2.setPkwId("106101-2");
        com2.setName("Obwodowa Komisja Wyborcza nr: 4");
        com2.setAddress("Przedszkole Dwujęzyczne pod Patronatem Rumcajsa ul. Przekręt 6, 91-221 Łódź");
        commissions.add(com2);

        Commission com3 = new Commission();
        com3.setId(3);
        com3.setPkwId("106101-3");
        com3.setName("Obwodowa Komisja Wyborcza nr: 5");
        com3.setAddress("Zespół Szkół Rzemiosła im. Jana Kilińskiego ul. Liściasta 181, 91-220 Łódź");
        commissions.add(com3);

        Commission com4 = new Commission();
        com4.setId(4);
        com4.setPkwId("106101-4");
        com4.setName("Obwodowa Komisja Wyborcza nr: 12");
        com4.setAddress("Żłobek im Misia Puchatka ul. Niewiarygodna 69, 91-222 Łódź");
        commissions.add(com4);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                commissionsArrayAdapter.notifyDataSetChanged();
            }
        });

    }

}
