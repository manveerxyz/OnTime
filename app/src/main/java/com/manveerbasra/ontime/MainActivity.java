package com.manveerbasra.ontime;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.manveerbasra.ontime.db.Alarm;
import com.manveerbasra.ontime.db.AlarmDatabase;
import com.manveerbasra.ontime.db.AlarmDbHelper;
import com.manveerbasra.ontime.db.utils.DatabaseInitializer;

import java.sql.SQLOutput;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * Used to access AlarmDatabase
     */
    private AlarmDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        setFABListener();

        dbHelper = new AlarmDbHelper(getApplicationContext());

        displaySavedAlarms();
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
                startActivity(intent);
            }
        });
    }

    /**
     * Initialize dbHelper, get saved alarms, and initialize ListView of alarms
     */
    private void displaySavedAlarms() {
        // Get saved alarms
        AlarmDataManager[] alarms = dbHelper.getAllAlarms();
        // Populate ListView with alarms
        ListView alarmListView = findViewById(R.id.alarm_list);
        alarmListView.setAdapter(new AlarmListAdapter(MainActivity.this, alarms));

        TextView emptyListText = findViewById(R.id.no_alarms_text);
        alarmListView.setEmptyView(emptyListText);
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
