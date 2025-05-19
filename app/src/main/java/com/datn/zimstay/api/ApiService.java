package com.datn.zimstay.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

import com.datn.zimstay.api.models.ChangePasswordRequest;
import com.datn.zimstay.api.models.LoginRequest;
import com.datn.zimstay.api.models.LoginResponse;
import com.datn.zimstay.api.models.OtpRequest;
import com.datn.zimstay.api.models.OtpResponse;
import com.datn.zimstay.api.models.RegisterRequest;
import com.datn.zimstay.api.models.RegisterResponse;
import com.datn.zimstay.api.models.ResetPasswordRequest;
import com.datn.zimstay.api.models.ResetPasswordResponse;
import com.datn.zimstay.api.models.TokenCheckResponse;
import com.datn.zimstay.api.models.UserProfile;
import com.datn.zimstay.api.models.VerifyOtpRequest;
import com.datn.zimstay.api.models.UploadResponse;
import com.datn.zimstay.api.models.UpdateProfileRequest;
import okhttp3.MultipartBody;

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

    @GET("auth/check-token-expiration")
    Call<TokenCheckResponse> checkTokenExpiration(@Header("Authorization") String token);
    @GET("api/users/check-token")
    Call<TokenCheckResponse> getUser(@Header("Authorization") String token);

    @Multipart
    @POST("api/upload")
    Call<UploadResponse> uploadImage(
        @Part MultipartBody.Part file
    );
    @PUT("api/users/change-password")
    Call<TokenCheckResponse> changePassword(
            @Body ChangePasswordRequest request
    );
    @PUT("api/users/profile")
    Call<TokenCheckResponse> updateProfile(
        @Header("Authorization") String token,
        @Body UpdateProfileRequest request
    );
    @GET("uploads/images")
    Call<String> getImage();
} 