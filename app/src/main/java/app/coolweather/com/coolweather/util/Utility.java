package app.coolweather.com.coolweather.util;

import android.text.TextUtils;

import org.w3c.dom.Text;

import app.coolweather.com.coolweather.db.CoolWeatherDB;
import app.coolweather.com.coolweather.model.City;
import app.coolweather.com.coolweather.model.County;
import app.coolweather.com.coolweather.model.Province;

/**
 * Created by Jack on 2016/8/14.
 */
public class Utility {
    private static final String TAG ="Utility";

    public synchronized static boolean handleProvincesResponse(CoolWeatherDB db,  String response){
        LogUtil.d(TAG, "handleProvincesResponse");
        LogUtil.d(TAG, "response=" + response);
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if(allProvinces != null && allProvinces.length > 0){
                for (String p : allProvinces) {
                    String[] a = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(a[0]);
                    province.setProvinceName(a[1]);
                    db.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCitiesResponse(CoolWeatherDB db, String response, int provinceId){
        LogUtil.d(TAG, "handleCitiesResponse, provinceId=" + provinceId);
        LogUtil.d(TAG, "response=" + response);
        if(!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            LogUtil.d(TAG, "allCities=" + allCities);
            if(allCities != null && allCities.length > 0) {
                LogUtil.d(TAG, "allCities.length=" + allCities.length);
                for (String c : allCities) {
                    String[] a = c.split("\\|");
                    City city = new City();
                    city.setCityCode(a[0]);
                    city.setCityName(a[1]);
                    city.setProvinceId(provinceId);
                    LogUtil.d(TAG, city.toString());
                    db.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCountiesResponse(CoolWeatherDB db, String response, int cityId){
        LogUtil.d(TAG, "handleCountiesResponse, cityId=" + cityId);
        LogUtil.d(TAG, "response=" + response);
        if(!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if(allCounties != null && allCounties.length > 0){
                for(String c : allCounties){
                    String[] a = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(a[0]);
                    county.setCountyName(a[1]);
                    county.setCityId(cityId);
                    db.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
}
