package app.coolweather.com.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jack on 2016/8/14.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_COUNTRY = "create table Country ("
            + "id integer primary key autoincrement, "
            + "country_name text"
            + ")";

    public static final String CREATE_PROVINCE = "create table Province ("
            + "id integer primary key autoincrement, "
            + "province_name text, "
            + "province_type text, "
            + "country_id integer "
            + ")";

    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "latitude real, "
            + "longitude real, "
            + "province_id integer)";

    public static final String CREATE_FOREIGN_CITY = "create table ForeignCity ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "latitude real, "
            + "longitude real, "
            + "country_id integer)";


    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_COUNTRY);
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_FOREIGN_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        switch (oldVersion){
            case 1:
                db.execSQL("drop table if exists Country");
                db.execSQL("drop table if exists Province");
                db.execSQL("drop table if exists City");
                db.execSQL("drop table if exists County");
                db.execSQL(CREATE_COUNTRY);
                db.execSQL(CREATE_PROVINCE);
                db.execSQL(CREATE_CITY);
                db.execSQL(CREATE_FOREIGN_CITY);
            case 2:

            default:
                break;
        }
    }
}
