package com.wvs.shoppercrux.Wishlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.wvs.shoppercrux.Product.ServerImageParseAdapter;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.activities.Product;
import com.wvs.shoppercrux.activities.WishListActivity;
import com.wvs.shoppercrux.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by root on 30/8/16.
 */
public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    Context context;
    List<WishListPojo> wishListPojo;
    ImageLoader imageLoader;
    JsonObjectRequest jsonObjectRequest,jsonObjectRequest1;
    String JSON_REMOVE_WISH_URL="http://shoppercrux.com/shopper_android_api/removewishlist.php";
    String REMOVE_URL;
    RequestQueue requestQueue;
    String GET_CART_COUNT_URL="http://shoppercrux.com/shopper_android_api/addtocart.php";
    public String CART;
    public int count;
    public static String MY_PREFS_NAME="CartCount";

    public WishListAdapter(List<WishListPojo> wishListPojo,Context context){
        super();
        this.context=context;
        this.wishListPojo=wishListPojo;
    }

    @Override
    public WishListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wish_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WishListAdapter.ViewHolder holder, final int position) {
        final WishListPojo pojo= wishListPojo.get(position);

        if(pojo.getProdImage() != null) {
            imageLoader = ServerImageParseAdapter.getInstance(context).getImageLoader();
            imageLoader.get(pojo.getProdImage(),
                    ImageLoader.getImageListener(
                            holder.image,//Server Image
                            R.mipmap.ic_launcher,//Before loading server image the default showing image.
                            android.R.drawable.ic_dialog_alert //Error image if requested image dose not found on server.
                    )
            );
            holder.image.setImageUrl(pojo.getProdImage(),imageLoader);
            holder.name.setText(pojo.getProdName());

            holder.remove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pid=pojo.getProdId();
                    String customer_id = SQLiteHandler.user_id;
                    REMOVE_URL=JSON_REMOVE_WISH_URL+"?cid="+customer_id+"&pid="+pid;
                    Log.d("wishlistremove","wishlist remove url:"+REMOVE_URL);
                    removeWishList();
                    wishListPojo.remove(position);
                    notifyDataSetChanged();

                }
            });

            holder.moveToCart.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pid=pojo.getProdId();
                    String customer_id = SQLiteHandler.user_id;
                    REMOVE_URL=JSON_REMOVE_WISH_URL+"?cid="+customer_id+"&pid="+pid;
                    CART=GET_CART_COUNT_URL+"?cid="+customer_id+"&pid="+pid;
                    Log.d("wishlistremove","wishlist remove url:"+REMOVE_URL);
                    addToCart();
                    removeWishList();
                    wishListPojo.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else {
            holder.noItems.setVisibility(View.VISIBLE);
            holder.moveToCart.setVisibility(View.GONE);
            holder.remove.setVisibility(View.GONE);
            holder.name.setVisibility(View.GONE);
            holder.image.setVisibility(View.GONE);
        }


    }

    public void addToCart() {

        jsonObjectRequest1 = new JsonObjectRequest(CART, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("sc","Response"+response);
                try {
                    count = Integer.parseInt(response.getString("count"));
                    WishListActivity.setBadgeCount(context,WishListActivity.icon, String.valueOf(count));

                    SharedPreferences sharedPreferences = context.getSharedPreferences(Product.MY_PREFS_NAME,Context.MODE_PRIVATE);
                    SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
                    sharedEditor.putString("TotalCart", String.valueOf(count));
                    sharedEditor.commit();

                    Log.d("CartCount" ,"Cart count "+count);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest1);

    }

    public void removeWishList() {
        jsonObjectRequest = new JsonObjectRequest(REMOVE_URL, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            Log.d("Response-WS","Response wl:"+response);

            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return wishListPojo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public NetworkImageView image;
        public TextView name,noItems;
        public Button remove,moveToCart;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (NetworkImageView) itemView.findViewById(R.id.prodImage);
            name = (TextView) itemView.findViewById(R.id.prodName);
            remove = (Button) itemView.findViewById(R.id.remove_wishlist);
            moveToCart = (Button) itemView.findViewById(R.id.move_to_cart);
            noItems = (TextView) itemView.findViewById(R.id.noItems);
        }
    }
}
