package pl.openpkw.openpkwmobile.fragments;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.Toast;

import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.PasswordRestoreActivity;
import pl.openpkw.openpkwmobile.activities.VotingFormActivity;
import pl.openpkw.openpkwmobile.models.Commission;
import pl.openpkw.openpkwmobile.models.User;
import pl.openpkw.openpkwmobile.network.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by fockeRR on 21.04.15.
 */
public class LoginFragment extends Fragment {
    private EditText mLogin;
    private EditText mPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        mLogin = (EditText) v.findViewById(R.id.login_edittext_user);
        mPassword = (EditText) v.findViewById(R.id.login_edittext_password);
        Button restorePasswordBtn = (Button) v.findViewById(R.id.login_textlink_fpassword);
        Button loginButton = (Button) v.findViewById(R.id.login_button_login);
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
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = mLogin.getText().toString();
                String password = mPassword.getText().toString();
                if (validate(login, password)) {
                    login(login, password);
                }
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Simple validation of login and password
     *
     * @param login
     * @param password
     * @return result of validation
     */
    private boolean validate(String login, String password) {
        if (login == null || password == null)
            return false;
        login = login.trim();
        password = password.trim();
        if (login.isEmpty() || password.isEmpty())
            return false;
        else
            return true;
    }

    /**
     * Method for authenticating users
     *
     * @param login
     * @param password
     */
    private void login(String login, String password) {

        //creating callback which runs in the main thread
        Callback<User> loginCallback = new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                //TODO: change to for other activity ChooseCommisionActivity or similar if implemented
                Intent fvIntent = new Intent(getActivity(), VotingFormActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("user", user);
                /*
                        "id": 1,
                        "pkwId": "106101-1",
                        "name": "Studio Consulting Sp. z o.o.",
                        "address": "ul. Romanowska 55F, 91-174 Łódź",
                        "protokolCount": 2
                 */

                Commission dummyCommision = new Commission();
                dummyCommision.setId(1);
                dummyCommision.setAddress("ul. Romanowska 55F, 91-174 Łódź");
                dummyCommision.setPkwId("106101-1");
                dummyCommision.setName("Studio Consulting Sp. z o.o.");
                extra.putParcelable("commission", dummyCommision);
                fvIntent.putExtras(extra);
                startActivity(fvIntent);
                getActivity().finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        //running background network process of authentication
        //the result is handled by loginCallback
        Context appContext = getActivity().getApplicationContext();
        RestClient.get(appContext).login(login, password, loginCallback);

    }
}
