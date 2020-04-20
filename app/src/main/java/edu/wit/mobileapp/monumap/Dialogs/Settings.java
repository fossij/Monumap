package edu.wit.mobileapp.monumap.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ToggleButton;

import edu.wit.mobileapp.monumap.R;

public class Settings extends DialogFragment {
    private SharedPreferences sharedPreferences;
    private final Integer[] recentRoutesOptions = new Integer[]{1,2,3,4,5,10,20};

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_settings, null);

        // set settings values based on current usersettings
        final ToggleButton wheelchairButton = view.findViewById(R.id.settings_wheelchair_toggle);
        wheelchairButton.setChecked(sharedPreferences.getBoolean(getString(R.string.sp_wheelchairAccessibilityEnabled), false));
        final ToggleButton textToSpeechButton = view.findViewById(R.id.settings_text_to_speech_toggle);
        textToSpeechButton.setChecked(sharedPreferences.getBoolean(getString(R.string.sp_textToSpeechEnabled), false));
        final Spinner recentRoutesSpinner = view.findViewById(R.id.settings_recent_routes_spinner);
        ArrayAdapter<Integer> recentRoutesAdapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_item, recentRoutesOptions);
        recentRoutesSpinner.setAdapter(recentRoutesAdapter);
        recentRoutesSpinner.setSelection(sharedPreferences.getInt(getString(R.string.sp_recentRoutesPosition), 0));

        builder.setTitle(R.string.settings_title)
                .setPositiveButton(R.string.settings_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // update usersettings based on checked vals
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(getString(R.string.sp_wheelchairAccessibilityEnabled), wheelchairButton.isChecked());
                        editor.putBoolean(getString(R.string.sp_textToSpeechEnabled), textToSpeechButton.isChecked());
                        editor.putInt(getString(R.string.sp_recentRoutesPosition), recentRoutesSpinner.getSelectedItemPosition());
                        editor.putInt(getString(R.string.sp_recentRoutesNumber), recentRoutesOptions[recentRoutesSpinner.getSelectedItemPosition()]);
                        editor.apply();
                    }
                })
                .setNegativeButton(R.string.settings_cancel, null)
                .setView(view);

        return builder.create();
    }
}
