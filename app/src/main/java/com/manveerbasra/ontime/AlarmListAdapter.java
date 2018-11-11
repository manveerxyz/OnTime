package com.manveerbasra.ontime;

import android.arch.lifecycle.ViewModelProviders;
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
import com.manveerbasra.ontime.viewmodel.AlarmViewModel;

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
    private AlarmViewModel alarmViewModel;

    AlarmListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        alarmViewModel = ViewModelProviders.of((MainActivity) context).get(AlarmViewModel.class);
        setHasStableIds(true);
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

        addSwitchListener(alarm, viewHolder, resources);
    }

    void setAlarms(List<AlarmEntity> alarms) {
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    @Override
    public long getItemId(int position) {
        return alarms.get(position).getId();
    }

    private void addSwitchListener(final AlarmEntity alarm, final AlarmViewHolder viewHolder, final Resources resources) {
        viewHolder.activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    alarm.setActive(true);
                    viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorAccent));
                    viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorWhite));
                } else {
                    alarm.setActive(false);
                    viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorGrey500));
                    viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorGrey500));
                }
                alarmViewModel.update(alarm);
            }
        });
    }

}
