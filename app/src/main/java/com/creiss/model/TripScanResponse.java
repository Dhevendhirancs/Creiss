package com.creiss.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripScanResponse {
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Bytes")
    @Expose
    private String bytes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }
}
