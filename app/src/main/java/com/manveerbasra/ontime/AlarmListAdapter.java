package com.manveerbasra.ontime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class AlarmListAdapter extends ArrayAdapter<Alarm> {

    public AlarmListAdapter(@NonNull Context context, @NonNull Alarm[] alarms) {
        super(context, 0, alarms);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Alarm alarm = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_alarm, parent, false);
        }

        TextView timeTextView = convertView.findViewById(R.id.alarm_time_text);
        Switch activeSwitch = convertView.findViewById(R.id.alarm_active_switch);

        timeTextView.setText(alarm.getStringTime());
        if (alarm.isActive()) {
            activeSwitch.setChecked(true);
        } else {
            activeSwitch.setChecked(false);
        }

        return convertView;
    }
}
