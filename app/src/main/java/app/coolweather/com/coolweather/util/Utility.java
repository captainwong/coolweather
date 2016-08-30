package app.coolweather.com.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import app.coolweather.com.coolweather.db.CoolWeatherDB;
import app.coolweather.com.coolweather.model.ChinaCity;
import app.coolweather.com.coolweather.model.Country;
import app.coolweather.com.coolweather.model.ForeignCity;
import app.coolweather.com.coolweather.model.Province;

/**
 * Created by Jack on 2016/8/14.
 */
public class Utility {
    private static final String TAG ="Utility";

    public static boolean handleCountriesResponse(CoolWeatherDB db, String response){
        LogUtil.d(TAG, "handleCountriesResponse");
        LogUtil.d(TAG, "response=" + response);
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);

                String status = jsonObject.getString("status");
                if(!"ok".equals(status)){
                    return false;
                }

                // country name, province name, province
                HashMap<String, HashMap<String, Province>> chinaCountryProvinces = new HashMap<>();

                // province name, city code, china city
                HashMap<String, HashMap<String, ChinaCity>> chinaProvinceCities = new HashMap<>();

                // country name, city code, foreign city
                HashMap<String, HashMap<String, ForeignCity>> foreignCountryCities = new HashMap<>();

                //
                String prevMunicipality = "";
                String prevSpecialAdmRegion = "";

                JSONArray city_info = jsonObject.getJSONArray("city_info");
                for(int i = 0; i < city_info.length(); i++){
                    JSONObject cityObj = city_info.getJSONObject(i);
                    String cityName = cityObj.getString("city");
                    String countryName = cityObj.getString("cnty");
                    String cityCode = cityObj.getString("id");
                    double lat = cityObj.getDouble("lat");
                    double lon = cityObj.getDouble("lon");

                    if(countryName.equals(Country.CHINA)) {
                        String provinceName = cityObj.getString("prov");

                        if (provinceName.equals(Province.TYPE_MUNICIPALITY)) {
                            if(Province.municipalities.contains(cityName)){
                                provinceName = cityName;
                                prevMunicipality = cityName;
                            }else{
                                provinceName = prevMunicipality;
                            }

                        }

                        if (provinceName.equals(Province.TYPE_SPECIAL_ADMINISTRATIVE_REGION)) {
                            if(Province.specialAdministrativeRegions.contains(cityName)) {
                                provinceName = cityName;
                                prevSpecialAdmRegion = cityName;
                            }else{
                                provinceName = prevSpecialAdmRegion;
                            }
                        }

                        if (!chinaCountryProvinces.containsKey(countryName)) {
                            chinaCountryProvinces.put(countryName, new HashMap<String, Province>());
                        }

                        HashMap<String, Province> provinces = chinaCountryProvinces.get(countryName);
                        if (!provinces.containsKey(provinceName)) {
                            Province province = new Province();
                            province.setProvinceName(provinceName);
                            province.setProvinceType(Province.resolveProvinceType(provinceName));
                            provinces.put(provinceName, province);
                        }

                        if (!chinaProvinceCities.containsKey(provinceName)) {
                            chinaProvinceCities.put(provinceName, new HashMap<String, ChinaCity>());
                        }

                        HashMap<String, ChinaCity> cities = chinaProvinceCities.get(provinceName);
                        if (!cities.containsKey(cityCode)) {
                            ChinaCity city = new ChinaCity();
                            city.setCityName(cityName);
                            city.setCityCode(cityCode);
                            city.setLatitude(lat);
                            city.setLongitude(lon);
                            cities.put(cityCode, city);
                        }

                    }else{
                        if(!foreignCountryCities.containsKey(countryName)){
                            foreignCountryCities.put(countryName, new HashMap<String, ForeignCity>());
                        }

                        HashMap<String, ForeignCity> foreignCities = foreignCountryCities.get(countryName);
                        if(!foreignCities.containsKey(cityCode)){
                            ForeignCity foreignCity = new ForeignCity();
                            foreignCity.setCityName(cityName);
                            foreignCity.setCityCode(cityCode);
                            foreignCity.setLatitude(lat);
                            foreignCity.setLongitude(lon);
                            foreignCities.put(cityCode, foreignCity);
                        }
                    }
                }

                // save to db
                for(HashMap.Entry<String, HashMap<String, Province>> entry : chinaCountryProvinces.entrySet()){
                    Country country = new Country();
                    country.setCountryName(entry.getKey());
                    long country_id = db.saveCountry(country);
                    HashMap<String, Province> chinaProvinces = entry.getValue();
                    for(HashMap.Entry<String, Province> provinceEntry : chinaProvinces.entrySet()){
                        Province province = provinceEntry.getValue();
                        province.setCountryId(country_id);
                        long province_id = db.saveProvince(province);

                        HashMap<String, ChinaCity> cities = chinaProvinceCities.get(province.getProvinceName());
                        for(HashMap.Entry<String, ChinaCity> cityEntry : cities.entrySet()){
                            ChinaCity city = cityEntry.getValue();
                            city.setProvinceId(province_id);
                            db.saveChinaCity(city);
                        }
                    }
                }

                for(HashMap.Entry<String, HashMap<String, ForeignCity>> entry : foreignCountryCities.entrySet()){
                    Country country = new Country();
                    country.setCountryName(entry.getKey());
                    long country_id = db.saveCountry(country);

                    HashMap<String, ForeignCity> foreignCityHashMap = entry.getValue();
                    for(HashMap.Entry<String, ForeignCity> foreignCityEntry : foreignCityHashMap.entrySet()){
                        ForeignCity city = foreignCityEntry.getValue();
                        city.setCountryId(country_id);
                        db.saveForeignCity(city);
                    }
                }
                return true;
            }catch(JSONException e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

//    public synchronized static boolean handleProvincesResponse(CoolWeatherDB db, String response){
//        LogUtil.d(TAG, "handleProvincesResponse");
//        LogUtil.d(TAG, "response=" + response);
//        if(!TextUtils.isEmpty(response)){
//            String[] allProvinces = response.split(",");
//            if(allProvinces != null && allProvinces.length > 0){
//                for (String p : allProvinces) {
//                    String[] a = p.split("\\|");
//                    Province province = new Province();
//                    province.setProvinceCode(a[0]);
//                    province.setProvinceName(a[1]);
//                    db.saveProvince(province);
//                }
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static boolean handleCitiesResponse(CoolWeatherDB db, String response, int provinceId){
//        LogUtil.d(TAG, "handleCitiesResponse, provinceId=" + provinceId);
//        LogUtil.d(TAG, "response=" + response);
//        if(!TextUtils.isEmpty(response)){
//            String[] allCities = response.split(",");
//            LogUtil.d(TAG, "allCities=" + allCities);
//            if(allCities != null && allCities.length > 0) {
//                LogUtil.d(TAG, "allCities.length=" + allCities.length);
//                for (String c : allCities) {
//                    String[] a = c.split("\\|");
//                    City city = new City();
//                    city.setCityCode(a[0]);
//                    city.setCityName(a[1]);
//                    city.setProvinceId(provinceId);
//                    LogUtil.d(TAG, city.toString());
//                    db.saveCity(city);
//                }
//                return true;
//            }
//        }
//        return false;
//    }

//    public static boolean handleCountiesResponse(CoolWeatherDB db, String response, int cityId){
//        LogUtil.d(TAG, "handleCountiesResponse, cityId=" + cityId);
//        LogUtil.d(TAG, "response=" + response);
//        if(!TextUtils.isEmpty(response)){
//            String[] allCounties = response.split(",");
//            if(allCounties != null && allCounties.length > 0){
//                for(String c : allCounties){
//                    String[] a = c.split("\\|");
//                    County county = new County();
//                    county.setCountyCode(a[0]);
//                    county.setCountyName(a[1]);
//                    county.setCityId(cityId);
//                    db.saveCounty(county);
//                }
//                return true;
//            }
//        }
//        return false;
//    }

    public static void handleWeatherResponse(Context context, String response){
        try{
            LogUtil.i(TAG, "handleWeatherResponse");
            JSONObject jsonObject = new JSONObject(response);
            JSONObject heWeatherData = jsonObject.getJSONArray("HeWeather data service 3.0").getJSONObject(0);
            JSONObject heDataBasic = heWeatherData.getJSONObject("basic");
            JSONObject heNow = heWeatherData.getJSONObject("now");
            String cityName = heDataBasic.getString("city");
            String cityCode = heDataBasic.getString("id");
            LogUtil.i(TAG, "city_code=" + cityCode);
            String tmp = heNow.getString("tmp");
            String weatherText = heNow.getJSONObject("cond").getString("txt");

            saveWeatherInfo(context, cityName, cityCode, tmp, weatherText);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context, String cityName, String cityCode,
                                       String temp, String weatherText){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("city_code", cityCode);
        editor.putString("temp", temp);
        editor.putString("weather_desp", weatherText);
        editor.putString("current_date", sdf.format(new Date()));
        editor.apply();
    }
}
