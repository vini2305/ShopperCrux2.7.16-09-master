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
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.wvs.shoppercrux.ProductDescription.GetDataAdapter;
import com.wvs.shoppercrux.ProductDescription.RecyclerViewAdapter;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.classes.BadgeDrawable;
import com.wvs.shoppercrux.helper.SQLiteHandler;
import com.wvs.shoppercrux.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductList extends AppCompatActivity {
    TextView productName, productPrice, productDescription,title;
    ImageView productImage;
    String STORE_URL;
    JsonArrayRequest jsonArrayRequest;
    RecyclerView.Adapter recyclerViewadapter;
    RequestQueue requestQueue;
    String GET_JSON_DATA_HTTP_URL = "http://shoppercrux.com/shopper_android_api/product_page.php?id=";
    List<GetDataAdapter> getDataAdapters;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView recyclerView;
    Toolbar toolbar;
    private MenuItem mCart;
    public static LayerDrawable icon;
    private SQLiteHandler db;
    private SessionManager session;
    public static Drawable reuse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) toolbar.findViewById(R.id.storeName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        productName = (TextView) findViewById(R.id.product_name);
        productPrice = (TextView) findViewById(R.id.product_price);
        productDescription = (TextView) findViewById(R.id.product_description);
        productImage = (ImageView) findViewById(R.id.product_image);
        getDataAdapters = new ArrayList<>();

        Intent intent = getIntent();

        String s1 = intent.getStringExtra("product_id");
        Log.d("productid", s1);
        STORE_URL = GET_JSON_DATA_HTTP_URL + s1;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);

        recyclerViewlayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        JSON_DATA_WEB_CALL();
    }

    public void JSON_DATA_WEB_CALL() {

        jsonArrayRequest = new JsonArrayRequest(STORE_URL,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mCart = menu.findItem(R.id.cart);
        icon = (LayerDrawable) mCart.getIcon();

        SharedPreferences preferences = getSharedPreferences(Product.MY_PREFS_NAME,MODE_PRIVATE);
        String sharedText = preferences.getString("TotalCart",null);
        if(sharedText != null) {
            setBadgeCount(this,icon,sharedText);
        }
        else {
            setBadgeCount(this,icon,"0");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.mycart).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {

            GetDataAdapter getDataAdapter = new GetDataAdapter();

            JSONObject json = null;
            try {

                json = array.getJSONObject(i);
                getDataAdapter.setProductImage(json.getString("image"));
                getDataAdapter.setProductName(json.getString("name"));
                getDataAdapter.setProductPrice(Double.parseDouble(json.getString("price")));
                getDataAdapter.setProductDesription(stripHtml(json.getString("description")));
                getDataAdapter.setProductId(json.getString("product_id"));

                title.setText(getDataAdapter.getProductName());

            } catch (JSONException e) {

                e.printStackTrace();
            }
            getDataAdapters.add(getDataAdapter);

        }
        recyclerViewadapter = new RecyclerViewAdapter(getDataAdapters, this);

        recyclerView.setAdapter(recyclerViewadapter);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.cart:
                Intent intent = new Intent(ProductList.this,CartActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_logout:
                logoutUser();

            case R.id.action_wishlist:
                Intent intent1=new Intent(ProductList.this,WishListActivity.class);
                startActivity(intent1);
                return true;

            case R.id.action_reset:
                Intent intent2=new Intent(ProductList.this,PasswordReset.class);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(ProductList.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
