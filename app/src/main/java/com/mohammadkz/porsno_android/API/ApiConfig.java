package com.mohammadkz.porsno_android.API;


import com.mohammadkz.porsno_android.Model.Response.CheckPhoneResponse;
import com.mohammadkz.porsno_android.Model.Response.LoginResponse;
import com.mohammadkz.porsno_android.Model.Response.NewQuestionaire;
import com.mohammadkz.porsno_android.Model.Response.SMSResponse;
import com.mohammadkz.porsno_android.Model.Response.SignUpResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiConfig {

    @FormUrlEncoded
    @POST("User_Insert.php")
    Call<SignUpResponse> SignUp(@Field("phoneNumber") String phoneNumber
            , @Field("pwd") String pwd
            , @Field("userName") String name
            , @Field("created") String time
    );

    @FormUrlEncoded
    @POST("User_SELECT.php")
    Call<CheckPhoneResponse> checkPhoneNumber(@Field("phoneNumber") String phoneNumber);

    @FormUrlEncoded
    @POST("User_login.php")
    Call<LoginResponse> loginResponse(
            @Field("phoneNumber") String phoneNumber
            , @Field("pwd") String pwd
    );

    @FormUrlEncoded
    @POST("User_confirm_code.php")
    Call<SMSResponse> SendSMS(@Field("phone") String phoneNumber
    );

    @FormUrlEncoded
    @POST("Question_post.php")
    Call<NewQuestionaire> newQuestnaire(@Field("icon") String icon,
                                        @Field("questionName") String name,
                                        @Field("start") String start,
                                        @Field("end") String end,
                                        @Field("cat") String cat,
                                        @Field("desc") String desc,
                                        @Field("question") String question,
                                        @Field("userId") String userId);

    }
