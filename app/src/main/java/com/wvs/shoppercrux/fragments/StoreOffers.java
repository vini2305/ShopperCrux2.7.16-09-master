package com.wvs.shoppercrux.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.wvs.shoppercrux.StoreOffers.StoreOffersAdapter;
import com.wvs.shoppercrux.StoreOffers.StoreOffersPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 22/8/16.
 */
public class StoreOffers extends Fragment {

    String GET_OFFER_DETAILS="http://shoppercrux.com/shopper_android_api/store_offer.php";

    public String OFFER_URL;
    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewlayoutManager;
    private List<StoreOffersPojo> storeOffers;
    private StoreOffersAdapter storeOffersAdapter;
    private String sellerId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.store_offers, container, false);

        Intent intent = getActivity().getIntent();
        sellerId = intent.getStringExtra("seller_id");
        OFFER_URL = GET_OFFER_DETAILS+"?sid="+sellerId;
        Log.d("Offer Url","Offer url:"+OFFER_URL);
        storeOffers = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.store_offer);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        offerDetails();
        return view;
    }

    private void offerDetails() {
        jsonArrayRequest = new JsonArrayRequest(OFFER_URL,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                    Log.d("Offer Response","Offers response:"+response);
                        parseOfferDetails(response);
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

    private void parseOfferDetails(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {


            StoreOffersPojo pojo=new StoreOffersPojo();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);

                if(!array.getJSONObject(i).has("error")) {
                    pojo.setId(json.getString("id"));
                    pojo.setName(json.getString("name"));
                    pojo.setQuanity(json.getString("quantity"));
                    pojo.setPrice(json.getString("price"));
                } else {
                    pojo.setMessage(json.getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            storeOffers.add(pojo);
        }
        storeOffersAdapter = new StoreOffersAdapter(storeOffers,getActivity());
        recyclerView.setAdapter(storeOffersAdapter);

    }
}
