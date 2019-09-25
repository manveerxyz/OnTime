package com.manveerbasra.ontime.ui.activity;

import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.manveerbasra.ontime.R;
import com.manveerbasra.ontime.db.Alarm;
import com.manveerbasra.ontime.ui.SetRepeatDaysDialogFragment;
import com.manveerbasra.ontime.viewmodel.AlarmViewModel;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Used to create and edit alarms, depending on REQUEST_CODE
 */
public class AddAlarmActivity extends AppCompatActivity implements SetRepeatDaysDialogFragment.OnDialogCompleteListener {

    private final String TAG = "AddAlarmActivity";

    private static final int SET_START_LOCATION_ACTIVITY_REQUEST_CODE = 1;
    private static final int SET_END_LOCATION_ACTIVITY_REQUEST_CODE = 2;

    // Key values for returning intent.
    public static final String EXTRA_BUNDLE = "com.manveerbasra.ontime.AddAlarmActivity.BUNDLE";
    public static final String EXTRA_ALARM = "com.manveerbasra.ontime.AddAlarmActivity.ALARM";
    public static final String EXTRA_DELETE = "com.manveerbasra.ontime.AddAlarmActivity.DELETE";

    private AlarmViewModel alarmViewModel;
    private int mCurrRequestCode; // current request code - static values in MainActivity

    private Alarm mAlarm;
    // Data objects
    private Calendar calendar;
    // View objects
    private TextView mTimeTextView;
    private TextView mRepeatTextView;
    private TextView mStartLocTextView;
    private TextView mEndLocTextView;
    private FloatingActionButton mDeleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        calendar = Calendar.getInstance();
        mDeleteButton = findViewById(R.id.fab_add_alarm_delete);

        mTimeTextView = findViewById(R.id.add_alarm_time_text);
        mRepeatTextView = findViewById(R.id.add_alarm_repeat_text);
        mStartLocTextView = findViewById(R.id.add_alarm_start_loc_text);
        mEndLocTextView = findViewById(R.id.add_alarm_end_loc_text);

        // Get a new or existing ViewModel from the ViewModelProvider.
        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_BUNDLE)) { // Activity called to edit an alarm.
            Bundle args = intent.getBundleExtra(EXTRA_BUNDLE);
            mAlarm = args.getParcelable(EXTRA_ALARM);
            mCurrRequestCode = MainActivity.EDIT_ALARM_ACTIVITY_REQUEST_CODE;
        } else {
            mCurrRequestCode = MainActivity.NEW_ALARM_ACTIVITY_REQUEST_CODE;
        }

        if (mAlarm != null) {
            mStartLocTextView.setText(mAlarm.startPlace);
            mEndLocTextView.setText(mAlarm.endPlace);
            mTimeTextView.setText(mAlarm.getStringTime());
            mRepeatTextView.setText(mAlarm.getStringOfActiveDays());
            setTitle(R.string.edit_alarm);
            addModeButtonListeners(mAlarm.transMode);

            addDeleteButtonListener();
        } else {
            mAlarm = new Alarm();
            mAlarm.activeDays = new boolean[7];
            mAlarm.transMode = "driving";
            mDeleteButton.hide();
            setInitialAlarmTime();
            addModeButtonListeners("driving");
            mRepeatTextView.setText(R.string.never);
        }

        addSetTimeLayoutListener();
        addSetRepeatLayoutListener();
        addSetStartLocationListener();
        addEndStartLocationListener();
    }


    /**
     * Initialize TimeTextView and Alarm's time with current time
     */
    private void setInitialAlarmTime() {
        // Get time and set it to alarm time TextView
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String currentTime = getFormattedTime(hour, minute);
        mTimeTextView.setText(currentTime);
        mAlarm.setTime(currentTime);
    }

    private void addDeleteButtonListener() {
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent replyIntent = new Intent();

                alarmViewModel.delete(mAlarm);
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
                if (mAlarm.time == null) {
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);
                } else {
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.setTime(mAlarm.time);
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);
                }

                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(AddAlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String formattedTime = getFormattedTime(selectedHour, selectedMinute);
                        mAlarm.setTime(formattedTime);
                        mTimeTextView.setText(formattedTime);
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

                Bundle args = new Bundle();
                args.putParcelable(MapsActivity.EXTRA_LATLNG, mAlarm.startPoint);
                intent.putExtra(MapsActivity.BUNDLE_POINT, args);
                intent.putExtra(MapsActivity.EXTRA_PLACE, mAlarm.startPlace);

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

                Bundle args = new Bundle();
                args.putParcelable(MapsActivity.EXTRA_LATLNG, mAlarm.endPoint);
                intent.putExtra(MapsActivity.BUNDLE_POINT, args);
                intent.putExtra(MapsActivity.EXTRA_PLACE, mAlarm.endPlace);

                startActivityForResult(intent, SET_END_LOCATION_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    private void addModeButtonListeners(String currMode) {
        final ImageButton walkButton = findViewById(R.id.mode_walk_button);
        final ImageButton bikeButton = findViewById(R.id.mode_bike_button);
        final ImageButton transitButton = findViewById(R.id.mode_transit_button);
        final ImageButton driveButton = findViewById(R.id.mode_drive_button);

        if (currMode == null) {
            currMode = "driving";
        }

        switch (currMode) { // Set initial backgrounds based on currMode parameter
            case "driving":
                updateBackgrounds(driveButton, walkButton, bikeButton, transitButton);
                break;
            case "transit":
                updateBackgrounds(transitButton, walkButton, bikeButton, driveButton);
                break;
            case "bicycling":
                updateBackgrounds(bikeButton, walkButton, transitButton, driveButton);
                break;
            case "walking":
                updateBackgrounds(walkButton, bikeButton, transitButton, driveButton);
                break;
        }

        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBackgrounds(walkButton, bikeButton, transitButton, driveButton);
                mAlarm.transMode = "walking";
            }
        });

        bikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBackgrounds(bikeButton, walkButton, transitButton, driveButton);
                mAlarm.transMode = "bicycling";
            }
        });

        transitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBackgrounds(transitButton, walkButton, bikeButton, driveButton);
                mAlarm.transMode = "transit";
            }
        });

        driveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBackgrounds(driveButton, walkButton, bikeButton, transitButton);
                mAlarm.transMode = "driving";
            }
        });
    }

    /**
     * Update backgrounds of different ImageButtons based on whether they should be active or not.
     * First parameter button is set to active
     */
    private void updateBackgrounds(ImageButton active, ImageButton inactive1, ImageButton inactive2, ImageButton inactive3) {
        active.setBackground(getDrawable(R.drawable.solid_circle_blue));
        inactive1.setBackground(getDrawable(R.drawable.solid_circle_grey));
        inactive2.setBackground(getDrawable(R.drawable.solid_circle_grey));
        inactive3.setBackground(getDrawable(R.drawable.solid_circle_grey));
    }

    /**
     * Get Bundle of arguments for SetRepeatDaysDialogFragment, arguments include alarm's active days.
     */
    @NonNull
    private Bundle getBundle() {
        Bundle args = new Bundle();

        args.putBooleanArray("activeDays", mAlarm.activeDays);
        return args;
    }

    /**
     * This method is called when SetRepeatDaysDialogFragment completes, we get the selectedDays
     * and apply that to alarm
     *
     * @param selectedDaysBools boolean array of selected days of the week
     */
    public void onDialogComplete(boolean[] selectedDaysBools) {
        mAlarm.activeDays = selectedDaysBools;
        String formattedActiveDays = Alarm.getStringOfActiveDays(mAlarm.activeDays);
        mRepeatTextView.setText(formattedActiveDays);
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
            if (mStartLocTextView.getText().equals(getString(R.string.set_start_location))
                    || mEndLocTextView.getText().equals(getString(R.string.set_end_location))) {
                Snackbar.make(findViewById(R.id.fab_add_alarm_delete), getString(R.string.locs_not_selected), Snackbar.LENGTH_SHORT).show();
            } else {
                Intent replyIntent = new Intent();

                if (mCurrRequestCode == MainActivity.EDIT_ALARM_ACTIVITY_REQUEST_CODE) {
                    alarmViewModel.update(mAlarm);
                } else {
                    alarmViewModel.insert(mAlarm);
                }

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

        if (data == null) {
            return;
        }

        if (requestCode == SET_START_LOCATION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get extras
            mAlarm.startPlace = data.getStringExtra(MapsActivity.EXTRA_PLACE);
            Bundle args = data.getBundleExtra(MapsActivity.BUNDLE_POINT);
            mAlarm.startPoint = args.getParcelable(MapsActivity.EXTRA_LATLNG);

            // Set place to start location textView
            mStartLocTextView.setText(mAlarm.startPlace);

        } else if (requestCode == SET_END_LOCATION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get extra
            mAlarm.endPlace = data.getStringExtra(MapsActivity.EXTRA_PLACE);
            Bundle args = data.getBundleExtra(MapsActivity.BUNDLE_POINT);
            mAlarm.endPoint = args.getParcelable(MapsActivity.EXTRA_LATLNG);
            // Set place to start location textView
            mEndLocTextView.setText(mAlarm.endPlace);
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
