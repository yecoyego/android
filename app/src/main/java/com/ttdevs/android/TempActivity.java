package com.ttdevs.android;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.ttdevs.android.utils.LogUtils;

import java.util.Calendar;

public class TempActivity extends BaseActivity implements View.OnClickListener {
    public static final String ALARM_FIRST = "com.ttdevs.android.first";
    public static final String ALARM_SECOND = "com.ttdevs.android.second";

    private AlarmManager mManager;
    private PendingIntent mFirstPIntent, mSecondPIntent;
    private IntentFilter mAlarmFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        mManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);

        mFirstPIntent = PendingIntent.getBroadcast(
                this, 2333,
                new Intent(ALARM_FIRST),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mSecondPIntent = PendingIntent.getBroadcast(
                this, 4666,
                new Intent(ALARM_SECOND),
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (null == mAlarmFilter) {
            mAlarmFilter = new IntentFilter(ALARM_FIRST);
            mAlarmFilter.addAction(ALARM_FIRST);
            mAlarmFilter.addAction(ALARM_SECOND);
        }
        registerReceiver(mAlarmReceiver, mAlarmFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mAlarmReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSet:
                setAlarm();
                break;
            case R.id.btCancel:
                mManager.cancel(mFirstPIntent);
                mManager.cancel(mSecondPIntent);
                LogUtils.debug("Manager.cancel()");
                break;
            case R.id.btCancelSelf:
                mFirstPIntent.cancel();
                mSecondPIntent.cancel();
                LogUtils.debug("PIntent.cancel()");
                break;
        }
    }

    private void setAlarm() {
        Calendar calFirst = Calendar.getInstance();
        calFirst.set(Calendar.SECOND, 0);

        calFirst.add(Calendar.MINUTE, 1);

        Calendar calSecond = Calendar.getInstance();
        calSecond.setTimeInMillis(calFirst.getTimeInMillis());
        calSecond.add(Calendar.MILLISECOND, 30000);

        startAlarm(calFirst, mFirstPIntent);
        startAlarm(calSecond, mSecondPIntent);

        LogUtils.debug(calFirst.getTime().toString());
        LogUtils.debug(calSecond.getTime().toString());
    }

    private void startAlarm(Calendar calendar, PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // mManager.setWindow(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000* 5, mFirstPIntent);
            mManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            // mManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mSecondPIntent);
        } else {
            mManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            LogUtils.debug(action);
            LogUtils.showToastLong(action);
        }
    };
}
