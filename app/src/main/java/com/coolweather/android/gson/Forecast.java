package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 41993 on 2018/4/7.
 */

public class Forecast {
    public String date;

    @SerializedName("tmp")
    public Temperatrue temperatrue;

    @SerializedName("cond")
    public More more;

    public class Temperatrue{
        public  String Max;
        public String Min;
    }
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
