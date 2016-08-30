package app.coolweather.com.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.coolweather.com.coolweather.R;
import app.coolweather.com.coolweather.db.CoolWeatherDB;
import app.coolweather.com.coolweather.model.ChinaCity;
import app.coolweather.com.coolweather.model.City;
import app.coolweather.com.coolweather.model.Country;
import app.coolweather.com.coolweather.model.ForeignCity;
import app.coolweather.com.coolweather.model.Province;
import app.coolweather.com.coolweather.util.HttpUtil;
import app.coolweather.com.coolweather.util.LogUtil;
import app.coolweather.com.coolweather.util.Utility;

public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_COUNTRY = 0;
    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    public static final int LEVEL_FOREIGN_CITY = 3;

    public static final String COUNTRY = "country";
    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    //public static final String COUNTY = "county";

    private ProgressDialog progressDialog;
    private TextView textView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB db;
    private List<String> dataList = new ArrayList<>();

    private List<Country> countries;
    private List<Province> provinces;
    private List<ChinaCity> cities;
    private List<ForeignCity> foreignCities;

    private Country selectedCountry;
    private Province selectedProvince;
    //private City selectedCity;

    private int currentLevel;
    private static final String TAG= "ChooseAreaActivity";

    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!isFromWeatherActivity && sharedPreferences.getBoolean("city_selected", false)){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_area);
        listView = (ListView)findViewById(R.id.list_view);
        textView = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        db = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> adapterView, View view, int i, long l) {
                LogUtil.d(TAG, "onItemClick index=" + i);
                if(currentLevel == LEVEL_COUNTRY) {
                    selectedCountry = countries.get(i);
                    LogUtil.d(TAG, "LEVEL_COUNTRY countryName=" + selectedCountry.getCountryName());
                    if(Country.CHINA.equals(selectedCountry.getCountryName())){
                        queryProvinces();
                    }else{
                        queryForeignCities();
                    }

                }else if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinces.get(i);
                    LogUtil.d(TAG, "LEVEL_PROVINCE provinceName=" + selectedProvince.getProvinceName());
                    queryCities();
                }else if(currentLevel == LEVEL_CITY || currentLevel == LEVEL_FOREIGN_CITY){
                    String cityCode = cities.get(i).getCityCode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("city_code", cityCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryCountries();
    }

    @Override
    public void onBackPressed(){
        if(currentLevel == LEVEL_CITY) {
            queryProvinces();
        }else if(currentLevel == LEVEL_PROVINCE || currentLevel == LEVEL_FOREIGN_CITY){
            queryCountries();
        } else if(isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
        } else {
            finish();
        }
    }

    private void queryCountries(){
        LogUtil.d(TAG, "queryCountries");
        countries = db.loadCountries();
        LogUtil.d(TAG, "countries.size() is " + countries.size());

        if(countries.size() > 0){
            dataList.clear();
            for(Country country : countries){
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("国家");
            currentLevel = LEVEL_COUNTRY;
        }else{
            queryFromServer();
        }
    }

    private void queryProvinces(){
        LogUtil.d(TAG, "queryProvinces");
        provinces = db.loadProvinces(selectedCountry.getId());
        LogUtil.d(TAG, "provinces.size() is " + provinces.size());
        if(provinces.size() > 0){
            dataList.clear();
            for(Province province : provinces){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedCountry.getCountryName());
            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer();
        }
    }

    private void queryCities(){
        cities = db.loadChinaCities(selectedProvince.getId());
        LogUtil.d(TAG, "queryCities, cities.size()=" + cities.size());
        if(cities.size() > 0){
            dataList.clear();
            for(City city : cities){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer();
        }
    }

    private void queryForeignCities(){
        foreignCities = db.loadForeignCities(selectedCountry.getId());
        LogUtil.d(TAG, "queryForeignCities, foreignCities.size()=" + foreignCities.size());
        if(foreignCities.size() > 0){
            dataList.clear();
            for(ForeignCity city : foreignCities){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedCountry.getCountryName());
            currentLevel = LEVEL_FOREIGN_CITY;
        }else{
            queryFromServer();
        }
    }

    private void queryFromServer(){
        LogUtil.d(TAG, "queryFromServer");
        //String address = "https://api.heweather.com/x3/citylist?search=allworld&key=9eb909aa67324124a43720e1922e8c06";
        String address = "https://api.heweather.com/x3/citylist?search=allchina&key=9eb909aa67324124a43720e1922e8c06";
        LogUtil.d(TAG, "address=" + address);

        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish (String response) {
                LogUtil.d(TAG, "HttpCallbackListener.onFinish");
                boolean result = Utility.handleCountriesResponse(db, response);

                LogUtil.d(TAG, "result=" + result);

                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run () {
                            closeProgressDialog();
                            queryCountries();
                        }
                    });
                }
            }

            @Override
            public void onError (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run () {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }


}
