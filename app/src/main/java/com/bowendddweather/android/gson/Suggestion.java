package com.bowendddweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Suggestion {
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
    @SerializedName("lifestyle")
    public List<Life> lifestyle;
    public class Life{
        @SerializedName("brf")
        public String brf;

        @SerializedName("txt")
        public String info;

    }

}
