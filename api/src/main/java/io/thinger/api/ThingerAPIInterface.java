package io.thinger.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ThingerAPIInterface {
    @GET("/v2/users/{user}/devices/{device}/api")
    Call<JsonObject> deviceAPI(@Header("Authorization") String authorization, @Path("user") String user, @Path("device") String device);

    @GET("/v2/users/{user}/devices/{device}/{resource}/api")
    Call<JsonObject> resourceAPI(@Header("Authorization") String authorization, @Path("user") String user, @Path("device") String device, @Path("resource") String resource);

    @GET("/v2/users/{user}/devices/{device}/{resource}")
    Call<JsonElement> getResource(@Header("Authorization") String authorization, @Path("user") String user, @Path("device") String device, @Path("resource") String resource);

    @POST("/v2/users/{user}/devices/{device}/{resource}")
    Call<JsonElement> postResource(@Header("Authorization") String authorization, @Path("user") String user, @Path("device") String device, @Path("resource") String resource, @Body JsonElement jsonContent);
}
