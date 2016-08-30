package app.coolweather.com.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import app.coolweather.com.coolweather.util.HttpUtil;
import app.coolweather.com.coolweather.util.LogUtil;
import app.coolweather.com.coolweather.util.Utility;
import app.coolweather.com.coolweather.receiver.AutoUpdateReceiver;

/**
 * Created by Jack on 2016/8/30.
 */
public class AutoUpdateService extends Service {

    private static final String TAG = "AutoUpdateService";

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        new Thread(new Runnable() {
            @Override
            public void run () {
                updateWeather();
            }
        }).start();


        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int gap = 1 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + gap;
        Intent intent1 = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather(){
        LogUtil.i(TAG, "updateWeather");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String city_code = sharedPreferences.getString("city_code", "");
        LogUtil.i(TAG, "city_code=" + city_code);
        if(TextUtils.isEmpty(city_code)){
            return;
        }

        String address = "https://api.heweather.com/x3/weather?cityid=" + city_code
                + "&key=9eb909aa67324124a43720e1922e8c06";
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish (String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this, response);
            }

            @Override
            public void onError (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
