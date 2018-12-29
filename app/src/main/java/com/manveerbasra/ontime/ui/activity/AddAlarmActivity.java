package com.manveerbasra.ontime.ui.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.manveerbasra.ontime.MapsActivity;
import com.manveerbasra.ontime.R;
import com.manveerbasra.ontime.db.Alarm;
import com.manveerbasra.ontime.ui.SetRepeatDaysDialogFragment;

import java.util.Calendar;

public class AddAlarmActivity extends AppCompatActivity implements SetRepeatDaysDialogFragment.OnDialogCompleteListener {

    private final String TAG = "AddAlarmActivity";

    private static final int SET_START_LOCATION_ACTIVITY_REQUEST_CODE = 1;
    private static final int SET_END_LOCATION_ACTIVITY_REQUEST_CODE = 2;

    // Key values for returning intent.
    public static final String EXTRA_ID = "com.manveerbasra.ontime.ID";
    public static final String EXTRA_TIME = "com.manveerbasra.ontime.TIME";
    public static final String EXTRA_ACTIVE = "com.manveerbasra.ontime.ACTIVE";
    public static final String EXTRA_ACTIVE_DAYS = "com.manveerbasra.ontime.ACTIVEDAYS";
    public static final String EXTRA_DELETE = "com.manveerbasra.ontime.DELETE";

    // Alarm attributes.
    int alarmID;
    String time;
    boolean[] activeDays;
    // Data objects
    Calendar calendar;
    // View objects
    TextView timeTextView;
    TextView repeatTextView;
    TextView startLocTextView;
    Button deleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        calendar = Calendar.getInstance();
        deleteButton = findViewById(R.id.add_alarm_delete);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) { // Activity called to edit an alarm.
            alarmID = intent.getIntExtra(EXTRA_ID, -1);
            time = intent.getStringExtra(EXTRA_TIME);
            activeDays = intent.getBooleanArrayExtra(EXTRA_ACTIVE_DAYS);

            timeTextView = findViewById(R.id.add_alarm_time_text);
            repeatTextView = findViewById(R.id.add_alarm_repeat_text);

            timeTextView.setText(time);
            repeatTextView.setText(getStringOfActiveDays());
            setTitle(R.string.edit_alarm);

            addDeleteButtonListener();
        } else {
            activeDays = new boolean[7];
            deleteButton.setVisibility(View.GONE);
            setInitialAlarmTime();
            setInitialRepetition();
        }

        addSetTimeLayoutListener();
        addSetRepeatLayoutListener();
        addSetStartLocationListener();
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

    private void addDeleteButtonListener() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent replyIntent = new Intent();

                // Add user-selected extras.
                replyIntent.putExtra(EXTRA_ID, alarmID);
                replyIntent.putExtra(EXTRA_DELETE, true);

                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
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

                // Get initial hour and minute values to display in dialog;
                int hour, minute;
                if (time == null) {
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);
                } else {
                    String[] splitTime = time.split(":");
                    hour = Integer.parseInt(splitTime[0]);
                    minute = Integer.parseInt(splitTime[1].substring(0, 2));
                    if (splitTime[1].endsWith("PM")) {
                        hour += 12;
                    } else if (splitTime[1].endsWith("AM")) {
                        if (hour == 12) hour = 0;
                    }
                }

                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(AddAlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String formattedTime = getFormattedTime(selectedHour, selectedMinute);
                        time = formattedTime;
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

    /**
     * When startLocation Layout is selected, open maps activity
     */
    private void addSetStartLocationListener() {
        RelativeLayout setStartLocButton = findViewById(R.id.add_alarm_start_loc_layout);

        setStartLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddAlarmActivity.this, MapsActivity.class);
                startActivityForResult(intent, SET_START_LOCATION_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    /**
     * Get Bundle of arguments for SetRepeatDaysDialogFragment, arguments include alarm's active days.
     *
     * @return Bundle of arguments
     */
    @NonNull
    private Bundle getBundle() {
        Bundle args = new Bundle();

        args.putBooleanArray("activeDays", activeDays);
        return args;
    }

    /**
     * This method is called when SetRepeatDaysDialogFragment completes, we get the selectedDays
     * and apply that to alarm
     *
     * @param selectedDaysBools boolean array of selected days of the week
     */
    public void onDialogComplete(boolean[] selectedDaysBools) {
        activeDays = selectedDaysBools;
        String formattedActiveDays = getStringOfActiveDays();
        repeatTextView.setText(formattedActiveDays);
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
            Intent replyIntent = new Intent();

            String time = timeTextView.getText().toString();

            // Add user-selected extras.
            replyIntent.putExtra(EXTRA_ID, alarmID);
            replyIntent.putExtra(EXTRA_TIME, time);
            replyIntent.putExtra(EXTRA_ACTIVE, false);
            replyIntent.putExtra(EXTRA_ACTIVE_DAYS, activeDays);

            setResult(RESULT_OK, replyIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates UI with data received from MapActivity
     *
     * @param requestCode request code varies on whether start or end location set
     * @param resultCode  whether activity successfully completed
     * @param data        reply Intent, contains extras that vary based on request code
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SET_START_LOCATION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get extra
            String timeStr = data.getStringExtra(MapsActivity.EXTRA_PLACE);

            // Set place to start location textView
            startLocTextView = findViewById(R.id.add_alarm_start_loc_text);
            startLocTextView.setText(timeStr);

        }
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

    /**
     * Get a user readable representation of the String Array activeDays
     *
     * @return String representation of activeDays
     */
    public String getStringOfActiveDays() {
        // Build string based on which indices are true in activeDays
        StringBuilder builder = new StringBuilder();
        int activeCount = 0;
        for (int i = 0; i < 7; i++) {
            if (activeDays[i]) {
                String formattedDay = Alarm.daysOfWeek[i].substring(0, 3) + ", ";
                builder.append(formattedDay);
                activeCount++;
            }
        }

        if (activeCount == 7) {
            return "everyday";
        } else if (activeCount == 0) {
            return "never";
        }

        boolean satInArray = activeDays[6]; // "Saturday" in activeDays
        boolean sunInArray = activeDays[0]; // "Sunday" in activeDays

        if (satInArray && sunInArray && activeCount == 2) {
            return "weekends";
        } else if (!satInArray && !sunInArray && activeCount == 5) {
            return "weekdays";
        }

        if (builder.length() > 1) {
            builder.setLength(builder.length() - 2);
        }

        return builder.toString();
    }
}
