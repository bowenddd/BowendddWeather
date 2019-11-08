package com.bowendddweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Later {
    @SerializedName("basic")
    public Basic basic;

    @SerializedName("status")
    public String status;


    @SerializedName("update")
    public Today.Update update;

    public class Update{
        @SerializedName("utc")
        public String updateTime;
    }

    @SerializedName("daily_forecast")
    public List<Forecast> forecasts;

}
