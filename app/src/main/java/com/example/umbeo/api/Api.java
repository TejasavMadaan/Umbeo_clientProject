package com.example.umbeo.api;

import com.example.umbeo.response_data.LoginResponse;
import com.example.umbeo.response_data.forgetpassword_response;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("signup")
    Call<ResponseBody> Signup(
            @Field("name") String name,
            @Field("phone") String number,
            @Field("email") String mail,
            @Field("password") String password,
            @Field("gps") String gps,
            @Field("latlng") String latlong,
            @Field("delivery_addresses") String[] address,
            @Field("shop") String shop,
            @Field("profile_pic") String profilePic
    );

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> userLogin(
            @Field("email") String email,
            @Field("password") String password
    );
    @FormUrlEncoded
    @POST("forgot-password")
    Call<forgetpassword_response> forgetPassword(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("reset-password")
    Call<forgetpassword_response> resetPassword(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("update-me")
    Call<forgetpassword_response> updateProfile(
            @Header("Authorization")  String header,
            @Field("shop") String shop,
            @Field("name") String name,
            @Field("profile_pic") String profilePic,
            @Field("phone") String number
    );

    @FormUrlEncoded
    @POST("update-me")
    Call<forgetpassword_response> updateName(
      @Header("Authorization") String header,
      @Field("shop") String shop,
      @Field("name") String name
    );

    @FormUrlEncoded
    @POST("update-me")
    Call<forgetpassword_response> updateAddress(
            @Header("Authorization") String header,
            @Field("shop") String shop,
            @Field("delivery_addresses") String[] address
    );

    @FormUrlEncoded
    @POST("update-me")
    Call<forgetpassword_response> updateImage(
            @Header("Authorization") String header,
            @Field("shop") String shop,
            @Field("profile_pic") String pic
    );

    @FormUrlEncoded
    @POST("update-me")
    Call<forgetpassword_response> updateNumber(
            @Header("Authorization") String header,
            @Field("shop") String shop,
            @Field("phone") String number
    );

    @FormUrlEncoded
    @POST("update-me")
    Call<forgetpassword_response> updateNumbandImage(
            @Header("Authorization") String header,
            @Field("shop") String shop,
            @Field("phone") String number,
            @Field("profile_pic") String pic
    );

    @FormUrlEncoded
    @POST("update-me")
    Call<forgetpassword_response> updateNameandImage(
            @Header("Authorization") String header,
            @Field("shop") String shop,
            @Field("name") String name,
            @Field("profile_pic") String pic
    );

    @FormUrlEncoded
    @POST("update-me")
    Call<forgetpassword_response> updateNameandNumber(
            @Header("Authorization") String header,
            @Field("shop") String shop,
            @Field("name") String name,
            @Field("phone") String number
    );
}
