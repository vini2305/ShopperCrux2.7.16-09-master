package com.wvs.shoppercrux.classes;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.wvs.shoppercrux.app.AppConfig;
import com.wvs.shoppercrux.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by root on 9/8/16.
 */
public class CategoryFetch {

    Context context;
    ArrayList<Category> arrayList = new ArrayList<>();
    String json_url= AppConfig.URL_CATEGORY;

    public CategoryFetch(Context context) {
        this.context = context;
    }

    public ArrayList<Category> getCategory(){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, json_url, (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        int count = 0;
                        while (count<response.length()){
                            try {
                                JSONObject jsonObject = response.getJSONObject(count);

                                Category category = new Category(jsonObject.getString("category_id"),jsonObject.getString("name"));
                                System.out.println(category);
                                arrayList.add(category);
                                count++;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "Error  ", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        AppController.getInstance().addToRequestque(jsonArrayRequest);
        return arrayList;
    }

}
