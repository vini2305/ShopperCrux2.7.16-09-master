package com.wvs.shoppercrux.activities;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.app.AppConfig;
import com.wvs.shoppercrux.app.AppController;
import com.wvs.shoppercrux.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PasswordReset extends AppCompatActivity {

    public Toolbar toolbar;
    private TextInputLayout pwLabel, rpwLabel;
    private EditText password,retype;
    private Button reset;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        pwLabel = (TextInputLayout) findViewById(R.id.input_reset_password_label);
        rpwLabel = (TextInputLayout) findViewById(R.id.input_retype_password_label);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        password = (EditText) findViewById(R.id.input_reset_password);
        retype = (EditText) findViewById(R.id.input_retype_password);
        reset = (Button) findViewById(R.id.btn_reset);

        reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String passWord = password.getText().toString();
                String reType = retype.getText().toString();

                if(reType.isEmpty() || passWord.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Both the fields are mandatory",Toast.LENGTH_LONG).show();
                } else if(!reType.equals(passWord)) {
                    Toast.makeText(getApplicationContext(),"Both the password must be same",Toast.LENGTH_LONG).show();
                } else {
                    String userid = SQLiteHandler.user_id;;
                    resetPassword(userid,passWord);
                }
            }
        });

        pwLabel.setTypeface(font);
        rpwLabel.setTypeface(font);
    }

    private void resetPassword(final String userid,final String password) {
        String tag_string_req = "req_reset";

        pDialog.setMessage("Please wait..");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_RESET_PASSWORD, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if(!error) {
                        Toast.makeText(getApplicationContext(),"Password reset successfully",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Could not reset password reset!! Try later !!",Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e) {
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
                params.put("userid", userid);
                params.put("password", password);

                return params;
            }
        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
