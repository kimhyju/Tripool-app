package com.gachon.kimhyju.tripool.others;

import com.gachon.kimhyju.tripool.object.Checklist;
import com.gachon.kimhyju.tripool.object.Trip;
import com.gachon.kimhyju.tripool.object.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NetworkService {
    //User
    @POST("/User")
    Call<User> post_user(@Body User user);
    @DELETE("/User/{user_id}")
    Call<User> delete_User(@Path("user_id") int user_id);
    @GET("/User/{email}")
    Call<User> find_User(@Path("email") String email);

    //Token
    @PUT("/Token")
    Call<User> update_token(@Body User user);

    //Friend
    @POST("/Friend/{user_id}/{friend_id}")
    Call<User> add_friend(@Path("user_id") int user_id, @Path("friend_id") int friend_id);
    @GET("/Friend/{user_id}")
    Call<List<User>> find_friend(@Path("user_id") int user_id);
    @DELETE("/Friend/{user_id}/{friend_id}")
    Call<User> delete_friend(@Path("user_id") int user_id, @Path("friend_id") int friend_id);

    //Trip
    @POST("/Trip")
    Call<Trip> create_trip(@Body Trip trip);
    @PUT("/Trip")
    Call<Trip> modify_trip(@Body Trip trip);
    @DELETE("/Trip/{trip_id}/{user_id}")
    Call<Trip> delete_trip(@Path("trip_id") String trip_id, @Path("user_id") int user_id);
    @GET("/Trip/{user_id}")
    Call<List<Trip>> get_trip(@Path("user_id") int user_id);

    //Trip_join
    @POST("/Trip_join/{trip_id}/{user_id}")
    Call<Trip> join_trip(@Path("trip_id")String trip_id, @Path("user_id") int user_id);

    //Trip_main
    @PUT("/Trip_main/{trip_id}/{user_id}")
    Call<Trip> select_trip(@Path("trip_id")String trip_id, @Path("user_id") int user_id);
    @GET("/Trip_main/{user_id}")
    Call<Trip> get_maintrip(@Path("user_id") int user_id);

    //Checklist
    @POST("/Checklist")
    Call<Checklist> add_checklist(@Body Checklist checklist);
    @GET("/Checklist/{trip_id}")
    Call<List<Checklist>> get_checklist(@Path("trip_id") String trip_id);
}
