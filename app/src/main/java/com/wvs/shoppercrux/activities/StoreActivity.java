package com.wvs.shoppercrux.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import com.wvs.shoppercrux.Stores.GetDataAdapter;
import com.wvs.shoppercrux.Stores.RecyclerViewAdapter;
import com.wvs.shoppercrux.helper.SQLiteHandler;
import com.wvs.shoppercrux.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    List<GetDataAdapter> GetDataAdapter1;
    private SearchManager searchManager;
    private SearchView searchView;
    RecyclerView recyclerView;
    private MenuItem mSearchItem;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerViewAdapter recyclerViewadapter;
    Toolbar toolbar;
    String GET_JSON_DATA_HTTP_URL = "http://shoppercrux.com/shopper_android_api/seller.php?id=";
    String JSON_IMAGE_TITLE_NAME = "nickname";
    String JSON_IMAGE_URL = "banner";
    String SELLER_ID = "seller_id";
    String SELLER_ADDRESS="seller_address";
    String STORE_URL;
    JsonArrayRequest jsonArrayRequest ;
    private MenuItem mCart;
    private SQLiteHandler db;
    private SessionManager session;
    private LayerDrawable icon;

    RequestQueue requestQueue ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Stores");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        Intent intent = getIntent();
        String id = intent.getStringExtra("location_id");
//        textView = (TextView) findViewById(R.id.display_id);
//        textView.setText(id);
        Log.d("location id",id);
         STORE_URL = GET_JSON_DATA_HTTP_URL+id;
        Log.d("location id","Store URL"+STORE_URL);
        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        JSON_DATA_WEB_CALL();
        Log.d("sellerid", SELLER_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mCart = menu.findItem(R.id.cart);
        icon = (LayerDrawable) mCart.getIcon();

        SharedPreferences preferences = getSharedPreferences(MainActivity.MY_PREFS_NAME,MODE_PRIVATE);
        String sharedText = preferences.getString("TotalCart",null);
        if(sharedText != null) {
            Product.setBadgeCount(this,icon,sharedText);
        } else {
            Product.setBadgeCount(this,icon,"0");
        }

        mSearchItem = menu.findItem(R.id.search);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(getApplicationContext(),StoreActivity.class)));
            searchView.setSubmitButtonEnabled(true);
            searchView.setQueryRefinementEnabled(true);
            searchView.setOnQueryTextListener(this);
        }

         MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener(){

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                recyclerViewadapter.setFilter(GetDataAdapter1);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        return true;
    }

      public void JSON_DATA_WEB_CALL(){

        jsonArrayRequest = new JsonArrayRequest(STORE_URL,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("location id","Store URL:"+STORE_URL);
                        JSON_PARSE_DATA_AFTER_WEBCALL(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.cart).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array){

        for(int i = 0; i<array.length(); i++) {

            GetDataAdapter getDataAdapter = new GetDataAdapter();

            JSONObject json = null;
            try {

                json = array.getJSONObject(i);

                getDataAdapter.setImageTitleNamee(json.getString(JSON_IMAGE_TITLE_NAME));
                getDataAdapter.setImageServerUrl(json.getString(JSON_IMAGE_URL));
                getDataAdapter.setSellerID(json.getString(SELLER_ID));
                getDataAdapter.setSellerAddress(json.getString(SELLER_ADDRESS).replaceAll("\t",""));
               // String seller_data =  getDataAdapter.setSellerID(json.getString(SELLER_ID));
//Log.d("sellerid", getDataAdapter.setSellerID(json.getString(SELLER_ID))

            } catch (JSONException e) {

                e.printStackTrace();
            }

            GetDataAdapter1.add(getDataAdapter);
        }

        recyclerViewadapter = new RecyclerViewAdapter(GetDataAdapter1, this);

        recyclerView.setAdapter(recyclerViewadapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_logout:
                logoutUser();
                return true;

            case R.id.action_wishlist:
                Intent intent1=new Intent(StoreActivity.this,WishListActivity.class);
                startActivity(intent1);
                return true;

            case R.id.action_reset:
                Intent intent2=new Intent(StoreActivity.this,PasswordReset.class);
                startActivity(intent2);
                return true;

            case R.id.mycart:
                Intent intent = new Intent(StoreActivity.this,CartActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        final List<GetDataAdapter> stores= filter(GetDataAdapter1,query);
        recyclerViewadapter.setFilter(stores);
        return true;
    }

    private List<GetDataAdapter> filter(List<GetDataAdapter> stores,String query){
    query = query.toLowerCase();

        final List<GetDataAdapter> storeList = new ArrayList<>();
        for(GetDataAdapter store : stores) {
            final String storeName = store.getImageTitleName().toLowerCase();
            if(storeName.contains(query)){
                storeList.add(store);
            }
        }
    return storeList;
    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(StoreActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}