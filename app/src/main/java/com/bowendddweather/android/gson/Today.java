package com.bowendddweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Today {
    @SerializedName("basic")
    public Basic basic;

    @SerializedName("status")
    public String status;


    @SerializedName("update")
    public Update update;

    public class Update{
        @SerializedName("utc")
        public String updateTime;
    }

    @SerializedName("now")
    public Now now;

}
