package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 41993 on 2018/4/6.
 */

public class City extends DataSupport {
    private  int id;
    private String cityName;
    private int provinceId;
    private  int citycode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getCitycode() {
        return citycode;
    }

    public void setCitycode(int citycode) {
        this.citycode = citycode;
    }
}
