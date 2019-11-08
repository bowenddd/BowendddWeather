package com.bowendddweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bowendddweather.android.gson.Forecast;
import com.bowendddweather.android.gson.Later;
import com.bowendddweather.android.gson.Now;
import com.bowendddweather.android.gson.Suggestion;
import com.bowendddweather.android.gson.Today;
import com.bowendddweather.android.gson.Weather;
import com.bowendddweather.android.service.AutoUpdateService;
import com.bowendddweather.android.util.HttpUtil;
import com.bowendddweather.android.util.Utility;
import com.bumptech.glide.Glide;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.PreferencesFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private NestedScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private LinearLayout suggestionLayout;

    private TextView presText;

    private TextView humText;

    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefresh;

    private String mWeatherId;

    public DrawerLayout drawerLayout;

    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        weatherLayout = findViewById(R.id.weather_layout);
        weatherInfoText = findViewById(R.id.weather_info_text);
        titleCity = findViewById(R.id.title_city);
        degreeText = findViewById(R.id.degree_text);
        titleUpdateTime = findViewById(R.id.title_update_time);
        forecastLayout = findViewById(R.id.forecast_layout);
        suggestionLayout = findViewById(R.id.suggestion_layout);
        humText = findViewById(R.id.hum_text);
        presText = findViewById(R.id.pres_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        navButton = findViewById(R.id.nav_button);
        drawerLayout = findViewById(R.id.drawer_layout);
        mWeatherId = getIntent().getStringExtra("weather_id");
        weatherLayout.setVisibility(View.INVISIBLE);
        requestWeather(mWeatherId);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
                weatherLayout.fullScroll(ScrollView.FOCUS_UP);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void requestWeather(final String weatherId){
        String key = "b991f7b3b0234c82bc449d7d28383179";
        final List<String> list = new ArrayList<>();
        final List<String> types = Arrays.asList("now","forecast","lifestyle");
        final List<String> mtypes = new ArrayList<>();
        for(final String type:types) {
            String weatherUrl = "https://free-api.heweather.net/s6/weather/"+type+"?location=" + weatherId + "&key=" + key;
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WeatherActivity.this, "出现错误，获取天气信息失败!", Toast.LENGTH_LONG).show();
                        }
                    });
                    swipeRefresh.setRefreshing(false);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseText = response.body().string();
                    Log.d("test","response");
                    list.add(responseText);
                    mtypes.add(type);
                    if(list.size()==3) {
                        final Weather weather = getWeatherMessage(list, mtypes);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(weather!=null){
                                    mWeatherId = weather.today.basic.weatherId;
                                    showWeatherInfo(weather);
                                }
                                else{
                                    Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_LONG).show();
                                }
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                    }

                }
            });
        }
    }

    private void showWeatherInfo(Weather weather){
        String cityName = weather.today.basic.cityName;
        String updateTime = weather.today.update.updateTime.split(" ")[1];
        Log.d("test",updateTime);
        String degree = weather.today.now.temperature+"℃";
        String weatherInfo = weather.today.now.cond_txt;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.later.forecasts){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.info);
            maxText.setText(forecast.max);
            minText.setText(forecast.min);
            forecastLayout.addView(view);
        }
        if(weather.today.now != null){
            presText.setText(weather.today.now.pres);
            humText.setText(weather.today.now.hum);
        }
        suggestionLayout.removeAllViews();
        List<String> typelist = Arrays.asList("舒适度指数：","穿衣指数：","感冒指数：","运动指数：","旅游指数：","紫外线指数：","洗车指数：","空气污染指数：");
        for (int i=0;i<weather.suggestion.lifestyle.size();i++){
            View view = LayoutInflater.from(this).inflate(R.layout.suggestion_item,suggestionLayout,false);
            TextView suggestion_type = view.findViewById(R.id.suggestion_type);
            TextView suggestion_brf = view.findViewById(R.id.suggestion_brf);
            TextView suggestion_info = view.findViewById(R.id.suggestion_info);
            suggestion_type.setText(typelist.get(i));
            suggestion_brf.setText(weather.suggestion.lifestyle.get(i).brf);
            suggestion_info.setText(weather.suggestion.lifestyle.get(i).info);
            suggestionLayout.addView(view);

        }
        weatherLayout.setVisibility(View.VISIBLE);
        showBingPic();
        Intent intent = new Intent(this,AutoUpdateService.class);
        startService(intent);

    }

    private void showBingPic(){
        final String bingPic = "https://bing.ioliu.cn/v1/rand?type=json";
        HttpUtil.sendOkHttpRequest(bingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseJson = response.body().string();
                try{
                    final String imgUrl = new JSONObject(responseJson).getJSONObject("data").getString("url");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(imgUrl).into(bingPicImg);
                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });

    }
    private Weather getWeatherMessage(List<String>list,List<String>types){
        Weather weather = new Weather();
        for (int i=0;i<types.size();i++){
            if(types.get(i).equals("now")){
                weather.today = Utility.<Today>handleWeatherResponse(list.get(i),types.get(i));
            }
            else if(types.get(i).equals("lifestyle")){
                weather.suggestion = Utility.<Suggestion>handleWeatherResponse(list.get(i),types.get(i));
            }
            else if(types.get(i).equals("forecast")){
                weather.later = Utility.<Later>handleWeatherResponse(list.get(i),types.get(i));
            }
        }

        return weather;

    }
}

