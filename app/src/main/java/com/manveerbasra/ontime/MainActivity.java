package com.manveerbasra.ontime;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.manveerbasra.ontime.db.Alarm;
import com.manveerbasra.ontime.db.AlarmDatabase;
import com.manveerbasra.ontime.db.utils.DatabaseInitializer;

import java.sql.SQLOutput;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AlarmDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

//        // only for testing
//        AlarmDataManager alarm1 = new AlarmDataManager(8, 48, "AM");
//        AlarmDataManager alarm2 = new AlarmDataManager(10, 45, "PM", false,
//                new String[] {"Saturday", "Sunday"});
//
//        AlarmDataManager[] alarms = new AlarmDataManager[] {
//                alarm1,
//                alarm2
//        };

        // Note: Db references should not be in an activity.
        db = AlarmDatabase.getInMemoryDatabase(getApplicationContext());
        DatabaseInitializer.populateSync(db);

        List<Alarm> alarmsList = db.alarmModel().loadAllAlarms();
        System.out.println(alarmsList);
        Alarm[] alarms = new Alarm[alarmsList.size()];
        alarms = alarmsList.toArray(alarms);

        ListView alarmListView = findViewById(R.id.alarm_list);
        alarmListView.setAdapter(new AlarmListAdapter(MainActivity.this, alarms));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // TODO: Link to SettingsActivity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
