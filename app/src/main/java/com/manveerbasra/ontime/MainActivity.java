package com.manveerbasra.ontime;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.manveerbasra.ontime.db.AlarmEntity;
import com.manveerbasra.ontime.viewmodel.AlarmViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int NEW_ALARM_ACTIVITY_REQUEST_CODE = 1;

    /**
     * Used to access AlarmDatabase
     */
    private AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // Setup adapter to display alarms.
        RecyclerView recyclerView = findViewById(R.id.alarm_list);
        final AlarmListAdapter adapter = new AlarmListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get a new or existing ViewModel from the ViewModelProvider.
        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);

        // Add an observer on the LiveData returned by getAllAlarms.
        alarmViewModel.getAllAlarms().observe(this, new Observer<List<AlarmEntity>>() {
            @Override
            public void onChanged(@Nullable final List<AlarmEntity> alarms) {
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_ALARM_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String timeStr = data.getStringExtra(AddAlarmActivity.EXTRA_TIME);
            boolean active = data.getBooleanExtra(AddAlarmActivity.EXTRA_ACTIVE, false);
            String[] activeDays = data.getStringArrayExtra(AddAlarmActivity.EXTRA_ACTIVE_DAYS);

            // Convert String timeStr to Date object.
            DateFormat formatter = new SimpleDateFormat("hh:mm aa");
            Date time = null;
            try {
                time = formatter.parse(timeStr);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            AlarmEntity alarm = new AlarmEntity(time, active, activeDays);
            alarmViewModel.insert(alarm);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.alarm_not_saved,
                    Toast.LENGTH_LONG).show();
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
            // TODO: Link to SettingsActivity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
