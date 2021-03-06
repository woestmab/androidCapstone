package com.bwoestman.weatheralarm;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Brian Woestman on 2/26/16.
 * Android Programming
 * We 5:30p - 9:20p
 */

/**
 * this class is a fragment where the alarms are edited
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AlarmEditFragment extends Fragment implements AppInfo, View.OnClickListener
{
    private SingletonAlarm singletonAlarm = SingletonAlarm.getInstance();
    private Alarm clickedAlarm;
    private TimePicker mTimePicker;
    private SeekBar mSbAdjustment;
    private SeekBar mSbRain;
    private TextView mTvAdjustSbPosition;
    private TextView mTvRainSbPosition;
    private ArrayList<Alarm> alarms;
    private AlarmListFragment alarmListFragment;

    private Integer adjPosition = 0;
    private Integer rainPosition = 0;

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p/>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view;

        if (singletonAlarm.getAlarms() != null)
        {
            alarms = singletonAlarm.getAlarms();
        }

        if (singletonAlarm.getIsSinglePane())
        {
            view = inflater.inflate(R.layout.fragment_alarm_edit, container, false);

            Button mBtnCancel = (Button) view.findViewById(R.id.btnCancel);
            mBtnCancel.setOnClickListener(this);
        }
        else
        {
            view = inflater.inflate(R.layout.tablet_fragment_alarm_edit, container, false);
            alarmListFragment = (AlarmListFragment) getFragmentManager()
                    .findFragmentById(R.id.alarm_list_fragment);
        }

        mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);

        mSbAdjustment = (SeekBar) view.findViewById(R.id.sbAdjustment);
        mSbRain = (SeekBar) view.findViewById(R.id.sbRain);

        mTvAdjustSbPosition = (TextView) view.findViewById(R.id.tvAdjustSbPosition);
        mTvRainSbPosition = (TextView) view.findViewById(R.id.tvRainSbPosition);

        Button mBtnSave = (Button) view.findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(this);

        mTimePicker.setIs24HourView(true);
        mTimePicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        updateUi();

        //seekbar anonymous class for Adjustment setting
        mSbAdjustment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                String adjLabel;

                adjLabel = ": " + Integer.toString(progress) + " " +
                        getResources().getString(R.string.adjustment_label);

                adjPosition = progress;
                mTvAdjustSbPosition.setText(adjLabel);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });

        //seekbar anonymous class for Rain setting
        mSbRain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                String rainLabel;
                rainPosition = progress;

                rainLabel = ": " + Integer.toString(progress) + "%";
                mTvRainSbPosition.setText(rainLabel);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        singletonAlarm.setAlarms(alarms);
    }

    /**
     * this class switches to the AlarmListFragment
     */

    public void goToListView()
    {
        AlarmListFragment alf = new AlarmListFragment();

        android.support.v4.app.FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, alf)
                .addToBackStack(null)
                .commit();
    }

    /**
     * this method is the on click controller for the save / cancel buttons
     *
     * @param v View
     */

    @Override
    public void onClick(View v)
    {
        AlarmController ac = new AlarmController();
        switch (v.getId())
        {
            case R.id.btnSave:
                if (clickedAlarm == null)
                {
                    saveNewAlarm(ac);
                }
                else
                {
                    saveExistingAlarm(ac);
                }
                break;
            case R.id.btnCancel:
                DBHandler db = new DBHandler(getContext(), null, null, 1);
                singletonAlarm.setAlarms((ArrayList<Alarm>) db.getAlarms());
                goToListView();
                break;
            default:
                break;
        }
    }

    /**
     * this method saves new alarms to the database after they've been validated
     *
     * @param ac AlarmController
     */

    public void saveNewAlarm(AlarmController ac)
    {
        boolean alarmsOk = true;

        Alarm alarm = new Alarm();
        alarm.setHour(mTimePicker.getCurrentHour());
        alarm.setMinute(mTimePicker.getCurrentMinute());
        alarm.setAdjustment(adjPosition);
        alarm.setRain(rainPosition);

        //todo remove this part -> this shouldn't happen at every save
        DBHandler dbHandler = new DBHandler(getContext(), null, null, 1);
        dbHandler.addAlarm(alarm);

        alarms = (ArrayList<Alarm>) dbHandler.getAlarms();
        singletonAlarm.setAlarms(alarms);

        if (alarms != null)
        {
            for (Alarm a : alarms)
            {
                ac.createAlarmCalendar(a);
                ac.validateAlarmTime(a);

                //check that alarm adj doesn't exceed the alarm time
                if (ac.validateAlarmAdj(a))
                {
                    if (ac.setAlarm(a))
                    {
                        if (a.getEnabled() == 0)
                        {
                            ac.createAlarm(getActivity(), a);
                            a.setEnabled(1);
                            dbHandler.updateAlarm(a);
                        }
                    }
                    else
                    {
                        ac.adjustAlarmCalendar(a);
                        ac.createTimedTask(getContext(), a);
                    }
                }
                else
                {
                    String error = getResources().getString(R.string
                            .adjustment_error);
                    dbHandler.deleteAlarm(a);
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT)
                            .show();
                    alarmsOk = false;
                }
            }
            if (alarmsOk && singletonAlarm.getIsSinglePane())
            {
                alarms = (ArrayList<Alarm>) dbHandler.getAlarms();
                singletonAlarm.setAlarms(alarms);
                goToListView();
            }
            else if (!singletonAlarm.getIsSinglePane())
            {
                alarmListFragment.updateUI();
            }
        }
    }

    /**
     * this method is used to update existing alarms after being modified in the
     * AlarmEditFragment
     *
     * @param ac AlarmController
     */

    public void saveExistingAlarm(AlarmController ac)
    {
        clickedAlarm.setHour(mTimePicker.getCurrentHour());
        clickedAlarm.setMinute(mTimePicker.getCurrentMinute());
        clickedAlarm.setAdjustment(adjPosition);
        clickedAlarm.setRain(rainPosition);

        ac.createAlarmCalendar(clickedAlarm);
        ac.validateAlarmTime(clickedAlarm);

        //check that alarm adj doesn't exceed the alarm time
        if (ac.validateAlarmAdj(clickedAlarm))
        {
            if (ac.setAlarm(clickedAlarm))
            {
                ac.createAlarm(getActivity(), clickedAlarm);
            }
            else
            {
                ac.createTimedTask(getContext(), clickedAlarm);
            }
            DBHandler dbHandler = new DBHandler(getContext(), null, null, 1);
            dbHandler.updateAlarm(clickedAlarm);
            singletonAlarm.setAlarms((ArrayList<Alarm>) dbHandler.getAlarms());
            if (singletonAlarm.getIsSinglePane())
            {
                goToListView();
            }
            else
            {
                alarmListFragment.updateUI();
            }
        }
        else
        {
            String error = getResources().getString(R.string
                    .adjustment_error);
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void updateUi()
    {
        String rainText;
        String adjText;

        if (SingletonAlarm.getInstance().getClickedAlarmPosition() == null)
        {
            singletonAlarm = SingletonAlarm.getInstance();
            singletonAlarm.setAlarms(new ArrayList<Alarm>());
        }
        else
        {
            singletonAlarm = SingletonAlarm.getInstance();
        }

        if (singletonAlarm.getClickedAlarm() != null)
        {
            clickedAlarm = singletonAlarm.getClickedAlarm();

            rainText = ": " + clickedAlarm.getRain() + getActivity().getString(R.string
                    .percent_symbol);

            adjText = ": " + clickedAlarm.getAdjustment() + " " +
                    getResources().getString(R.string.adjustment_label);

            adjPosition = clickedAlarm.getAdjustment();
            rainPosition = clickedAlarm.getRain();

            mTimePicker.setCurrentHour(clickedAlarm.getHour());
            mTimePicker.setCurrentMinute(clickedAlarm.getMinute());

            mSbRain.setProgress(clickedAlarm.getRain());
            mTvRainSbPosition.setText(rainText);

            mSbAdjustment.setProgress(clickedAlarm.getAdjustment());
            mTvAdjustSbPosition.setText(adjText);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void clearInputs()
    {
        clickedAlarm = null;

        Calendar calendar;
        calendar = Calendar.getInstance();

        mTimePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        mTimePicker.setMinute(calendar.get(Calendar.MINUTE));

        mSbAdjustment.setProgress(0);
        mSbRain.setProgress(0);

        mTvAdjustSbPosition.setText("");
        mTvRainSbPosition.setText("");
    }
}