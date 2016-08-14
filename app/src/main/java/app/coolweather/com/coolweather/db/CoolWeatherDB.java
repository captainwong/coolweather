package app.coolweather.com.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import app.coolweather.com.coolweather.model.City;
import app.coolweather.com.coolweather.model.County;
import app.coolweather.com.coolweather.model.Province;

/**
 * Created by Jack on 2016/8/14.
 */
public class CoolWeatherDB {
    public static final String DB_NAME = "cool_weather";
    public static final int VERSION = 1;
    private static CoolWeatherDB ourInstance;
    private SQLiteDatabase db;

    public static CoolWeatherDB getInstance(Context context) {
        if(ourInstance == null){
            ourInstance = new CoolWeatherDB(context);
        }
        return ourInstance;
    }

    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper helper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = helper.getWritableDatabase();
    }

    public void saveProvince(Province province){
        if(province != null){
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while(cursor.moveToNext());
        }

        return list;
    }

    public void saveCity(City city) {
        if(city != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("city_name", city.getCityName());
            contentValues.put("city_code", city.getCityCode());
            db.insert("City", null, contentValues);
        }
    }

    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id = ?",
                new String[]{String.valueOf(provinceId)}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                list.add(city);
            }while(cursor.moveToNext());
        }

        return list;
    }

    public void saveCounty(County county){
        if(county != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("county_name", county.getCountyName());
            contentValues.put("county_code", county.getCountyCode());
            contentValues.put("city_id", county.getCityId());
            db.insert("County", null, contentValues);
        }
    }

    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<>();
        Cursor cursor = db.query("County", null, "city_id = ?",
                new String[]{String.valueOf(cityId)}, null,null,null);

        if(cursor.moveToFirst()){
            do{
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                list.add(county);
            }while(cursor.moveToNext());
        }

        return list;
    }


}
