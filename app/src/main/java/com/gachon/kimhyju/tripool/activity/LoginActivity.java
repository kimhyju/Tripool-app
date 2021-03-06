package com.gachon.kimhyju.tripool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.object.User;
import com.gachon.kimhyju.tripool.others.ApplicationController;
import com.gachon.kimhyju.tripool.others.NetworkService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Gender;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private SessionCallback callback;
    private NetworkService networkService;
    int user_id;
    String nickName;
    String profile_image;
    String thumbnail_image;
    Gender gender;
    String email;
    String token;
    com.kakao.usermgmt.LoginButton kakaologin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();
        kakaologin=findViewById(R.id.com_kakao_login);


        if (Session.getCurrentSession().isClosed()) {
            callback = new SessionCallback();
            Session.getCurrentSession().addCallback(callback);

        } else {
            kakaologin.setVisibility(View.INVISIBLE);
            Handler handler =new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("action", "launch");
                    startActivity(intent);
                    finish();
                }
            }, 500);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void requestMe(){
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                redirectLoginActivity();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("error", "Session Closed Error is " + errorResult.toString());
            }


            @Override
            public void onSuccess(MeV2Response result) {
                user_id=(int)result.getId();
                nickName=result.getNickname();
                profile_image=result.getProfileImagePath();
                thumbnail_image=result.getThumbnailImagePath();
                gender=result.getKakaoAccount().getGender();
                email=result.getKakaoAccount().getEmail();
                token=FirebaseInstanceId.getInstance().getToken();
                insertUser(user_id,nickName,profile_image,thumbnail_image,String.valueOf(gender),email,token);
                requestUpdateProfile();
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.d("error", "Session Fail Error is " + exception.getMessage().toString());
        }
    }


    private void requestUpdateProfile() {
        final Map<String, String> properties = new HashMap<String, String>();
        properties.put("gender", String.valueOf(gender));
        properties.put("email",email);
        UserManagement.getInstance().requestUpdateProfile(new ApiResponseCallback<Long>() {
            @Override
            public void onSuccess(Long userid) {
                UserProfile userProfile=UserProfile.loadFromCache();
                String userEmail = userProfile.getEmail();
                userProfile.updateUserProfile(properties);
                if (userProfile != null) {
                    userProfile.saveUserToCache();
                }
                Logger.d("succeeded to update user profile" + userProfile);
            }

            @Override
            public void onNotSignedUp() {
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.e(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

        }, properties);
    }

    public void insertUser(int user_id,String nickName,String profile_image,String thumbnail_image,String gender,String email, String token){
        User user=new User();
        user.setUser_id(user_id);
        user.setNickname(nickName);
        user.setProfile_image(profile_image);
        user.setThumbnail_image(thumbnail_image);
        user.setGender(gender);
        user.setEmail(email);
        user.setToken(token);
        Call<User> insertuser=networkService.post_user(user);
        insertuser.enqueue(new Callback<User>(){
           @Override
            public void onResponse(Call<User> user, Response<User> response){
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                }else{
                    int statusCode=response.code();
                    Log.d("MyTag(onResponse)","응답코드 : "+statusCode);
                }
            }
            @Override
            public void onFailure(Call<User> user, Throwable t){
                Log.d("MyTag(onFailure)","응답코드 : "+t.getMessage());
            }
        });
    }



}
