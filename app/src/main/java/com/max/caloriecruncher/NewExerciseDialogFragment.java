package com.max.caloriecruncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class NewExerciseDialogFragment extends DialogFragment {

    Exercise exercise;
    Double last_cals = 0.0;

    public NewExerciseDialogFragment(Exercise ex) {
        exercise = ex;
    }
    public NewExerciseDialogFragment() {
        exercise = new Exercise();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View diag;
        builder.setView(diag = inflater.inflate(R.layout.new_exercise_dialogue_prompt, null))
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        System.out.println("setting exercise params");

                        if (((EditText) diag.findViewById(R.id.calories)).getText().toString() == "" ||
                                ((EditText) diag.findViewById(R.id.ex_amount)).getText().toString() == "") {
                            MainActivity.adding.set_cal_ratio(0.0);
                        } else {
                            MainActivity.adding.set_cal_ratio(Double.parseDouble(((EditText) diag.findViewById(R.id.ex_amount)).getText().toString()) /
                                    (Double.parseDouble(((EditText) diag.findViewById(R.id.calories)).getText().toString()) / 100));
                        }

                        MainActivity.adding.set_name(((EditText) diag.findViewById(R.id.new_name)).getText().toString());
                        MainActivity.adding.set_type(((Spinner) diag.findViewById(R.id.units)).getSelectedItem().toString());
                        System.out.println(exercise.getExName());
                        System.out.println(MainActivity.adding.getExName());
                        ((NoticeDialogListener) getActivity()).onDialogPositiveClick();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NewExerciseDialogFragment.this.getDialog().cancel();
                    }
                });


        List<String> unit_types = new ArrayList<>();
        unit_types.add("minutes");
        unit_types.add("reps");

        Spinner unit_selection = (Spinner) diag.findViewById(R.id.units);


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, unit_types);

        // attaching data adapter to spinner
        unit_selection.setAdapter(dataAdapter);

        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        void onDialogPositiveClick();
        void onDialogNegativeClick();
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
