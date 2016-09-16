package com.wvs.shoppercrux.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.wvs.shoppercrux.Cart.CartAdapter;
import com.wvs.shoppercrux.Cart.CartList;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.helper.SQLiteHandler;
import com.wvs.shoppercrux.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private List<CartList> cartListAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager recyclerLayoutManager;
    private CartAdapter cartAdapter;
    private CartList cartItems;
    private String MODEL="model";
    private String IMAGE="image";
    private String PRICE="price";
    private String QUANTITY="quantity";
    private String PRODUCT_ID="product_id";
    private String TOTAL ="total";
    String GET_CART_ITEMS_URL="http://shoppercrux.com/shopper_android_api/checkout.php";
    private String CART_URL;
    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue requestQueue;
    private Button checkout;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Cart");

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        checkout = (Button) findViewById(R.id.checkout);
        cartItems = new CartList();
        cartListAdapter = new ArrayList<>();
        cartListAdapter.add(cartItems);
        cartAdapter = new CartAdapter(cartListAdapter,this);
        recyclerView.setAdapter(cartAdapter);
        getCartItems();

        checkout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this,CheckoutActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.cart).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.mycart:
                Intent intent = new Intent(CartActivity.this,CartActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_logout:
                logoutUser();
                return true;

            case R.id.action_wishlist:
                Intent intent1=new Intent(CartActivity.this,WishListActivity.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(CartActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void getCartItems() {
        String cid= SQLiteHandler.user_id;
        CART_URL = GET_CART_ITEMS_URL +"?cid="+cid;
        Log.d("Checkout","checkout url:"+CART_URL);

        jsonArrayRequest = new JsonArrayRequest(CART_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
//                Log.d("Checkout","Response Checkout:"+response);
                obtainedCartItems(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void obtainedCartItems(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {

            CartList cartList = new CartList();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                    cartList.setProductName(json.getString(MODEL));
                    cartList.setProductPrice(json.getString(PRICE));
                    cartList.setProductQuantity(json.getString(QUANTITY));
                    cartList.setImageServerUrl(json.getString(IMAGE));
                    cartList.setProductId(json.getString(PRODUCT_ID));
                    cartList.setTotalPrice(json.getString(TOTAL));


            } catch (JSONException e){
                e.printStackTrace();
            }
            cartListAdapter.add(cartList);
        }
        cartAdapter = new CartAdapter(cartListAdapter,this);
        recyclerView.setAdapter(cartAdapter);
    }
}




































