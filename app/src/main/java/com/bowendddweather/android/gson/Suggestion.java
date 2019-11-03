package com.bowendddweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    @SerializedName("sport")
    public Sport sport;

    public class Comfort{
        @SerializedName("brf")
        public String brf;

        @SerializedName("txt")
        public String info;

    }

    public class CarWash{
        @SerializedName("brf")
        public String brf;

        @SerializedName("txt")
        public String info;

    }

    public class Sport{
        @SerializedName("brf")
        public String brf;

        @SerializedName("txt")
        public String info;

    }

}
