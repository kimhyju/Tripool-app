package com.gachon.kimhyju.tripool.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.object.User;
import com.gachon.kimhyju.tripool.others.ApplicationController;
import com.gachon.kimhyju.tripool.others.FriendAdapter;
import com.gachon.kimhyju.tripool.others.NetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendlistActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private NetworkService networkService;
    FriendAdapter friendAdapter;
    int user_id;
    int friend_id;
    String friend_name;
    String friend_profile_image;
    String friend_image;
    String friend_gender;
    String friend_email;
    String friend_token;
    SwipeMenuListView listView;
    SwipeMenuCreator sc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();

        sc = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setTitle("삭제");
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        listView=findViewById(R.id.friendlist_view);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        Intent intent= getIntent();
        user_id=intent.getIntExtra("user_id",0);
        friendAdapter=new FriendAdapter(getApplicationContext());
        listView.setOnItemLongClickListener(this);

    }


    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.action_add,menu);
        return true;
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_add:
                Intent intent=new Intent(FriendlistActivity.this,FriendfindActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);


    };


    public void getFriend(int user_id){
        friendAdapter.clear();
        Call<List<User>> getfriend=networkService.find_friend(user_id);
        getfriend.enqueue(new Callback<List<User>>(){
            @Override
            public void onResponse(Call<List<User>> user, Response<List<User>> response){
                if(response.isSuccessful()){
                    List<User> friendList=response.body();
                    for(User frienditem : friendList){
                        friend_name=frienditem.getNickname();
                        friend_image=frienditem.getThumbnail_image();
                        friend_email=frienditem.getEmail();
                        friend_gender=frienditem.getGender();
                        friend_profile_image=frienditem.getProfile_image();
                        friend_token=frienditem.getToken();
                        friend_id=frienditem.getUser_id();
                        friendAdapter.addItem(frienditem);
                    }
                    friendAdapter.notifyDataSetChanged();
                    listView.setAdapter(friendAdapter);
                    listView.setMenuCreator(sc);

                }else{
                    int statusCode=response.code();
                    Log.d("MyTag(onResponse)","응답코드 : "+statusCode);
                }
            }
            @Override
            public void onFailure(Call<List<User>> user, Throwable t){
                Log.d("MyTag(onFailure)","응답코드 : "+t.getMessage());
            }
        });
    }



    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id){
        User user=(User)friendAdapter.getItem(position);
        friend_id=user.getUser_id();
        PopupMenu popup=new PopupMenu(FriendlistActivity.this,view);
        getMenuInflater().inflate(R.menu.popupmenu_friendlist,popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_deletefriend:
                        showDialog();
                        break;
                }
                return false;
            }
        });
        popup.show();

        return true;
    }

    public void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("친구 삭제하기");
        builder.setMessage("정말로 친구를 삭제하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteTrip(user_id,friend_id);
                getFriend(user_id);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void deleteTrip(int user_id,int friend_id){
        Call<User> deletefriend=networkService.delete_friend(user_id,friend_id);
        deletefriend.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> user, Response<User> response){
                if(response.isSuccessful()){

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


    public void onResume(){
        super.onResume();
        getFriend(user_id);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
