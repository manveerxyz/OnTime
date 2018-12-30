package com.manveerbasra.ontime.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.manveerbasra.ontime.R;
import com.manveerbasra.ontime.db.Alarm;
import com.manveerbasra.ontime.ui.AlarmListAdapter;
import com.manveerbasra.ontime.viewmodel.AlarmViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    public static final int NEW_ALARM_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_ALARM_ACTIVITY_REQUEST_CODE = 2;

    /**
     * Used to access AlarmDatabase
     */
    private AlarmViewModel alarmViewModel;
    private View snackbarAnchor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        snackbarAnchor = findViewById(R.id.fab);

        // Setup adapter to display alarms.
        RecyclerView recyclerView = findViewById(R.id.alarm_list);
        final AlarmListAdapter adapter = new AlarmListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get a new or existing ViewModel from the ViewModelProvider.
        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);

        // Add an observer on the LiveData returned by getAllAlarms.
        alarmViewModel.getAllAlarms().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(@Nullable final List<Alarm> alarms) {
                // Update the cached copy of the words in the adapter.
                adapter.setAlarms(alarms);
            }
        });

        setFABListener();
    }

    /**
     * Setup FloatingActionButton listener
     */
    private void setFABListener() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
                startActivityForResult(intent, NEW_ALARM_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    /**
     * Updates AlarmViewModel with data received from AddAlarmActivity.
     * <p>
     * Handles both new Alarms and edited Alarms.
     *
     * @param requestCode request code varies on whether alarm is added or edited
     * @param resultCode  whether activity successfully completed
     * @param data        reply Intent, contains extras
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_ALARM_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get Extras.
            String timeStr = data.getStringExtra(AddAlarmActivity.EXTRA_TIME);
            boolean active = data.getBooleanExtra(AddAlarmActivity.EXTRA_ACTIVE, false);
            boolean[] activeDays = data.getBooleanArrayExtra(AddAlarmActivity.EXTRA_ACTIVE_DAYS);

            Bundle args = data.getBundleExtra(AddAlarmActivity.BUNDLE_POINTS);
            LatLng startPoint = args.getParcelable(AddAlarmActivity.EXTRA_START_POINT);
            LatLng endPoint = args.getParcelable(AddAlarmActivity.EXTRA_END_POINT);

            // Convert String timeStr to Date object.
            DateFormat formatter = new SimpleDateFormat("hh:mm aa");
            Date time = null;
            try {
                time = formatter.parse(timeStr);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            // Create new Alarm object.
            Alarm alarm = new Alarm(time, active, activeDays, startPoint, endPoint);
            // Insert into ViewModel.
            alarmViewModel.insert(alarm);

            // Show snackbar to notify user
            Snackbar.make(snackbarAnchor, R.string.alarm_saved, Snackbar.LENGTH_SHORT).show();

        } else if (requestCode == EDIT_ALARM_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get Extras.
            int id = data.getIntExtra(AddAlarmActivity.EXTRA_ID, -1);
            if (data.hasExtra(AddAlarmActivity.EXTRA_DELETE)) { // Alarm to be deleted.
                // Get Alarm object to be deleted.
                Alarm alarm = alarmViewModel.getById(id);
                // Delete alarm.
                alarmViewModel.delete(alarm);

                // Show snackbar to notify user
                Snackbar.make(snackbarAnchor, R.string.alarm_deleted, Snackbar.LENGTH_SHORT).show();
            } else {
                String timeStr = data.getStringExtra(AddAlarmActivity.EXTRA_TIME);
                boolean active = data.getBooleanExtra(AddAlarmActivity.EXTRA_ACTIVE, false);
                boolean[] activeDays = data.getBooleanArrayExtra(AddAlarmActivity.EXTRA_ACTIVE_DAYS);

                Bundle args = data.getBundleExtra(AddAlarmActivity.BUNDLE_POINTS);
                LatLng startPoint = args.getParcelable(AddAlarmActivity.EXTRA_START_POINT);
                LatLng endPoint = args.getParcelable(AddAlarmActivity.EXTRA_END_POINT);

                // Convert String timeStr to Date object.
                DateFormat formatter = new SimpleDateFormat("hh:mm aa");
                Date time = null;
                try {
                    time = formatter.parse(timeStr);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

                // Get Alarm object that was edited.
                Alarm alarm = alarmViewModel.getById(id);
                // Update Alarm object.
                alarm.setTime(time);
                alarm.setActive(active);
                alarm.setActiveDays(activeDays);
                alarm.setStartPoint(startPoint);
                alarm.setEndPoint(endPoint);
                // Update in ViewModel.
                alarmViewModel.update(alarm);

                // Show snackbar to notify user
                Snackbar.make(snackbarAnchor, R.string.alarm_saved, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            // Show snackbar to notify user
            Snackbar.make(snackbarAnchor, R.string.alarm_not_saved, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
