package com.datn.zimstay.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

import com.datn.zimstay.api.models.ApartmentResponse;
import com.datn.zimstay.api.models.ApartmentsResponse;
import com.datn.zimstay.api.models.ChangePasswordRequest;
import com.datn.zimstay.api.models.ConversationResponse;
import com.datn.zimstay.api.models.GetTinhResponse;
import com.datn.zimstay.api.models.LoginRequest;
import com.datn.zimstay.api.models.LoginResponse;
import com.datn.zimstay.api.models.MessageRequest;
import com.datn.zimstay.api.models.MessageResponse;
import com.datn.zimstay.api.models.OtpRequest;
import com.datn.zimstay.api.models.OtpResponse;
import com.datn.zimstay.api.models.RegisterRequest;
import com.datn.zimstay.api.models.RegisterResponse;
import com.datn.zimstay.api.models.ResetPasswordRequest;
import com.datn.zimstay.api.models.ResetPasswordResponse;
import com.datn.zimstay.api.models.TokenCheckResponse;
import com.datn.zimstay.api.models.VerifyOtpRequest;
import com.datn.zimstay.api.models.UpdateProfileRequest;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/users/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/users/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @POST("api/otp/send")
    Call<OtpResponse> sendOtp(@Body OtpRequest otpRequest);

    @POST("api/otp/verify")
    Call<OtpResponse> verifyOtp(@Body VerifyOtpRequest verifyOtpRequest);

    @POST("api/users/reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);

    @POST("api/otp/sendOTPreset")
    Call<OtpResponse> sendOTPreset(@Body OtpRequest otpRequest);

    @GET("api/users/me")
    Call<TokenCheckResponse> checkTokenExpiration(@Header("Authorization") String token);
    @GET("api/users/check-token")
    Call<TokenCheckResponse> getUser(@Header("Authorization") String token);

    @Multipart
    @POST("api/upload")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file);

    @PUT("api/users/change-password")
    Call<TokenCheckResponse> changePassword(@Body ChangePasswordRequest request);

    @PUT("api/users/profile")
    Call<TokenCheckResponse> updateProfile(@Header("Authorization") String token, @Body UpdateProfileRequest request);

    @GET("uploads/images/{filename}")
    String getImageUrl(@Path("filename") String filename);

    @GET("/api/apartments/{id}")
    Call<ApartmentResponse> getApartmentById(@Path("id") int id);

    @GET("api/dia-chi/tinh")
    Call<ArrayList<GetTinhResponse>> getTinh();

    @GET("api/dia-chi/tinh/{id_tinh}/huyen")
    Call<ArrayList<GetTinhResponse>> getQuanHuyen(@Path("id_tinh") String id);

    @GET("/api/dia-chi/tinh/{idTinh}/huyen/{idHuyen}/xa")
    Call<ArrayList<GetTinhResponse>> getXa(@Path("idTinh") int idTinh, @Path("idHuyen") int idHuyen);

    @POST("api/apartments/search")
    Call<ApartmentsResponse>searchApartments(@Body JsonObject body);

    @POST("/api/conversations")
    Call<ConversationResponse> createConversation(
            @Query("user1Id") int user1Id,
            @Query("user2Id") int user2Id,
            @Query("apartmentIds") int apartmentId
    );
    @GET("/api/conversations/{conversationId}")
    Call<ConversationResponse> getConversation(@Path("conversationId") int conversationId);

    @GET("/api/conversations/{id}")
    Call<ConversationResponse> getConversationById(@Path("id") int id);

    @GET("/api/conversations/user/{userId}")
    Call<ArrayList<ConversationResponse>> getConversationsByUserId(@Path("userId") int userId);

    @GET("/api/conversations/between?user1Id={user1Id}&user2Id={user2Id}")
    Call<ConversationResponse> getConversationBetweenUsers(@Path("user1Id") int user1Id, @Path("user2Id") int user2Id);

    @GET("/api/users/{id}")
    Call<TokenCheckResponse> getUserById(@Path("id") int id);

    @GET("/api/messages/conversation/{conversationId}")
    Call<List<MessageResponse>> getMessagesByConversationId(
            @Path("conversationId") int conversationId,
            @Query("userId") int userId);
    @POST("api/messages")
    Call<MessageResponse> sendMessage(@Body MessageRequest messageRequest);
}