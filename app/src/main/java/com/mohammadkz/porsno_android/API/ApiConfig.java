package com.mohammadkz.porsno_android.API;


import com.mohammadkz.porsno_android.Model.Response.CheckPhoneResponse;
import com.mohammadkz.porsno_android.Model.Response.LoginResponse;
import com.mohammadkz.porsno_android.Model.Response.SMSResponse;
import com.mohammadkz.porsno_android.Model.Response.SignUpResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiConfig {

    @FormUrlEncoded
    @POST("")
    Call<SignUpResponse> SignUp(@Field("phoneNumber") String phoneNumber
            , @Field("pwd") String pwd
            , @Field("name") String name
            , @Field("timeStamp") String time
    );

    @FormUrlEncoded
    @POST("")
    Call<CheckPhoneResponse> checkPhoneNumber(@Field("phoneNumber") String phoneNumber);

    @FormUrlEncoded
    @POST("")
    Call<LoginResponse> loginResponse(
            @Field("phoneNumber") String phoneNumber
            , @Field("pwd") String pwd
    );

    @FormUrlEncoded
    @POST("")
    Call<SMSResponse> SendSMS(@Field("phone") String phoneNumber
    );

}
