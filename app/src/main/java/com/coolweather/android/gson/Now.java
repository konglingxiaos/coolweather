package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 41993 on 2018/4/7.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public MOre mOre;
    public class MOre{
        @SerializedName("txt")
        public String info;
    }
}
