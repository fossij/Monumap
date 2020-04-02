package edu.wit.mobileapp.monumap.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.wit.mobileapp.monumap.Entities.Route;
import edu.wit.mobileapp.monumap.R;

public class RecentRoutes extends DialogFragment {
    private ArrayList<Route> recentRoutes;

    public void setRecentRoutes(ArrayList<Route> recentRoutes) {
        this.recentRoutes = recentRoutes;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_recent_routes, null);

        // set initial builder values
        builder.setTitle(getString(R.string.recent_routes_title))
                .setNegativeButton(getString(R.string.recent_routes_cancel), null)
                .setView(view);

        // check empty
        if(!recentRoutes.isEmpty()) {
            // set text view
            builder.setPositiveButton(getString(R.string.recent_routes_calculate), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }

        return builder.create();
    }
}
