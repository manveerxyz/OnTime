package com.manveerbasra.ontime.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.manveerbasra.ontime.R;
import com.manveerbasra.ontime.alarmmanager.AlarmHandler;
import com.manveerbasra.ontime.db.Alarm;
import com.manveerbasra.ontime.ui.activity.AddAlarmActivity;
import com.manveerbasra.ontime.ui.activity.MainActivity;
import com.manveerbasra.ontime.viewmodel.AlarmViewModel;

import java.util.Collections;
import java.util.List;

/**
 * ArrayAdapter used to populate MainActivity Alarms ListView
 */
public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder> {

    private final String TAG = "AlarmListAdapter";

    /**
     * View for each alarm
     */
    class AlarmViewHolder extends RecyclerView.ViewHolder {
        private final TextView timeTextView;
        private final TextView repetitionTextView;
        private final Switch activeSwitch;
        private final ImageButton editButton;

        private AlarmViewHolder(View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.alarm_time_text);
            repetitionTextView = itemView.findViewById(R.id.alarm_repetition_text);
            activeSwitch = itemView.findViewById(R.id.alarm_active_switch);
            editButton = itemView.findViewById(R.id.alarm_edit_button);
        }
    }

    // Layout members
    private final LayoutInflater mInflater;
    private final TextView emptyTextView;
    // Data list (cached copy of alarms)
    private List<Alarm> alarms = Collections.emptyList();
    // To handle interactions with database
    private AlarmViewModel alarmViewModel;
    // To schedule alarms
    private AlarmHandler alarmHandler;

    public AlarmListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        emptyTextView = ((MainActivity) context).findViewById(R.id.no_alarms_text);

        alarmViewModel = ViewModelProviders.of((MainActivity) context).get(AlarmViewModel.class);
        alarmHandler = new AlarmHandler(context, ((MainActivity) context).findViewById(R.id.fab));

        setHasStableIds(true); // so Switch interaction has smooth animations
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
        Alarm alarm = alarms.get(position);

        viewHolder.timeTextView.setText(alarm.getStringTime()); // set alarm time

        // Set repeatTextView text
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
            viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorDarkText));
        } else {
            viewHolder.activeSwitch.setChecked(false);
            viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorGrey500));
            viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorGrey500));
        }

        // Add Button click listeners
        addSwitchListener(alarm, viewHolder, resources);
        addEditButtonListener(alarm, viewHolder);
    }

    /**
     * Update current list of alarms and update UI
     *
     * @param alarms List of updated alarms
     */
    public void setAlarms(List<Alarm> alarms) {
        Log.i(TAG, "updating alarms data-set");
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        emptyTextView.setVisibility(alarms.size() > 0 ? View.GONE : View.VISIBLE);
        return alarms.size();
    }

    @Override
    public long getItemId(int position) {
        return alarms.get(position).getId();
    }

    /**
     * Add OnCheckedChange Listener to alarm's "active" switch
     *
     * @param alarm      Alarm object
     * @param viewHolder Alarm's ViewHolder object, containing Switch
     * @param resources  Resources file to get color values from
     */
    private void addSwitchListener(final Alarm alarm, final AlarmViewHolder viewHolder, final Resources resources) {
        viewHolder.activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    alarm.setActive(true);
                    viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorAccent));
                    viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorDarkText));
                    // schedule alarm
                    Log.i(TAG, "scheduling alarm: " + alarm.getId());
                    alarmHandler.scheduleAlarm(alarm);
                } else {
                    alarm.setActive(false);
                    viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorGrey500));
                    viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorGrey500));
                    alarmHandler.cancelAlarm(alarm);
                }

                // Update database and schedule alarm
                Log.i(TAG, "updating database with alarm: " + alarm.getId());
                alarmViewModel.updateActive(alarm);
            }
        });
    }

    /**
     * Add OnClickListener to alarm's edit button to open EditAlarmActivity (AddAlarmActivity.java)
     *
     * @param alarm      Alarm object
     * @param viewHolder Alarm's ViewHolder object, containing edit button
     */
    private void addEditButtonListener(final Alarm alarm, final AlarmViewHolder viewHolder) {
        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, AddAlarmActivity.class);

                intent.putExtra(AddAlarmActivity.EXTRA_ID, alarm.getId());
                intent.putExtra(AddAlarmActivity.EXTRA_TIME, alarm.getStringTime());
                intent.putExtra(AddAlarmActivity.EXTRA_ACTIVE_DAYS, alarm.getActiveDays());

                ((MainActivity) context).startActivityForResult(intent, MainActivity.EDIT_ALARM_ACTIVITY_REQUEST_CODE);
            }
        });
    }

}
