package com.wvs.shoppercrux.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.StoreProfile.StoreProfileAdapter;
import com.wvs.shoppercrux.StoreProfile.StoreProfileRecycle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 22/8/16.
 */
public class StoreProfile extends Fragment {

    private String GET_STORE_DETAILS_URL="http://shoppercrux.com/shopper_android_api/storedetails.php?id=";
    private String sellerId;
    public String STORE_URL;
    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue requestQueue;
    private List<StoreProfileAdapter> storeProfileAdapters;
    private StoreProfileRecycle storeProfileRecycle;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewlayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.store_profile,container,false);

        Intent intent = getActivity().getIntent();
        sellerId = intent.getStringExtra("seller_id");
        STORE_URL = GET_STORE_DETAILS_URL + sellerId;
        Log.d("SC","Store Url:"+STORE_URL);
        storeProfileAdapters = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        storeDetails();
        return view;
    }

    public void storeDetails(){
        jsonArrayRequest = new JsonArrayRequest(STORE_URL,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        parseStoreDetails(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue = Volley.newRequestQueue(getActivity());

        requestQueue.add(jsonArrayRequest);
    }

    public void parseStoreDetails(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {

            StoreProfileAdapter storeDetails = new StoreProfileAdapter();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                storeDetails.setStoreName(json.getString("nickname"));
                storeDetails.setStoreDescription(stripHtml(json.getString("description")));
                storeDetails.setStoreAddress(json.getString("seller_address"));
                storeDetails.setStoreContact(json.getString("telephone"));
                storeDetails.setImageServerUrl(json.getString("banner"));
                storeDetails.setLatitude(json.getString("latitude"));
                storeDetails.setLongitude(json.getString("longitude"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            storeProfileAdapters.add(storeDetails);
        }
        storeProfileRecycle = new StoreProfileRecycle(storeProfileAdapters,getActivity());
        recyclerView.setAdapter(storeProfileRecycle);
    }
    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

}
