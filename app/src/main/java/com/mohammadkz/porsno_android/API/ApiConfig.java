package com.mohammadkz.porsno_android.API;


import com.mohammadkz.porsno_android.Model.Advice;
import com.mohammadkz.porsno_android.Model.PriceResponse;
import com.mohammadkz.porsno_android.Model.Response.AnswerHistoryResponse;
import com.mohammadkz.porsno_android.Model.Response.CheckPhoneResponse;
import com.mohammadkz.porsno_android.Model.Response.ForgetPwdResponse;
import com.mohammadkz.porsno_android.Model.Response.GetAnswer24;
import com.mohammadkz.porsno_android.Model.Response.GetQuestionResponse;
import com.mohammadkz.porsno_android.Model.Response.HistoryBuyResponse;
import com.mohammadkz.porsno_android.Model.Response.LoginResponse;
import com.mohammadkz.porsno_android.Model.Response.NewQuestionaire;
import com.mohammadkz.porsno_android.Model.Response.NormalResponse;
import com.mohammadkz.porsno_android.Model.Response.SMSResponse;
import com.mohammadkz.porsno_android.Model.Response.SignUpResponse;
import com.mohammadkz.porsno_android.Model.Response.UpgradeResponse;
import com.mohammadkz.porsno_android.Model.Response.UrlResponse;

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
    Call<SMSResponse> SendSMS(@Field("phoneNumber") String phoneNumber
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
                                     @Field("questionId") String questionId,
                                     @Field("comment") String comment
    );

    @GET("shop_getprice.php")
    Call<List<PriceResponse>> getPrice();

    @FormUrlEncoded
    @POST("User_Upgrade.php")
    Call<UpgradeResponse> upgradeAccount(
            @Field("Level") String level,
            @Field("uId") String uId
    );

    @FormUrlEncoded
    @POST("History_get_answer.php")
    Call<List<AnswerHistoryResponse>> getAnswerHistory(@Field("userId") String userId);

    @FormUrlEncoded
    @POST("History_get_order.php")
    Call<List<HistoryBuyResponse>> getBuyHistory(@Field("userId") String userId);

    @FormUrlEncoded
    @POST("Idpay_make_peyment.php")
    Call<UrlResponse> getUrl(@Field("amount") String price
            , @Field("name") String name
            , @Field("phone") String phone
            , @Field("userId") String userId
            , @Field("buyedAccount") String accountName
    );

    @FormUrlEncoded
    @POST("User_password_forget.php")
    Call<ForgetPwdResponse> forgetPwd(@Field("phoneNumber") String pn);

    @FormUrlEncoded
    @POST("User_Update_pwd.php")
    Call<NormalResponse> updatePwd(@Field("phoneNumber") String pn
            , @Field("pwd") String pwd
    );

    @FormUrlEncoded
    @POST("Comment_get.php")
    Call<List<Advice>> getComment(@Field("questionId") String questionId
    );

    @FormUrlEncoded
    @POST("Answer_24_Ago.php")
    Call<GetAnswer24> getDone24(@Field("questionId") String questionId
    );

    @FormUrlEncoded
    @POST("Question_Edit.php")
    Call<NormalResponse> editQuestion(@Field("userId") String userId
            , @Field("questionId") String questionId
            , @Field("title") String title
            , @Field("desc") String desc
            , @Field("start") String start
            , @Field("end") String end
    );

    @FormUrlEncoded
    @POST("AppLog_send.php")
    Call<NormalResponse> appLog(@Field("phoneNumber") String userPn
            , @Field("log") String log
            , @Field("Location") String location
    );

    @FormUrlEncoded
    @POST("Add_report.php")
    Call<NormalResponse> addReport(@Field("qId") String qId
            , @Field("uId") String uId
            , @Field("description") String description
    );

    // this use just for Bazar IAB =>  after done the request should save the data to DB
    @FormUrlEncoded
    @POST("Order_send.php")
    Call<NormalResponse> newOrder(@Field("OrderId") String orderId
            , @Field("userId") String uId
            , @Field("amount") String amount
            , @Field("name") String name
            , @Field("phone") String phone
            , @Field("buyedAccount") String buyedAccount
            , @Field("status") String status
            , @Field("paymentTrackId") String token
    );
}
