package com.coolweather.android;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.Conty;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 41993 on 2018/4/6.
 */

public class ChooseAreaFragment extends Fragment {
    public  static final int LEVEL_PROVINCE=0;
    public  static final int LEVEL_CITY=1;
    public  static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView titleView;
    private Button backbutton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> datalist=new ArrayList<>();
    /*
       省列表
     */
    private List<Province> provinceList;
    private  List<City> cityList;
    private List<Conty>countylist;
    /**
     * 选中的省
     */
     private Province selectedProvince;
     private City selectedCity;
     private Conty selectedCounty;
     /*
     选中的级别
      */
     private int currenlevel;
    /**
     *
     * @param inflater
     * @param container
     * @param saveInstanceState
     * @return
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
       View view=inflater.inflate(R.layout.choose_area,container,false);
       titleView=(TextView) view.findViewById(R.id.title_text);
       backbutton=(Button) view.findViewById(R.id.back_button);
       listView=(ListView)view.findViewById(R.id.list_view);
       adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,datalist);
       listView.setAdapter(adapter);
       return view;
    }
    @Override
 public void onActivityCreated(Bundle savedInstanceState){
     super.onActivityCreated(savedInstanceState);
     listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
              if (currenlevel==LEVEL_PROVINCE){
                 selectedProvince=provinceList.get(position);
                 queryCities();
              }
              else if (currenlevel==LEVEL_CITY){
                  selectedCity=cityList.get(position);
                queryCounties();
              }else if (currenlevel==LEVEL_COUNTY){
                  String weatherid=countylist.get(position).getWeatherId();
                  Intent intent=new Intent(getActivity(),WeatherActivity.class);
                  intent.putExtra("weather_id",weatherid);
                  startActivity(intent);
                  getActivity().finish();
              }
         }
     });
    backbutton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (currenlevel==LEVEL_COUNTY){
                      queryCities();
            }
            else if (currenlevel==LEVEL_CITY){
               queryProvince();
            }

        }
    });
    queryProvince();

 }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再到服务器上查询
     */
 private  void queryProvince(){
     titleView.setText("中国");
     backbutton.setVisibility(View.GONE);
     provinceList=DataSupport.findAll(Province.class);
     if (provinceList.size()>0){
         datalist.clear();
         for (Province province:provinceList){
         datalist.add(province.getProvincename());
         }
         adapter.notifyDataSetChanged();
         listView.setSelection(0);
         currenlevel=LEVEL_PROVINCE;
     }
     else {
       String address="http://guolin.tech/api/china";
       queryFromSever(address,"province");
     }
 }

    /**
     * 查询选中省的所有市，优先从数据库查询，如果没有查询到再到服务器上查询
     */
    private void queryCities(){
     titleView.setText(selectedProvince.getProvincename());
     backbutton.setVisibility(View.VISIBLE);
     cityList=DataSupport.where("provinceId=?",String.valueOf(selectedProvince.getId()))
             .find(City.class);
     if (cityList.size()>0){
         datalist.clear();
         for (City city :cityList){
             datalist.add(city.getCityName());
         }
         adapter.notifyDataSetChanged();
         listView.setSelection(0);
         currenlevel=LEVEL_CITY;
     }
     else {
         int provinceCode=selectedProvince.getProvincecode();
         String address="http://guolin.tech/api/china/"+provinceCode;
         queryFromSever(address,"city");
     }
    }

    /**
     * 查询选中市的所有县，优先从数据库查询，如果没有查询到再到服务器上查询
     */
    private void queryCounties(){
        titleView.setText(selectedCity.getCityName());
        backbutton.setVisibility(View.VISIBLE);
        countylist=DataSupport.where("cityId=?",String.valueOf(selectedCity.getId()))
                .find(Conty.class);
        if (countylist.size()>0){
             datalist.clear();
             for (Conty conty:countylist){
                 datalist.add(conty.getCountyName());
             }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currenlevel=LEVEL_COUNTY;
        }
        else {
            int provinceCode=selectedProvince.getProvincecode();
            int cityCode=selectedCity.getCitycode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromSever(address,"county");
        }
    }
     private void queryFromSever(String address,final String type){
       showProgressDiglog();
         HttpUtil.sendOkHttp3Request(address, new Callback() {
             @Override
             public void onFailure(Call call, IOException e) {
              getActivity().runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      closeProgressDiglog();
                      Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                  }
              });
             }

             @Override
             public void onResponse(Call call, Response response) throws IOException {
                 String responseText=response.body().string();
                 boolean result=false;
                 if ("province".equals(type)){
                     result= Utility.handleProvinceResponse(responseText);
                 }
                 else if ("city".equals(type)){
                     result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                 }
                 else if ("county".equals(type)){
                     result=Utility.handleCountyResponse(responseText,selectedCity.getId());
                 }
                 if (result){
                     getActivity().runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             closeProgressDiglog();
                             if ("province".equals(type)){
                                 queryProvince();
                             }
                             if ("city".equals(type)){
                                 queryCities();
                             }
                             if ("county".equals(type)){
                                 queryCounties();
                             }
                         }
                     });
                 }
             }
         });
     }
     private  void showProgressDiglog(){
         if (progressDialog==null){
             progressDialog=new ProgressDialog(getActivity());
             progressDialog.setMessage("正在加载.....");
             progressDialog.setCanceledOnTouchOutside(false);
         }
         progressDialog.show();
     }
     private void closeProgressDiglog(){
   if (progressDialog!=null){
       progressDialog.dismiss();
   }
     }
}
