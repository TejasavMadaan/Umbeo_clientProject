package com.example.umbeo.response_data;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {




    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private String token;


    public LoginResponse(String status, String token) {
        this.status = status;
        this.token = token;

    }

    public String getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }


}
