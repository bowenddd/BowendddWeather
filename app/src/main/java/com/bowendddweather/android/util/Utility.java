package com.bowendddweather.android.util;

import android.text.TextUtils;
import android.util.Log;

import com.bowendddweather.android.db.City;
import com.bowendddweather.android.db.County;
import com.bowendddweather.android.db.Province;
import com.bowendddweather.android.gson.Later;
import com.bowendddweather.android.gson.Suggestion;
import com.bowendddweather.android.gson.Today;
import com.bowendddweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handelProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces = new JSONArray(response);
                for(int i=0 ; i<allProvinces.length(); i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.setPrivnceName(provinceObject.getString("name"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handelCityResponse(String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCitys = new JSONArray(response);
                for(int i=0 ; i<allCitys.length() ; i++){
                    JSONObject cityObject = allCitys.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
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

    public static boolean handleCountyResponse(String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCountys = new JSONArray(response);
                for(int i=0 ; i < allCountys.length() ; i++){
                    JSONObject countysObject = allCountys.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countysObject.getString("name"));
                    county.setCityId(cityId);
                    county.setWeatherId(countysObject.getString("weather_id"));
                    county.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将返回的JSON数据解析成Weather实体类
     */

    public static <T>T handleWeatherResponse(String response,String className){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weathrtContent = jsonArray.getJSONObject(0).toString();
            Log.d("test",className);
            Log.d("test",weathrtContent);
            if(className.equals("now")){
                return (T)new Gson().fromJson(weathrtContent,Today.class);
            }
            else if(className.equals("forecast")){
                return (T)new Gson().fromJson(weathrtContent,Later.class);
            }
            else if(className.equals("lifestyle")){
                return (T)new Gson().fromJson(weathrtContent,Suggestion.class);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

