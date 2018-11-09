package com.manveerbasra.ontime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class AddAlarmActivity extends AppCompatActivity implements SetRepeatDaysDialogFragment.OnDialogCompleteListener {

    String[] daysOfWeek;
    TextView timeTextView;
    TextView repeatTextView;
    Calendar calendar;
    Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        daysOfWeek = getResources().getStringArray(R.array.days_of_week);
        calendar = Calendar.getInstance();
        alarm = new Alarm();

        setInitialAlarmTime();
        setInitialRepetition();
        addSetTimeLayoutListener();
        addSetRepeatLayoutListener();
    }


    /**
     * Initialize timeTextView with current time
     */
    private void setInitialAlarmTime() {
        // Get time and set it to alarm time TextView
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String currentTime = getFormattedTime(hour, minute);
        timeTextView = findViewById(R.id.add_alarm_time_text);
        timeTextView.setText(currentTime);
    }

    /**
     * Initialize timeTextView with current time
     */
    private void setInitialRepetition() {
        repeatTextView = findViewById(R.id.add_alarm_repeat_text);
        repeatTextView.setText(getString(R.string.never));
    }

    /**
     * When timeChangeLayout is selected, open TimePickerDialog
     */
    private void addSetTimeLayoutListener() {
        // Get layout view
        RelativeLayout setTimeButton = findViewById(R.id.add_alarm_time_layout);

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(AddAlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String formattedTime = getFormattedTime(selectedHour, selectedMinute);
                        timeTextView.setText(formattedTime);
                    }
                }, hour, minute, false);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });
    }

    /**
     * When repeatChangeLayout is selected, open SetRepeatDaysDialogFragment
     */
    private void addSetRepeatLayoutListener() {
        // Get layout view
        RelativeLayout setRepeatButton = findViewById(R.id.add_alarm_repeat_layout);

        setRepeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetRepeatDaysDialogFragment setRepeatDaysDialogFragment = new SetRepeatDaysDialogFragment();
                Bundle args = getBundle();


                setRepeatDaysDialogFragment.setArguments(args);
                // Display dialog
                setRepeatDaysDialogFragment.show(getSupportFragmentManager(), "A");
            }
        });
    }

    @NonNull
    private Bundle getBundle() {
        Bundle args = new Bundle();

        // Get current active days and convert them to an ArrayList of ints
        String[] activeDays = alarm.getActiveDays();

        // Populate an array of 7 false's
        ArrayList<Boolean> activeDaysBooleans = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            activeDaysBooleans.add(false);
        }

        if (activeDays != null && activeDays.length != 0) { // there are some selected repeat days
            for (String day : activeDays) {
                int i = Arrays.asList(daysOfWeek).indexOf(day);
                activeDaysBooleans.set(i, true);
            }
        }

        // Pass current active days to DialogFragment so it can display selections
        boolean[] activeDaysArray = new boolean[activeDaysBooleans.size()];
        int index = 0;
        for (Boolean object : activeDaysBooleans) {
            activeDaysArray[index++] = object;
        }
        args.putBooleanArray("activeDays", activeDaysArray);
        return args;
    }

    /**
     * This method is called when SetRepeatDaysDialogFragment completes, we get the selectedDays
     * and apply that to alarm
     *
     * @param selectedDaysBools integer array of selected days of the week
     */
    public void onDialogComplete(boolean[] selectedDaysBools) {
        if (selectedDaysBools.length > 0) {
            ArrayList<String> selectedDays = new ArrayList<>();


            int i = 0;
            for (boolean bool : selectedDaysBools) {
                if (bool) {
                    selectedDays.add(daysOfWeek[i]);
                }
                i++;
            }

            System.out.println(selectedDays);

            // Convert selectedDays to Array and apply that to alarm
            String[] activeDays = new String[selectedDays.size()];
            activeDays = selectedDays.toArray(activeDays);
            alarm.setRepeat(true);
            alarm.setActiveDays(activeDays);

            String formattedActiveDays = alarm.getStringOfActiveDays();
            repeatTextView.setText(formattedActiveDays);
        } else {
            String[] activeDays = new String[0];
            alarm.setRepeat(false);
            alarm.setActiveDays(activeDays);

            repeatTextView.setText(getString(R.string.never));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_alarm_save) {
            // TODO: Save Alarm
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Return a string of the form hh:mm aa from given hour and minute
     *
     * @param hour   integer of hour in 24h format
     * @param minute integer of minute
     * @return a String of formatted time
     */
    private String getFormattedTime(int hour, int minute) {
        String meridian = "AM";
        if (hour >= 12) {
            meridian = "PM";
        }

        if (hour > 12) {
            hour -= 12;
        } else if (hour == 0) {
            hour = 12;
        }

        String formattedTime;
        if (minute < 10) {
            formattedTime = hour + ":0" + minute + " " + meridian;
        } else {
            formattedTime = hour + ":" + minute + " " + meridian;
        }

        return formattedTime;
    }
}
