package app.coolweather.com.coolweather.model;

/**
 * Created by Jack on 2016/8/15.
 */
public class Country {

    public static final String CHINA = "中国";

    private long id;
    private String countryName;

    public long getId () {
        return id;
    }

    public void setId (long id) {
        this.id = id;
    }

    public String getCountryName () {
        return countryName;
    }

    public void setCountryName (String countryName) {
        this.countryName = countryName;
    }
}
