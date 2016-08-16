package app.coolweather.com.coolweather.model;

/**
 * Created by Jack on 2016/8/14.
 */
public class City {
    protected long id;
    protected String cityName;
    protected String cityCode;
    protected double latitude;
    protected double longitude;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public double getLatitude () {
        return latitude;
    }

    public void setLatitude (double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude () {
        return longitude;
    }

    public void setLongitude (double longitude) {
        this.longitude = longitude;
    }


}
