package com.wvs.shoppercrux.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.Wishlist.WishListAdapter;
import com.wvs.shoppercrux.Wishlist.WishListPojo;
import com.wvs.shoppercrux.classes.BadgeDrawable;
import com.wvs.shoppercrux.helper.SQLiteHandler;
import com.wvs.shoppercrux.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WishListActivity extends AppCompatActivity {

    private MenuItem mCart;
    public static LayerDrawable icon;
    private Toolbar mToolbar;
    private SQLiteHandler db;
    private SessionManager session;
    JsonArrayRequest jsonArrayRequest;
    RequestQueue requestQueue;
    String GET_WISHLIST_URL="http://shoppercrux.com/shopper_android_api/wishlisted.php";
    String WISHLIST;
    List<WishListPojo> wishList;
    WishListAdapter wishListAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager recyclerLayoutManager;
    public static Drawable reuse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Wishlist");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.wishlistRecycle);
        recyclerView.setHasFixedSize(true);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        wishList = new ArrayList<>();

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        wishListData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mCart = menu.findItem(R.id.cart);
        icon = (LayerDrawable) mCart.getIcon();

        SharedPreferences preferences = getSharedPreferences(Product.MY_PREFS_NAME,MODE_PRIVATE);
        String sharedText = preferences.getString("TotalCart",null);
        if(sharedText != null) {
            setBadgeCount(this,icon,sharedText);
        } else {
            setBadgeCount(this,icon,"0");
        }

        return true;
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(WishListActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.mycart:
                Intent intent = new Intent(WishListActivity.this,CartActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_logout:
                logoutUser();
                return true;

            case R.id.action_wishlist:
                Intent intent1=new Intent(WishListActivity.this,WishListActivity.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.cart).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    public void wishListData(){

        String cid= SQLiteHandler.user_id;
        WISHLIST = GET_WISHLIST_URL+"?cid="+cid;
        Log.d("WishList","WishList URl:"+WISHLIST);
        jsonArrayRequest = new JsonArrayRequest(WISHLIST, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Wishlist","Wishlist Response"+response);
                wishListItems(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    public void wishListItems(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            WishListPojo wishListPojo1= new WishListPojo();
            JSONObject jsonObject=null;
            try {
                jsonObject = array.getJSONObject(i);
                String name=jsonObject.getString("model");
                String image=jsonObject.getString("image");
                String id=jsonObject.getString("product_id");

                if(array.getJSONObject(i).has("empty")){
                    String message ="No items";
                } else {
                    wishListPojo1.setProdName(name);
                    wishListPojo1.setProdImage(image);
                    wishListPojo1.setProdId(id);
                }


            } catch (JSONException e){
                e.printStackTrace();
            }
            wishList.add(wishListPojo1);
        }
        wishListAdapter = new WishListAdapter(wishList,this);
        recyclerView.setAdapter(wishListAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {
        BadgeDrawable badge;
        // Reuse drawable if possible
        reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

}































