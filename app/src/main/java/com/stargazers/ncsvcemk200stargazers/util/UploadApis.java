package com.stargazers.ncsvcemk200stargazers.util;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApis {
    @Multipart
    @POST("/v1/vision/stage")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part part);
}
