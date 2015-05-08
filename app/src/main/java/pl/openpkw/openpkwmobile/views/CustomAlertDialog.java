package pl.openpkw.openpkwmobile.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import pl.openpkw.openpkwmobile.R;

/**
 * Created by mkonkel on 2015-05-08.
 */
public class CustomAlertDialog extends DialogFragment {
    public static final String TITLE = "title";
    public static final String MESSAGE = "msg";

    public static CustomAlertDialog newInstance(String title, String msg) {
        CustomAlertDialog frag = new CustomAlertDialog();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, msg);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE);
        String msg = getArguments().getString(MESSAGE);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .create();
    }
}
