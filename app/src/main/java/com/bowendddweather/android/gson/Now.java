package com.bowendddweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public Cond cond;
    public class Cond{
        @SerializedName("code")
        public String code;
        @SerializedName("tet")
        public String info;
    }
}
