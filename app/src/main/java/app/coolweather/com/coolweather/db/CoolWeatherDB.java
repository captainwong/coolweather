package app.coolweather.com.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import app.coolweather.com.coolweather.model.ChinaCity;
import app.coolweather.com.coolweather.model.City;
import app.coolweather.com.coolweather.model.Country;
import app.coolweather.com.coolweather.model.ForeignCity;
import app.coolweather.com.coolweather.model.Province;

/**
 * Created by Jack on 2016/8/14.
 */
public class CoolWeatherDB {
    public static final String DB_NAME = "cool_weather";
    public static final int VERSION = 2;
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

    public long saveCountry(Country country){
        if(country != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("country_name", country.getCountryName());
            return db.insert("Country", null, contentValues);
        }
        return -1;
    }

    public List<Country> loadCountries(){
        List<Country> countries = new ArrayList<>();
        Cursor cursor = db.query("Country", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Country country=new Country();
                country.setId(cursor.getLong(cursor.getColumnIndex("id")));
                country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
                countries.add(country);
            }while(cursor.moveToNext());
        }

        return countries;
    }

    public long saveProvince(Province province){
        if(province != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("province_name", province.getProvinceName());
            contentValues.put("province_type", province.getProvinceType());
            contentValues.put("country_id", province.getCountryId());
            return db.insert("Province", null, contentValues);
        }
        return -1;
    }

    public List<Province> loadProvinces(long countryId) {
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("Province", null, "country_id = ?",
                new String[]{String.valueOf(countryId)}, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceType(cursor.getString(cursor.getColumnIndex("province_type")));
                province.setCountryId(countryId);
                list.add(province);
            }while(cursor.moveToNext());
        }

        return list;
    }

    public long saveChinaCity(ChinaCity city) {
        if(city != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("city_name", city.getCityName());
            contentValues.put("city_code", city.getCityCode());
            contentValues.put("latitude", city.getLatitude());
            contentValues.put("longitude", city.getLongitude());
            contentValues.put("province_id", city.getProvinceId());
            return db.insert("City", null, contentValues);
        }
        return -1;
    }

    public List<ChinaCity> loadChinaCities(long provinceId){
        List<ChinaCity> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id = ?",
                new String[]{String.valueOf(provinceId)}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                ChinaCity city = new ChinaCity();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
                city.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
                list.add(city);
            }while(cursor.moveToNext());
        }

        return list;
    }

    public long saveForeignCity(ForeignCity city) {
        if(city != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("city_name", city.getCityName());
            contentValues.put("city_code", city.getCityCode());
            contentValues.put("latitude", city.getLatitude());
            contentValues.put("longitude", city.getLongitude());
            contentValues.put("country_id", city.getCountryId());
            long id = db.insert("ForeignCity", null, contentValues);
            return id;
        }
        return -1;
    }

    public List<ForeignCity> loadForeignCities(long countryId){
        List<ForeignCity> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, "country_id = ?",
                new String[]{String.valueOf(countryId)}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                ForeignCity city = new ForeignCity();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
                city.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));

                list.add(city);
            }while(cursor.moveToNext());
        }

        return list;
    }




}
