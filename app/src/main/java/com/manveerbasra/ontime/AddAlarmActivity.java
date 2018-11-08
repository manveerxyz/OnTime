package com.manveerbasra.ontime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddAlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        setInitialAlarmTime();



    }

    private void setInitialAlarmTime() {
        // Get time and set it to alarm time TextView
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("hh:mm aa");
        String date_str = df.format(cal.getTime());
        TextView timeTextView = findViewById(R.id.add_alarm_time_text);
        timeTextView.setText(date_str);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_alarm_save) {
            // TODO: Save Alarm
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
