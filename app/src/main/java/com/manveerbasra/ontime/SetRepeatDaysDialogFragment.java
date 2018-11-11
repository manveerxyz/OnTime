package com.manveerbasra.ontime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Fragment used to display AlarmEntity active days to user
 */
public class SetRepeatDaysDialogFragment extends DialogFragment {

    /**
     * Listener for Dialog Completion
     *
     * Implemented in AddAlarmActivity
     */
    public interface OnDialogCompleteListener {
        void onDialogComplete(boolean[] selectedDays);
    }

    private OnDialogCompleteListener completeListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.completeListener = (OnDialogCompleteListener) activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    // End of DialogCompleteListener methods

    private boolean[] activeDays;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeDays = getArguments().getBooleanArray("activeDays");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // Set the dialog title
        builder.setTitle(R.string.set_repeat);
        // Specify the list array, the items to be selected by default (null for none),
        // and the listener through which to receive callbacks when items are selected
        builder.setMultiChoiceItems(R.array.days_of_week, activeDays,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedDay,
                                        boolean isChecked) {
                        activeDays[selectedDay] = isChecked;
                    }
                });
        // Set the action buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Return selectedDays to activity
                saveSelectedDays(activeDays);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        return builder.create();
    }

    /**
     * Saved selectedDays by calling completeListener's complete function
     * @param selectedDays ArrayList of selected days ints
     */
    private void saveSelectedDays(boolean[] selectedDays) {
        this.completeListener.onDialogComplete(selectedDays);
    }
}
