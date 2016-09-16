package com.wvs.shoppercrux.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.app.AppConfig;
import com.wvs.shoppercrux.app.AppController;
import com.wvs.shoppercrux.classes.ConnectivityReceiver;
import com.wvs.shoppercrux.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private Button submit;
    private EditText email;
    private ProgressDialog pDialog;
    private SessionManager session;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        email = (EditText) findViewById(R.id.input_email);
        email.setTextColor(Color.parseColor("#ffffff"));
        scrollView = (ScrollView) findViewById(R.id.forgot);
        submit = (Button) findViewById(R.id.btn_forgot);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(ForgotActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail=email.getText().toString().trim();
                if(!useremail.isEmpty()) {
                    sendPassword(useremail);
                }
                else if(useremail.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Please enter your email address",Toast.LENGTH_LONG).show();
                }
            }
        });

        checkConnection();

    }

    private void sendPassword(final String email) {

        String tag_send_password="send_password";

        pDialog.setMessage("Please wait..");
        showDialog();

        StringRequest request=new StringRequest(Request.Method.POST, AppConfig.URL_FORGOT_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("ShopperCrux", "Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");



                    if(!error) {
                        String message=jObj.getString("success_msg");
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                        // Launch main activity
                        Intent intent = new Intent(ForgotActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else if(error) {
                        String error_msg=jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),error_msg,Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ShopperCrux", "Error Response: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request, tag_send_password);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        AppController.getInstance().setConnectivityListener(this);
    }


    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        Snackbar snackbar;
        int color;
        if (isConnected) {
            message = "Connected to Internet";
            color = Color.WHITE;
            snackbar = Snackbar
                    .make(scrollView, message, Snackbar.LENGTH_LONG);
        } else {
            message = "Not connected to internet !!";
            color = Color.RED;
            snackbar = Snackbar
                    .make(scrollView, message, Snackbar.LENGTH_INDEFINITE);
        }

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }
}
