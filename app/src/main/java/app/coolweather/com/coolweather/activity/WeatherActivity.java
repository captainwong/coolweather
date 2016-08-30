package app.coolweather.com.coolweather.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.coolweather.com.coolweather.R;
import app.coolweather.com.coolweather.service.AutoUpdateService;
import app.coolweather.com.coolweather.util.HttpUtil;
import app.coolweather.com.coolweather.util.LogUtil;
import app.coolweather.com.coolweather.util.Utility;

public class WeatherActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "WeatherActivity";

    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView tempText;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);

        weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText = (TextView)findViewById(R.id.city_name);
        publishText = (TextView)findViewById(R.id.publish_text);
        weatherDespText = (TextView)findViewById(R.id.weather_desp);
        tempText = (TextView)findViewById(R.id.temp);
        currentDateText = (TextView)findViewById(R.id.current_date);
        switchCity = (Button)findViewById(R.id.switch_city);
        refreshWeather = (Button)findViewById(R.id.refresh_weather);

        String city_code = getIntent().getStringExtra("city_code");
        if(!TextUtils.isEmpty(city_code)){
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherInfo(city_code);
        }else{
            showWeather();
        }

        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;

            case R.id.refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String city_code = prefs.getString("city_code", "");
                LogUtil.i(TAG, "city_code=" + city_code);
                if(!TextUtils.isEmpty(city_code)){
                    queryWeatherInfo(city_code);
                }
                break;

            default:
                break;
        }
    }


    private void queryWeatherInfo(String city_code){
        LogUtil.d(TAG, "queryWeatherInfo city_code=" + city_code);
        String address = "https://api.heweather.com/x3/weather?cityid=" + city_code
                + "&key=9eb909aa67324124a43720e1922e8c06";
        queryFromServer(address);
    }

    private void queryFromServer(final String address){
        LogUtil.d(TAG, "queryFromServer address=" + address);
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish (String response) {
                LogUtil.d(TAG, "onFinish " + ", response=" + response);

                if(!TextUtils.isEmpty(response)){
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run () {
                            showWeather();
                        }
                    });
                }

            }

            @Override
            public void onError (Exception e) {
                LogUtil.d(TAG, "onError Exception=" + e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run () {
                        publishText.setText("同步失败!");
                    }
                });
            }
        });
    }

    private void showWeather(){
        LogUtil.d(TAG, "showWeather");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(sharedPreferences.getString("city_name", ""));
        tempText.setText(sharedPreferences.getString("temp", "") + "℃");
        weatherDespText.setText(sharedPreferences.getString("weather_desp", ""));

        Resources res = getResources();
        String publishTime = String.format(res.getString(R.string.today_publish),
                //sharedPreferences.getString("publish_time", ""));
                sharedPreferences.getString("current_date", ""));
        publishText.setText(publishTime);

        currentDateText.setText(sharedPreferences.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);


        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
