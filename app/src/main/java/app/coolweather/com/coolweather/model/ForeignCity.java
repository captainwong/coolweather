package app.coolweather.com.coolweather.model;

/**
 * Created by Jack on 2016/8/16.
 */
public class ForeignCity extends City {
    protected long countryId;


    public long getCountryId () {
        return countryId;
    }

    public void setCountryId (long countryId) {
        this.countryId = countryId;
    }

}
