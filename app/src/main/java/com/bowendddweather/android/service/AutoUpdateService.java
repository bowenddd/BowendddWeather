package com.bowendddweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.bowendddweather.android.WeatherActivity;
import com.bowendddweather.android.gson.Weather;
import com.bowendddweather.android.util.HttpUtil;
import com.bowendddweather.android.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean find = true;
        List<String> weatherMessages = new ArrayList<>();
        for(String type:WeatherActivity.types){
            String message = prefs.getString(type,null);
            if(message == null){
                find = false;
                break;
            }
            weatherMessages.add(message);
        }
        if(find) {
            Weather weather = WeatherActivity.getWeatherMessage(weatherMessages, WeatherActivity.types);
            String weatherId = weather.today.basic.weatherId;
            String key = "b991f7b3b0234c82bc449d7d28383179";
            for (final String type : WeatherActivity.types) {
                String weatherUrl = "https://free-api.heweather.net/s6/weather/" + type + "?location=" + weatherId + "&key=" + key;
                HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseText = response.body().string();
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString(type, responseText);
                        editor.apply();
                    }
                });
            }
        }

    }
}
