package edu.wit.mobileapp.monumap.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.wit.mobileapp.monumap.Entities.Instruction;
import edu.wit.mobileapp.monumap.R;

public class InstructionInfo extends DialogFragment {
    private Instruction instruction;
    private int instructionNum;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_instructions_info, null);

        // create instruction icon
        ImageView icon = view.findViewById(R.id.instruction_info_direction_icon);
        icon.setImageBitmap(this.instruction.getDirectionIcon());

        // create instruction text
        TextView instructionText = view.findViewById(R.id.instruction_info_instruction);
        instructionText.setText(this.instruction.getText());

        // create instruction duration
        TextView instructionDuration = view.findViewById(R.id.instruction_info_duration);
        instructionDuration.setText(getContext().getResources().getString(R.string.instruction_info_duration, String.valueOf(this.instruction.getDuration())));

        // create instruction distance
        TextView instructionDistance = view.findViewById(R.id.instruction_info_distance);
        instructionDistance.setText(getContext().getResources().getString(R.string.instruction_info_distance, String.valueOf(this.instruction.getDistance())));

        builder.setTitle(getContext().getResources().getString(R.string.instruction_info_title, this.instructionNum))
                .setPositiveButton(R.string.instruction_info_ok, null)
                .setView(view);

        return builder.create();
    }

    public void setInstruction(Instruction instruction, int instructionNum) {
        this.instruction = instruction;
        this.instructionNum = instructionNum;
    }
}
