package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.Conty;
import com.coolweather.android.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 41993 on 2018/4/6.
 */

public class Utility {
    /**
     *解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allprovince = new JSONArray(response);
                for (int i=0;i<allprovince.length();i++){
                    JSONObject provinceobj=allprovince.getJSONObject(i);
                    Province province=new Province();
                    province.setProvincename(provinceobj.getString("name"));
                    province.setProvincecode(provinceobj.getInt("id"));
                    province.save();
                }
                return true;
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
     return false;
    }
    /**
     * 解析和处理返回市级数据
     */
    public static  boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allcity=new JSONArray(response);
                for (int i=0;i<allcity.length();i++){
                    JSONObject cityobj=allcity.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityobj.getString("name"));
                    city.setCitycode(cityobj.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;

    }
    /**
     * 解析和处理服务器返回的县级数据
     */
   public static boolean handleCountyResponse(String response,int cityid){
       if (!TextUtils.isEmpty(response)){
           try {
               JSONArray allcounty=new JSONArray(response);
               for (int i=0;i<allcounty.length();i++)
               {
                   JSONObject countyobj=allcounty.getJSONObject(i);
                   Conty conty=new Conty();
                   conty.setCountyName(countyobj.getString("name"));
                   conty.setWeatherId(countyobj.getString("weather_id"));
                   conty.setCityId(cityid);
                   conty.save();
               }
               return true;
           }catch (JSONException e){
               e.printStackTrace();
           }
       }
       return false;
   }
}
