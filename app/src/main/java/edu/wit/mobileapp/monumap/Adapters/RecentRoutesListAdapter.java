package edu.wit.mobileapp.monumap.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.wit.mobileapp.monumap.Entities.Route;
import edu.wit.mobileapp.monumap.R;

public class RecentRoutesListAdapter extends ArrayAdapter<Route> {
    private LayoutInflater mInflater;

    public RecentRoutesListAdapter(Context context, int rid, List<Route> list) {
        super(context, rid, list);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Route route = getItem(position);
        View view = mInflater.inflate(R.layout.recent_routes_list_item, null);

        // set route start location
        TextView routeStart = view.findViewById(R.id.recent_route_start_location_name);
        routeStart.setText(getContext().getResources().getString(R.string.recent_routes_start_location, route.getStart().getLocationName()));

        // set route destination location
        TextView routeDestination = view.findViewById(R.id.recent_route_destination_location_name);
        routeDestination.setText(getContext().getResources().getString(R.string.recent_routes_destination_location, route.getDestination().getLocationName()));

        // set route duration
        TextView routeDuration = view.findViewById(R.id.recent_route_duration);
        routeDuration.setText(getContext().getResources().getString(R.string.recent_routes_duration, route.getDuration()));

        // set route distance
        TextView routeDistance = view.findViewById(R.id.recent_route_distance);
        routeDistance.setText(getContext().getResources().getString(R.string.recent_routes_distance, route.getDistance()));

        return view;
    }
}
