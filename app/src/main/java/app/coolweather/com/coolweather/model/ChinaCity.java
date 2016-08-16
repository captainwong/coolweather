package app.coolweather.com.coolweather.model;

/**
 * Created by Jack on 2016/8/16.
 */
public class ChinaCity extends City {
    protected long provinceId;

    public long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(long provinceId) {
        this.provinceId = provinceId;
    }


}
