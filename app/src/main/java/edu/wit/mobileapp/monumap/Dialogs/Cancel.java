package edu.wit.mobileapp.monumap.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.wit.mobileapp.monumap.Home;
import edu.wit.mobileapp.monumap.R;

public class Cancel extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.cancel_message)
                .setTitle(R.string.cancel_title)
                .setPositiveButton(R.string.cancel_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // cancel route, go back to home screen
                        Intent intent = new Intent(getContext(), Home.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel_no, null);

        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cancel, container, false);
    }
}
