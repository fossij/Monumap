package edu.wit.mobileapp.monumap.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.wit.mobileapp.monumap.Entities.Instruction;
import edu.wit.mobileapp.monumap.R;

public class InstructionsListAdapter extends ArrayAdapter<Instruction> {
    private LayoutInflater mInflater;

    public InstructionsListAdapter(Context context, int rid, List<Instruction> list) {
        super(context, rid, list);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Instruction instruction = getItem(position);
        View view = mInflater.inflate(R.layout.instructions_list_item, null);

        //  instruction direction icon
        ImageView icon = view.findViewById(R.id.instruction_direction_icon);
        instruction.setContext(getContext()); // must set context to avoid error in direction icon setting
        icon.setImageBitmap(instruction.getDirectionIcon());

        // instruction text
        TextView text = view.findViewById(R.id.instruction_text);
        text.setText(instruction.getText());

        // instruction duration
        TextView duration = view.findViewById(R.id.instruction_duration);
        duration.setText(String.valueOf(instruction.getDuration()));

        // instruction distance
        TextView distance = view.findViewById(R.id.instruction_distance);
        distance.setText(String.valueOf(instruction.getDistance()));

        return view;
    }
}