package group7.scavengerhaunt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Kevin on 4/1/2017.
 */

public class EndGameFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You won!")
            .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //getActivity().finish();
                dismiss();
            }
        })
            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getActivity().finish();
                    dismiss();
                }
            });

        return builder.create();
    }

}
