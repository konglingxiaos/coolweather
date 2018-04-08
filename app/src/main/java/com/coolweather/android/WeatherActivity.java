package com.coolweather.android;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfotext;
    private LinearLayout forecastLayout;
    private TextView aqitext;
    private TextView pm25text;
    private TextView comfortext;
    private TextView catwashtext;
    private TextView sporttext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化控件
        weatherLayout =(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
       weatherInfotext=(TextView)findViewById(R.id.weather_info_text);
       forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
       aqitext=(TextView)findViewById(R.id.aqi_text);
       pm25text=(TextView)findViewById(R.id.pm25_text);
       comfortext=(TextView)findViewById(R.id.cpmfort_text);
       catwashtext=(TextView)findViewById(R.id.car_wash_text);
       sporttext=(TextView)findViewById(R.id.sport_text);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        if (weatherString!=null){
            //有缓存时直接解析天气数据
            Weather weather= Utility.handleWeatherResponse(weatherString);
              showWeatherInfo(weather);
        }
        else {
            //无缓存时去服务器查询天气
            String weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.VISIBLE);
            requestWeather(weatherId);
        }
    }/*根据天气Id 请求城市天气信息
    */
    public void  requestWeather(final  String weatherId){
      String weatherUrl="http://guolin.tech/api/weather?cityid="+
              weatherId+"&key=eae5078020884a0e83d687b041b7b7a6";
        HttpUtil.sendOkHttp3Request(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               final String responseText=response.body().string();
               final Weather weather=Utility.handleWeatherResponse(responseText);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       if (weather!=null&&"ok".equals(weather.status)){
                           SharedPreferences.Editor editor=PreferenceManager.
                                   getDefaultSharedPreferences(WeatherActivity.this).edit();
                           editor.putString("weather",responseText);
                           editor.apply();
                          showWeatherInfo(weather);
                       }
                       else {
                           Toast.makeText(WeatherActivity.this,"获取天气失败",
                                   Toast.LENGTH_SHORT).show();}
                   }
               });
            }
        });
    }
    private void  showWeatherInfo(Weather weather){
     String cityName=weather.basic.cityName;
     String upadteTime=weather.basic.update.updateTime.split("")[1];
     String degree=weather.now.temperature+"℃";
     String weatherInfo=weather.now.mOre.info;
     titleCity.setText(cityName);
     titleUpdateTime.setText(upadteTime);
     degreeText.setText(degree);
     weatherInfotext.setText(weatherInfo);
     forecastLayout.removeAllViews();
     for (Forecast forecast:weather.forecastList){
         View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,
                 false);
         TextView datertext=(TextView)view.findViewById(R.id.date_text);
         TextView infotext=(TextView)view.findViewById(R.id.info_text);
         TextView maxtext=(TextView)view.findViewById(R.id.max_text);
         TextView mintext=(TextView)view.findViewById(R.id.min_text);
         datertext.setText(forecast.date);
         infotext.setText(forecast.more.info);
         maxtext.setText(forecast.temperatrue.Max);
        mintext.setText(forecast.temperatrue.Min);
        forecastLayout.addView(view);
     }
     if (weather.aqi !=null){
         aqitext.setText(weather.aqi.city.aqi);
         pm25text.setText(weather.aqi.city.pm25);
     }
     String comfort="舒适度："+weather.suggestion.comfort.info;
        String carwash="洗车指数："+weather.suggestion.carwash.info;
        String sport="运动建议："+weather.suggestion.sport.info;
        comfortext.setText(comfort);
        catwashtext.setText(carwash);
        sporttext.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
