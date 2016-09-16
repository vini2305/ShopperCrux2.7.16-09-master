package com.wvs.shoppercrux.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.Utils.HttpService;
import com.wvs.shoppercrux.app.AppConfig;
import com.wvs.shoppercrux.app.AppController;
import com.wvs.shoppercrux.classes.ConnectivityReceiver;
import com.wvs.shoppercrux.helper.SQLiteHandler;
import com.wvs.shoppercrux.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TextView loginLink;
    private ProgressBar progressBar;
    private Button signUp,verifyOtpBtn;
    public EditText name,email,password,phone,inputOtp;
    private TextInputLayout nameLabel,emailLabel,passwordLabel,phoneLabel;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private LinearLayout signUpLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Log.w("Shoppercrux", "onCreate() method excecuted");

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        signUpLayout = (LinearLayout) findViewById(R.id.layout_signup);
        viewPager = (ViewPager) findViewById(R.id.viewPagerVertical);

        loginLink = (TextView) findViewById(R.id.link_login);
        loginLink.setTextColor(Color.parseColor("#ffffff"));

        verifyOtpBtn = (Button) findViewById(R.id.btn_verify_otp);
        signUp = (Button) findViewById(R.id.btn_signup);
        signUp.setTextColor(Color.parseColor("#ffffff"));
        verifyOtpBtn.setTextColor(Color.parseColor("#ffffff"));

        name = (EditText) findViewById(R.id.input_name);
        email = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);
        phone = (EditText) findViewById(R.id.input_phone);
        inputOtp = (EditText) findViewById(R.id.inputOtp);

        name.setTextColor(Color.parseColor("#ffffff"));
        email.setTextColor(Color.parseColor("#ffffff"));
        password.setTextColor(Color.parseColor("#ffffff"));
        phone.setTextColor(Color.parseColor("#ffffff"));
        inputOtp.setTextColor(Color.parseColor("#ffffff"));

        nameLabel = (TextInputLayout) findViewById(R.id.input_name_label);
        emailLabel = (TextInputLayout) findViewById(R.id.input_email_label);
        passwordLabel = (TextInputLayout) findViewById(R.id.input_password_label);
        phoneLabel = (TextInputLayout) findViewById(R.id.input_phone_label);

        nameLabel.setTypeface(font);
        emailLabel.setTypeface(font);
        passwordLabel.setTypeface(font);
        phoneLabel.setTypeface(font);


        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(this);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(SignUpActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateForm();
            }
        });

        verifyOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });

        // Manually checking internet connection
        checkConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        AppController.getInstance().setConnectivityListener(this);
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

      // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        Snackbar snackbar;
        int color;
        if (isConnected) {
            message = "Connected to Internet";
            color = Color.WHITE;
            snackbar = Snackbar
                    .make(signUpLayout, message, Snackbar.LENGTH_LONG);
        } else {
            message = "Not connected to internet !!";
            color = Color.RED;
            snackbar = Snackbar
                    .make(signUpLayout, message, Snackbar.LENGTH_INDEFINITE);
        }

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }


    /**
     * Validating user details form
     */
    private void validateForm() {

        Log.d("ShopperCrux","validating the form" );

        String fullname = name.getText().toString().trim();
        String useremail = email.getText().toString().trim();
        String userpassword = password.getText().toString().trim();
        String userphone = phone.getText().toString().trim();

        // validating empty name and email and password
        if (fullname.length() == 0 || useremail.length() == 0 || userpassword.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your details", Toast.LENGTH_SHORT).show();
            return;
        }

        // validating mobile number
        // it should be of 10 digits length
        if (isValidPhoneNumber(userphone)) {

            // request for sms
            progressBar.setVisibility(View.VISIBLE);

            // saving the mobile number in shared preferences
            session.setMobileNumber(userphone);

            // requesting for sms
            requestForSMS(fullname, useremail, userpassword,userphone);

        } else {
            Toast.makeText(getApplicationContext(), "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestForSMS(final String name, final String email, final String password, final String mobile) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("ShopperCrux", response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = jObj.getBoolean("error");
                    String message = jObj.getString("message");
                    JSONObject user = jObj.getJSONObject("user");
                    final String otp = user.getString("otp");

                    // checking for error, if not error SMS is initiated
                    // device should receive it shortly
                    if (!error) {
                        // boolean flag saying device is waiting for sms
                        session.setIsWaitingForSms(true);
                        // moving the screen to next pager item i.e otp screen
                        viewPager.setCurrentItem(1);
                        pDialog.setMessage("Please wait...");
                        pDialog.show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                pDialog.dismiss();
                                inputOtp.setText(otp);
                            }
                        }, 3000);

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(getApplicationContext(),
                                "Error: " + message,
                                Toast.LENGTH_LONG).show();
                    }

                    // hiding the progress bar
                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    Log.d("Response error","Response Error:"+e.getMessage());
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                    progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ShopperCrux", "Volley Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {

            /**
             * Passing user parameters to our server
             * @return
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstname", name);
                params.put("email", email);
                params.put("phone", mobile);
                params.put("password",password);
                Log.e("ShopperCrux", "Posting params: " + params.toString());

                return params;
            }

        };

        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strReq.setRetryPolicy(policy);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    /**
     * sending the OTP to server and activating the user
     */
    private void verifyOtp() {
        Log.w("Shoppercrux", "Verifying Otp");

        String otp = inputOtp.getText().toString().trim();

        if (!otp.isEmpty()) {
            Intent grapprIntent = new Intent(getApplicationContext(), HttpService.class);
            grapprIntent.putExtra("otp", otp);
            startService(grapprIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show();
        }
    }


    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ( object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.layout_signup;
                    break;
                case 1:
                    resId = R.id.layout_otp;
                    break;
            }
            return findViewById(resId);
        }
    }
}
















