package com.example.coordinaresponder.network;

import com.example.coordinaresponder.models.IncidentRequest;
import com.example.coordinaresponder.models.LoginRequest;
import com.example.coordinaresponder.models.SignupRequest;
import com.example.coordinaresponder.models.SupabaseAuthResponse;
import com.example.coordinaresponder.models.UserProfile;
import com.example.coordinaresponder.models.EmergencyReport;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseService {
    @POST("auth/v1/signup")
    Call<SupabaseAuthResponse> signUp(
            @Header("apikey") String apiKey,
            @Body SignupRequest body
    );

    @POST("auth/v1/token?grant_type=password")
    Call<SupabaseAuthResponse> login(
            @Header("apikey") String apiKey,
            @Body LoginRequest body
    );

    @POST("rest/v1/users")
    Call<Void> createUserProfile(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Body UserProfile profile
    );

    @GET("rest/v1/users")
    Call<List<UserProfile>> getUserProfile(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("id") String userId
    );

    @PATCH("rest/v1/users")
    Call<Void> updateUserProfile(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("id") String userId,
            @Body Map<String, Object> updates
    );

    @POST("rest/v1/incidents")
    Call<Void> createIncident(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Body IncidentRequest incident
    );

    @GET("rest/v1/incidents?select=*")
    Call<List<EmergencyReport>> getIncidents(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("reported_by") String userId,
            @Query("order") String order
    );

    @GET("rest/v1/incidents?select=*")
    Call<List<EmergencyReport>> getIncidentsFiltered(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("reported_by") String userId,
            @Query("status") String statusCondition,
            @Query("order") String order
    );
}
