package com.bowendddweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond_txt")
    public String cond_txt;
    @SerializedName("wind_dir")
    public String wind_dir;
    @SerializedName("hum")
    public String hum;
    @SerializedName("pres")
    public String pres;

}
