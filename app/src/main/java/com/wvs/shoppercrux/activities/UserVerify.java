package com.wvs.shoppercrux.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.payUMoney.sdk.SdkConstants;
import com.wvs.shoppercrux.Cart.CartList;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.adapters.CartSummaryAdapter;
import com.wvs.shoppercrux.app.AppConfig;
import com.wvs.shoppercrux.app.AppController;
import com.wvs.shoppercrux.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserVerify extends AppCompatActivity {

    public static final String TAG = "PayUMoneySDK Payment Gateway";

    public Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager recyclerLayoutManager;
    private List<CartList> cartListAdapter;
    private CartList cartItems;
    private CartSummaryAdapter cartAdapter;
    private Button cod,pgway,otp;
    private String MODEL="model";
    private String QUANTITY="quantity";
    String GET_CART_ITEMS_URL="http://shoppercrux.com/shopper_android_api/checkout.php";
    private String CART_URL;
    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_verify);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Otp");

        cod = (Button) findViewById(R.id.cod);
        pgway = (Button) findViewById(R.id.pgway);
        otp = (Button) findViewById(R.id.btn_otp_verify);

        pgway.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            makePayment();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.order_summary);
        recyclerView.setHasFixedSize(true);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        cartItems = new CartList();
        cartListAdapter = new ArrayList<>();
        cartListAdapter.add(cartItems);
        cartAdapter = new CartSummaryAdapter(cartListAdapter,this);
        recyclerView.setAdapter(cartAdapter);
        getCartItems();

        otp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            String userid = SQLiteHandler.user_id;
            String getOtp = otp.getText().toString().trim();
            otpVerify(getOtp,userid);
            }
        });

        cod.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid = SQLiteHandler.user_id;
                 verifyUser(userid);
            }
        });
    }

    private void verifyUser(final String userid) {

        String tag_string_req = "req_verify";
        String GET_VERIFICATION_URL="http://shoppercrux.com/shopper_android_api/checkVerified.php?cid="+userid;
        StringRequest request = new StringRequest(Request.Method.POST, GET_VERIFICATION_URL, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if(!error) {
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        placeOrder(userid);
                    } else if(error) {
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request, tag_string_req);
    }

    private void placeOrder(final String userid) {
        final String GET_ITEMS_URL="http://shoppercrux.com/shopper_android_api/checkout.php?cid="+userid;

        //Get json array from the url
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(GET_ITEMS_URL, new Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                Log.d("Items response","Items response:"+response.toString());
                postArray(userid,response.toString());
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void postArray(final String userid,final String array) {

        final String tag_string_req = "req_items";
        final String POST_ITEMS_URL="http://shoppercrux.com/shopper_android_api/cod.php";
        //Posting the response obtained from the above request
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.POST, POST_ITEMS_URL, array, new Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Order response","Order response:"+response);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting the respose here
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userid);
                params.put("response", array.toString());
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(arrayRequest, tag_string_req);
    }

    private void otpVerify(final String otp,final String userid) {
        String tag_string_req = "req_otp";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_USER_OTP_VERIFY, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Otp response","Otp response:"+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if(!error) {
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    } else if(error) {
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp", otp);
                params.put("userid", userid);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
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
                cartList.setProductQuantity(json.getString(QUANTITY));

            } catch (JSONException e){
                e.printStackTrace();

            }
            cartListAdapter.add(cartList);
        }
        cartAdapter = new CartSummaryAdapter(cartListAdapter,this);
        recyclerView.setAdapter(cartAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void makePayment() {

        PayUmoneySdkInitilizer.PaymentParam.Builder builder = new PayUmoneySdkInitilizer.PaymentParam.Builder();

        builder.setAmount(10)
                .setTnxId(getTxnId())
                .setPhone("8882434664")
                .setProductName("product_name")
                .setFirstName("vini")
                .setEmail("vinayaka@wvs.in")
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setIsDebug(false)
                .setKey("2MLn0vWG")
                .setMerchantId("5508534");// Debug Merchant ID

        PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();

        /*
         server side call required to calculate hash with the help of <salt>
         <salt> is already shared along with merchant <key>
         serverCalculatedHash =sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5|<salt>)

         (e.g.)

         sha512(FCstqb|0nf7|10.0|product_name|piyush|piyush.jain@payu.in||||||MBgjYaFG)

         9f1ce50ba8995e970a23c33e665a990e648df8de3baf64a33e19815acd402275617a16041e421cfa10b7532369f5f12725c7fcf69e8d10da64c59087008590fc

        */

        // Recommended
        calculateServerSideHashAndInitiatePayment(paymentParam);

       /*
        testing purpose

        String serverCalculatedHash="9f1ce50ba8995e970a23c33e665a990e648df8de3baf64a33e19815acd402275617a16041e421cfa10b7532369f5f12725c7fcf69e8d10da64c59087008590fc";
        paymentParam.setMerchantHash(serverCalculatedHash);
        PayUmoneySdkInitilizer.startPaymentActivityForResult(this, paymentParam);
        */
    }
    private String getTxnId() {
        return ("0nf7" + System.currentTimeMillis());
    }
    private void calculateServerSideHashAndInitiatePayment(final PayUmoneySdkInitilizer.PaymentParam paymentParam) {

        // Replace your server side hash generator API URL
        String url = "http://shoppercrux.com/shopper_android_api/moneyhash.php";

        Toast.makeText(this, "Please wait... Generating hash from server ... ", Toast.LENGTH_LONG).show();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has(SdkConstants.STATUS)) {
                        String status = jsonObject.optString(SdkConstants.STATUS);
                        if (status != null || status.equals("1")) {

                            String hash = jsonObject.getString(SdkConstants.RESULT);
                            Log.i("app_activity", "Server calculated Hash :  " + hash);

                            paymentParam.setMerchantHash(hash);

                            PayUmoneySdkInitilizer.startPaymentActivityForResult(UserVerify.this, paymentParam);
                        } else {
                            Toast.makeText(UserVerify.this,
                                    jsonObject.getString(SdkConstants.RESULT),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    Toast.makeText(UserVerify.this,
                            UserVerify.this.getString(R.string.connect_to_internet),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserVerify.this,
                            error.getMessage(),
                            Toast.LENGTH_SHORT).show();

                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paymentParam.getParams();
            }
        };
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PayUmoneySdkInitilizer.PAYU_SDK_PAYMENT_REQUEST_CODE) {

        /*if(data != null && data.hasExtra("result")){
          String responsePayUmoney = data.getStringExtra("result");
            if(SdkHelper.checkForValidString(responsePayUmoney))
                showDialogMessage(responsePayUmoney);
        } else {
            showDialogMessage("Unable to get Status of Payment");
        }*/


            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Success - Payment ID : " + data.getStringExtra(SdkConstants.PAYMENT_ID));
                String paymentId = data.getStringExtra(SdkConstants.PAYMENT_ID);
                showDialogMessage("Payment Success Id : " + paymentId);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "failure");
                showDialogMessage("cancelled");
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_FAILED) {
                Log.i("app_activity", "failure");

                if (data != null) {
                    if (data.getStringExtra(SdkConstants.RESULT).equals("cancel")) {

                    } else {
                        showDialogMessage("failure");
                    }
                }
                //Write your code if there's no result
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_BACK) {
                Log.i(TAG, "User returned without login");
                showDialogMessage("User returned without login");
            }
        }
    }

    private void showDialogMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(TAG);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }
}
