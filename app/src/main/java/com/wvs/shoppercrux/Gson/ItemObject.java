package com.wvs.shoppercrux.Gson;

import com.google.gson.annotations.SerializedName;

public class ItemObject {

    @SerializedName("name")
    private String locationName;
    @SerializedName("option_value_id")
    private String locationId;
    @SerializedName("pincode")
    private String locationPincode;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;


    public ItemObject(String locationName, String locationId, String locationPincode, String latitude, String longitude) {
        this.locationName = locationName;
        this.locationId = locationId;
        this.locationPincode = locationPincode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getLocationPincode() {
        return locationPincode;
    }

    public String getLatitude() { return latitude; }

    public String getLongitude() { return longitude; }
}
