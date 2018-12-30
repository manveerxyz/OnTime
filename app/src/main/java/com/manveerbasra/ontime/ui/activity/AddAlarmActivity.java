package com.manveerbasra.ontime.ui.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;
import com.manveerbasra.ontime.R;
import com.manveerbasra.ontime.db.Alarm;
import com.manveerbasra.ontime.ui.SetRepeatDaysDialogFragment;

import java.util.Calendar;

public class AddAlarmActivity extends AppCompatActivity implements SetRepeatDaysDialogFragment.OnDialogCompleteListener {

    private final String TAG = "AddAlarmActivity";

    private static final int SET_START_LOCATION_ACTIVITY_REQUEST_CODE = 1;
    private static final int SET_END_LOCATION_ACTIVITY_REQUEST_CODE = 2;

    // Key values for returning intent.
    public static final String EXTRA_ID = "com.manveerbasra.ontime.AddAlarmActivity.ID";
    public static final String EXTRA_TIME = "com.manveerbasra.ontime.AddAlarmActivity.TIME";
    public static final String EXTRA_ACTIVE = "com.manveerbasra.ontime.AddAlarmActivity.ACTIVE";
    public static final String EXTRA_ACTIVE_DAYS = "com.manveerbasra.ontime.AddAlarmActivity.ACTIVEDAYS";
    public static final String EXTRA_DELETE = "com.manveerbasra.ontime.AddAlarmActivity.DELETE";
    public static final String EXTRA_START_PLACE = "com.manveerbasra.ontime.AddAlarmActivity.STARTPLACE";
    public static final String EXTRA_END_PLACE = "com.manveerbasra.ontime.AddAlarmActivity.ENDPLACE";
    public static final String BUNDLE_POINTS = "com.manveerbasra.ontime.AddAlarmActivity.BUNDLE.POINTS";
    public static final String EXTRA_START_POINT = "com.manveerbasra.ontime.AddAlarmActivity.STARTPOINT";
    public static final String EXTRA_END_POINT = "com.manveerbasra.ontime.AddAlarmActivity.ENDPOINT";

    // Alarm attributes.
    int alarmID;
    String time;
    boolean[] activeDays;
    LatLng startPoint;
    LatLng endPoint;
    String startPlace;
    String endPlace;
    // Data objects
    Calendar calendar;
    // View objects
    TextView timeTextView;
    TextView repeatTextView;
    TextView startLocTextView;
    TextView endLocTextView;
    FloatingActionButton deleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        calendar = Calendar.getInstance();
        deleteButton = findViewById(R.id.fab_add_alarm_delete);

        timeTextView = findViewById(R.id.add_alarm_time_text);
        repeatTextView = findViewById(R.id.add_alarm_repeat_text);
        startLocTextView = findViewById(R.id.add_alarm_start_loc_text);
        endLocTextView = findViewById(R.id.add_alarm_end_loc_text);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) { // Activity called to edit an alarm.
            alarmID = intent.getIntExtra(EXTRA_ID, -1);
            time = intent.getStringExtra(EXTRA_TIME);
            activeDays = intent.getBooleanArrayExtra(EXTRA_ACTIVE_DAYS);
            startPlace = intent.getStringExtra(EXTRA_START_PLACE);
            endPlace = intent.getStringExtra(EXTRA_END_PLACE);

            Bundle args = intent.getBundleExtra(BUNDLE_POINTS);
            startPoint = args.getParcelable(EXTRA_START_POINT);
            endPoint = args.getParcelable(EXTRA_END_POINT);

            startLocTextView.setText(startPlace);
            endLocTextView.setText(endPlace);
            timeTextView.setText(time);
            repeatTextView.setText(Alarm.getStringOfActiveDays(activeDays));
            setTitle(R.string.edit_alarm);

            addDeleteButtonListener();
        } else {
            activeDays = new boolean[7];
            deleteButton.hide();
            setInitialAlarmTime();
            setInitialRepetition();
        }

        addSetTimeLayoutListener();
        addSetRepeatLayoutListener();
        addSetStartLocationListener();
        addEndStartLocationListener();
    }


    /**
     * Initialize timeTextView with current time
     */
    private void setInitialAlarmTime() {
        // Get time and set it to alarm time TextView
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String currentTime = getFormattedTime(hour, minute);
        timeTextView.setText(currentTime);

    }

    /**
     * Initialize timeTextView with current time
     */
    private void setInitialRepetition() {
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
     * When endLocation Layout is selected, open maps activity
     */
    private void addEndStartLocationListener() {
        RelativeLayout setEndLocButton = findViewById(R.id.add_alarm_end_loc_layout);

        setEndLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddAlarmActivity.this, MapsActivity.class);
                startActivityForResult(intent, SET_END_LOCATION_ACTIVITY_REQUEST_CODE);
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
        String formattedActiveDays = Alarm.getStringOfActiveDays(activeDays);
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
            if (startLocTextView.getText().equals(getString(R.string.not_set))
                    || endLocTextView.getText().equals(getString(R.string.not_set))) {
                Snackbar.make(findViewById(R.id.fab_add_alarm_delete), getString(R.string.locs_not_selected), Snackbar.LENGTH_SHORT).show();
            } else {
                Intent replyIntent = new Intent();

                String time = timeTextView.getText().toString();

                Bundle args = new Bundle();
                args.putParcelable(EXTRA_START_POINT, startPoint);
                args.putParcelable(EXTRA_END_POINT, endPoint);

                // Add user-selected extras.
                replyIntent.putExtra(EXTRA_ID, alarmID);
                replyIntent.putExtra(EXTRA_TIME, time);
                replyIntent.putExtra(EXTRA_ACTIVE, false);
                replyIntent.putExtra(EXTRA_ACTIVE_DAYS, activeDays);
                replyIntent.putExtra(EXTRA_START_PLACE, startPlace);
                replyIntent.putExtra(EXTRA_END_PLACE, endPlace);
                replyIntent.putExtra(BUNDLE_POINTS, args);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
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
            // Get extras
            startPlace = data.getStringExtra(MapsActivity.EXTRA_PLACE);
            Bundle args = data.getBundleExtra(MapsActivity.BUNDLE_POINT);
            startPoint = args.getParcelable(MapsActivity.EXTRA_LATLNG);

            // Set place to start location textView
            startLocTextView.setText(startPlace);

        } else if (requestCode == SET_END_LOCATION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get extra
            endPlace = data.getStringExtra(MapsActivity.EXTRA_PLACE);
            Bundle args = data.getBundleExtra(MapsActivity.BUNDLE_POINT);
            endPoint = args.getParcelable(MapsActivity.EXTRA_LATLNG);
            // Set place to start location textView
            endLocTextView.setText(endPlace);
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
}
