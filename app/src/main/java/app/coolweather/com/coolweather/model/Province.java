package app.coolweather.com.coolweather.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jack on 2016/8/14.
 */
public class Province {

    public static final String TYPE_PROVINCE = "省";
    public static final String TYPE_MUNICIPALITY = "直辖市";
    public static final String TYPE_SPECIAL_ADMINISTRATIVE_REGION = "特别行政区";

    public static final List<String> municipalities =
            new ArrayList<>(Arrays.asList("北京", "天津", "上海", "重庆"));

    public static final List<String> specialAdministrativeRegions =
            new ArrayList<>(Arrays.asList("香港", "澳门"));

    private long id;
    private String provinceName;
    private String provinceType;
    private long countryId;

    public static String resolveProvinceType(final String provinceType){
        if(TYPE_MUNICIPALITY.equals(provinceType)){
            return TYPE_MUNICIPALITY;
        }else if(TYPE_SPECIAL_ADMINISTRATIVE_REGION.equals(provinceType)){
            return TYPE_SPECIAL_ADMINISTRATIVE_REGION;
        }else{
            return TYPE_PROVINCE;
        }
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceType () {
        return provinceType;
    }

    public void setProvinceType (String provinceType) {
        this.provinceType = provinceType;
    }

    public long getCountryId () {
        return countryId;
    }

    public void setCountryId (long countryId) {
        this.countryId = countryId;
    }
}
