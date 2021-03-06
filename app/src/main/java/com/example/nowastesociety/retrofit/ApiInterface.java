package com.example.nowastesociety.retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @Multipart
    @POST("customer/profileImageUpload")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file,@Part("userType") RequestBody requestBody,  @Header("Authorization") String Authorization);
}

