package com.manveerbasra.ontime;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.manveerbasra.ontime.db.AlarmEntity;

import java.util.Collections;
import java.util.List;

/**
 * ArrayAdapter used to populate MainActivity Alarms ListView
 */
public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder> {

    class AlarmViewHolder extends RecyclerView.ViewHolder {
        private final TextView timeTextView;
        private final TextView repetitionTextView;
        private final Switch activeSwitch;

        private AlarmViewHolder(View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.alarm_time_text);
            repetitionTextView = itemView.findViewById(R.id.alarm_repetition_text);
            activeSwitch = itemView.findViewById(R.id.alarm_active_switch);
        }
    }

    private final LayoutInflater mInflater;
    private List<AlarmEntity> alarms = Collections.emptyList(); // Cached copy of alarms

    AlarmListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder viewHolder, int position) {
        Resources resources = viewHolder.itemView.getContext().getResources();
        AlarmEntity alarm = alarms.get(position);

        viewHolder.timeTextView.setText(alarm.getStringTime());

        // Set repeatTextView
        if (alarm.isRepeat()) {
            String repetitionText = alarm.getStringOfActiveDays();
            viewHolder.repetitionTextView.setText(repetitionText);
        } else {
            viewHolder.repetitionTextView.setText(resources.getString(R.string.no_repeat));
        }

        // Set TextView colors based on alarm's active state
        if (alarm.isActive()) {
            viewHolder.activeSwitch.setChecked(true);
            viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorAccent));
            viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorWhite));
        } else {
            viewHolder.activeSwitch.setChecked(false);
            viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorGrey500));
            viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorGrey500));
        }
    }

    void setAlarms(List<AlarmEntity> alarms) {
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

}

//
// -- OLD IMPLEMENTATION ---------------------------------------------------------------------------
//
//    /**
//     * Used to access AlarmDatabase
//     */
//    private AlarmRepository dbHelper;
//
//    public AlarmListAdapter(@NonNull Context context, @NonNull AlarmDataManager[] alarms) {
//        super(context, 0, alarms);
//        dbHelper = new AlarmRepository(getContext());
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//
//        AlarmDataManager alarm = getItem(position);
//
//        // Check if an existing view is being reused, otherwise inflate the view
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_alarm, parent, false);
//        }
//
//        if (alarm == null) { // Ensures dereference isn't null
//            return convertView;
//        }
//
//        // Get views to populate data
//        TextView timeTextView = convertView.findViewById(R.id.alarm_time_text);
//        TextView repetitionTextView = convertView.findViewById(R.id.alarm_repetition_text);
//        Switch activeSwitch = convertView.findViewById(R.id.alarm_active_switch);
//
//        addSwitchListener(alarm, timeTextView, repetitionTextView, activeSwitch);
//        populateViews(alarm, timeTextView, repetitionTextView, activeSwitch);
//
//
//        return convertView;
//    }
//
//    /**
//     * Populate Views with alarm data
//     * @param alarm AlarmDataManager object
//     * @param timeTextView TextView that displays alarm time
//     * @param repetitionTextView TextView that displays alarm repetition
//     * @param activeSwitch Switch for alarm's active/nonactive state
//     */
//    private void populateViews(AlarmDataManager alarm, TextView timeTextView, TextView repetitionTextView, Switch activeSwitch) {
//        // Set timeTextView
//        timeTextView.setText(alarm.getStringTime());
//
//        // Set repeatTextView
//        if (alarm.isRepeat()) {
//            String repetitionText = alarm.getStringOfActiveDays();
//            repetitionTextView.setText(repetitionText);
//        } else {
//            repetitionTextView.setText(getContext().getString(R.string.no_repeat));
//        }
//
//        // Set TextView colors based on alarm's active state
//        if (alarm.isActive()) {
//            activeSwitch.setChecked(true);
//            timeTextView.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
//            repetitionTextView.setTextColor(getContext().getResources().getColor(R.color.colorWhite));
//        } else {
//            activeSwitch.setChecked(false);
//            timeTextView.setTextColor(getContext().getResources().getColor(R.color.colorGrey500));
//            repetitionTextView.setTextColor(getContext().getResources().getColor(R.color.colorGrey500));
//        }
//    }
//
//    /**
//     * When activeSwitch's checked state changes, update colors and database.
//     * @param alarm AlarmDataManager object
//     * @param timeTextView TextView that displays alarm time
//     * @param repetitionTextView TextView that displays alarm repetition
//     * @param activeSwitch Switch for alarm's active/nonactive state
//     */
//    private void addSwitchListener(final AlarmDataManager alarm, final TextView timeTextView, final TextView repetitionTextView, Switch activeSwitch) {
//        activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
//                if (checked) {
//                    alarm.setActive(true);
//                    timeTextView.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
//                    repetitionTextView.setTextColor(getContext().getResources().getColor(R.color.colorWhite));
//                } else {
//                    alarm.setActive(false);
//                    timeTextView.setTextColor(getContext().getResources().getColor(R.color.colorGrey500));
//                    repetitionTextView.setTextColor(getContext().getResources().getColor(R.color.colorGrey500));
//                }
//                dbHelper.updateAlarm(alarm);
//                AlarmDataManager[] alarms = dbHelper.getAllAlarms();
//            }
//        });
//    }
