package com.example.bwoestman.weatheralarm;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian Woestman on 2/26/16.
 * Android Programming
 * We 5:30p - 9:20p
 */
public class AlarmListFragment extends Fragment implements AppInfo
{
    private SingletonAlarm singletonAlarm = SingletonAlarm.getInstance();

    private RecyclerView mAlarmRecyclerView;
    private AlarmAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        mAlarmRecyclerView = (RecyclerView) view.findViewById(R.id.alarm_recycler_view);
        mAlarmRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private class AlarmHolder extends RecyclerView.ViewHolder
    {
        private TextView mTimeTv;
        private Switch mEnableSw;

        private Alarm mAlarm;

        public AlarmHolder(View itemView)
        {
            super(itemView);

            mTimeTv = (TextView) itemView.findViewById(R.id.tv_time);
            mEnableSw = (Switch) itemView.findViewById(R.id.sw_enable);
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        public void bindAlarm(Alarm alarm)
        {
            String time = alarm.getHour() + ":" + alarm.getMinute();

            mAlarm = alarm;
            mTimeTv.setText(time);
            if (alarm.getEnabled() == 1)
            {
                mEnableSw.setChecked(true);
            }
            else
            {
                mEnableSw.setChecked(false);
            }
        }
    }

    private class AlarmAdapter extends RecyclerView.Adapter<AlarmHolder>
    {
        private List<Alarm> mAlarms;

        public AlarmAdapter(List<Alarm> alarms)
        {
            mAlarms = alarms;
        }

        @Override
        public AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_alarm, parent, false);

            return new AlarmHolder(view);
        }

        @Override
        public void onBindViewHolder(AlarmHolder holder, int position)
        {
            String time;
            Alarm alarm = mAlarms.get(position);

            time = alarm.getHour() + ":" + alarm.getMinute();

            holder.mTimeTv.setText(time);
        }

        @Override
        public int getItemCount()
        {
            return mAlarms.size();
        }
    }

    private void updateUI()
    {
        mAlarmRecyclerView.setAdapter(mAdapter);
    }

    private void echoAlarms()
    {
        DBHandler db = new DBHandler(getContext(), null, null, 1);
        List<Alarm> as = db.getAlarms();

        for (Alarm a : as)
        {
            Log.d(TAG, "echoAlarms: " + a.toString());
        }
    }
}