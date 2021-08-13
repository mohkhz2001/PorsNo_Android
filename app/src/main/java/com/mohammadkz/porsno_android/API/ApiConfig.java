package com.mohammadkz.porsno_android.API;


import com.mohammadkz.porsno_android.Model.PriceResponse;
import com.mohammadkz.porsno_android.Model.Questionnaire;
import com.mohammadkz.porsno_android.Model.Response.CheckPhoneResponse;
import com.mohammadkz.porsno_android.Model.Response.GetQuestionResponse;
import com.mohammadkz.porsno_android.Model.Response.LoginResponse;
import com.mohammadkz.porsno_android.Model.Response.NewQuestionaire;
import com.mohammadkz.porsno_android.Model.Response.NormalResponse;
import com.mohammadkz.porsno_android.Model.Response.SMSResponse;
import com.mohammadkz.porsno_android.Model.Response.SignUpResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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

    @FormUrlEncoded
    @POST("Question_Get.php")
    Call<List<GetQuestionResponse>> getQuestions(@Field("state") String state,
                                                 @Field("inp") String inp

    );

    @FormUrlEncoded
    @POST("Question_Get.php")
    Call<GetQuestionResponse> getQuestion(@Field("state") String state,
                                          @Field("inp") String inp

    );

    @FormUrlEncoded
    @POST("User_Edit.php")
    Call<NormalResponse> updateProfile(@Field("userName") String name,
                                       @Field("pwd") String pwd,
                                       @Field("birthday") String birthdayDate,
                                       @Field("userId") String id
    );

    @FormUrlEncoded
    @POST("Answer_sent.php")
    Call<NormalResponse> saveAnswers(@Field("userId") String userId,
                                     @Field("userName") String userName,
                                     @Field("date") String date,
                                     @Field("answer") String answer,
                                     @Field("questionId") String questionId
    );

    @GET("shop_getprice.php")
    Call<List<PriceResponse>> getPrice();

    @FormUrlEncoded
    @POST("User_Upgrade.php")
    Call<NormalResponse> upgradeAccount(
            @Field("Level") String level,
                                        @Field("uId") String uId
    );
}
