package edu.wit.mobileapp.monumap.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.wit.mobileapp.monumap.Adapters.RecentRoutesListAdapter;
import edu.wit.mobileapp.monumap.Entities.Route;
import edu.wit.mobileapp.monumap.R;

public class RecentRoutes extends DialogFragment {
    private ArrayList<Route> recentRoutes;
    private int numRoutes;
    private Route selectedRoute;


    public void setRecentRoutes(ArrayList<Route> recentRoutes, int numRoutes) {
        this.recentRoutes = recentRoutes;
        this.numRoutes = numRoutes;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_recent_routes, null);

        // set initial builder values
        builder.setTitle(getString(R.string.recent_routes_title))
                .setNegativeButton(getString(R.string.recent_routes_cancel), null)
                .setView(view);

        // check empty
        if(!recentRoutes.isEmpty()) {
            // remove text view
            TextView textView = view.findViewById(R.id.no_recent_routes_text);
            textView.setVisibility(View.GONE);

            // store only desired number of recent routes
            ArrayList<Route> tmpRecentRoutes = new ArrayList<>();
            if(numRoutes > recentRoutes.size()) {
                numRoutes = recentRoutes.size();
            }
            for(int i = 0; i < numRoutes; i++) {
                tmpRecentRoutes.add(recentRoutes.get(i));
            }

            // create list adapter for updated list
            final RecentRoutesListAdapter adapter = new RecentRoutesListAdapter(getActivity(), R.id.recent_routes_list, tmpRecentRoutes);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    selectedRoute = adapter.getItem(i);
                    // either calculate route or figure out how to hold onto value and not close dialog...
                    Toast.makeText(getActivity(), "Calculating route " + selectedRoute.getStart().getLocationName(), Toast.LENGTH_LONG).show();
                }
            });
        }

        return builder.create();
    }
}
