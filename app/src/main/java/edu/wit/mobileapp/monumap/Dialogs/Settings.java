package edu.wit.mobileapp.monumap.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ToggleButton;

import edu.wit.mobileapp.monumap.R;

public class Settings extends DialogFragment {
    private SharedPreferences sharedPreferences;

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_settings, null);

        // set buttons based on current usersettings
        final ToggleButton wheelchairButton = view.findViewById(R.id.settings_wheelchair_toggle);
        wheelchairButton.setChecked(sharedPreferences.getBoolean(getString(R.string.sp_wheelchairAccessibilityEnabled), false));
        final ToggleButton textToSpeechButton = view.findViewById(R.id.settings_text_to_speech_toggle);
        textToSpeechButton.setChecked(sharedPreferences.getBoolean(getString(R.string.sp_textToSpeechEnabled), false));

        builder.setTitle(R.string.settings_title)
                .setPositiveButton(R.string.settings_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // update usersettings based on checked vals
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(getString(R.string.sp_wheelchairAccessibilityEnabled), wheelchairButton.isChecked());
                        editor.putBoolean(getString(R.string.sp_textToSpeechEnabled), textToSpeechButton.isChecked());
                        editor.apply();
                    }
                })
                .setNegativeButton(R.string.settings_cancel, null)
                .setView(view);

        return builder.create();
    }
}
