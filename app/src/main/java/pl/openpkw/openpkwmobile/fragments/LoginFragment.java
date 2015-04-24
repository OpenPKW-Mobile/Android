package pl.openpkw.openpkwmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.PasswordRestoreActivity;

/**
 * Created by fockeRR on 21.04.15.
 */
public class LoginFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        Button restorePasswordBtn = (Button) v.findViewById(R.id.login_textlink_fpassword);
        SpannableString buttonText = new SpannableString(restorePasswordBtn.getText());
        buttonText.setSpan(new UnderlineSpan(), 0, buttonText.length(), 0);
        restorePasswordBtn.setText(buttonText);
        restorePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prestoreIntent = new Intent(getActivity(), PasswordRestoreActivity.class);
                startActivity(prestoreIntent);
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
