package group7.scavengerhaunt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Kevin on 4/1/2017.
 */

public class EndGameFragment extends DialogFragment {

    public static EndGameFragment newInstance(boolean won, int stageNum) {
        EndGameFragment frag = new EndGameFragment();
        Bundle args = new Bundle();
        args.putBoolean("result", won);
        args.putInt("stageNum", stageNum);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        boolean won = getArguments().getBoolean("result");
        final int stageNum = getArguments().getInt("stageNum");
        setCancelable(false);

        //Standard dialog Method
        if (won) {
            if (stageNum != -1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("You Escaped!")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Load next stage if possible
                                Intent intent = new Intent(getActivity(), GameActivity.class);
                                intent.putExtra("level", stageNum);
                                startActivity(intent);
                                getActivity().finish();
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
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("You Escaped!")
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().finish();
                                dismiss();
                            }
                        });
                return builder.create();
            }
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("You Were Caught!")
                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().recreate();
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

}
