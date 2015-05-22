package pl.openpkw.openpkwmobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import pl.openpkw.openpkwmobile.R;
import pl.openpkw.openpkwmobile.activities.OpenPKWActivity;
import pl.openpkw.openpkwmobile.models.UserRegister;
import pl.openpkw.openpkwmobile.network.RestClient;
import pl.openpkw.openpkwmobile.utils.EmailValidator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Wojciech Radzioch on 12.05.15.
 */
public class RegisterFragment extends Fragment {

    private EditText firstNameET, lastNameET, emailET, phoneET;
    private Button registerBT;
    private Context appContext;
    private String apiToken, apiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        appContext = getActivity().getApplicationContext();
        firstNameET = (EditText) v.findViewById(R.id.register_edittext_firstname);
        lastNameET = (EditText) v.findViewById(R.id.register_edittext_lastname);
        emailET = (EditText) v.findViewById(R.id.register_edittext_email);
        phoneET = (EditText) v.findViewById(R.id.register_edittext_phone);
        registerBT = (Button) v.findViewById(R.id.register_button_register);
        apiToken = getActivity().getString(R.string.api_token);
        apiClient = getActivity().getString(R.string.api_client);

        registerBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameET.getText().toString();
                String lastName = lastNameET.getText().toString();
                String email = emailET.getText().toString();
                String phone = phoneET.getText().toString();

                if(firstName.length()>0 && lastName.length()>0 && email.length()>0) {
                    if (EmailValidator.isEmailValid(email)) {
                        checkIsEmailExists(firstName, lastName, email, phone);
                    } else {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.toast_prestore_emailnotvalid), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.register_fill_all_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((OpenPKWActivity)getActivity()).setStepNo(v.findViewById(R.id.step), 2);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void checkIsEmailExists (final String firstName, final String lastName, final String email, final String phone) {
        Callback<String> checkEmailCallback = new Callback<String>() {
            @Override
            public void success(String responseString, Response response) {
                register(firstName, lastName, email, phone);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        RestClient.get(appContext).checkIsEmailExists(apiClient, apiToken, email, checkEmailCallback);
    }

    private void register(String firstName, String lastName, String email, String phone) {
        UserRegister userRegister = new UserRegister();
        userRegister.setFirstname(firstName);
        userRegister.setLastname(lastName);
        userRegister.setEmail(email);
        if (!phone.equals("")) {
            userRegister.setPhone(phone);
        }

        Callback<String> registerCallback = new Callback<String>() {
            @Override
            public void success(String responseString, Response response) {
                getActivity().finish();
                Toast.makeText(getActivity(), getActivity().getString(R.string.register_ok), Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        RestClient.get(appContext).register(apiClient, apiToken, userRegister, registerCallback);

    }
}
