package com.wvs.shoppercrux.Product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.activities.Product;
import com.wvs.shoppercrux.activities.ProductList;
import com.wvs.shoppercrux.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JUNED on 6/16/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<GetDataAdapter> getDataAdapter;
    ImageLoader imageLoader1;
    JsonObjectRequest jsonObjectRequest,jsonObjectRequest1;
    private RequestQueue requestQueue;
    String GET_CART_COUNT_URL="http://shoppercrux.com/shopper_android_api/addtocart.php";
    public String CART,WISHLIST;
    public int count;
    public static String MY_PREFS_NAME="CartCount";
    public static String MY_WISHLIST="WishList";
    String GET_WISHLIST="http://shoppercrux.com/shopper_android_api/wishlist.php";
    public static String productId;

    public RecyclerViewAdapter() {

    }

    public RecyclerViewAdapter(List<GetDataAdapter> getDataAdapter, Context context) {

        super();
        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final GetDataAdapter getDataAdapter1 = getDataAdapter.get(position);

        if(getDataAdapter1.getImageServerUrl() != null) {
            imageLoader1 = ServerImageParseAdapter.getInstance(context).getImageLoader();
            imageLoader1.get(getDataAdapter1.getImageServerUrl(),
                    ImageLoader.getImageListener(
                            viewHolder.networkImageView,//Server Image
                            R.mipmap.ic_launcher,//Before loading server image the default showing image.
                            android.R.drawable.ic_dialog_alert //Error image if requested image dose not found on server.
                    )
            );

            viewHolder.networkImageView.setImageUrl(getDataAdapter1.getImageServerUrl(), imageLoader1);
            Log.d("Image URl","Bindview:"+getDataAdapter1.getImageServerUrl());
            viewHolder.ImageTitleNameView.setText(getDataAdapter1.getImageTitleName());
            viewHolder.sellerId.setText(getDataAdapter1.getProductId());
            viewHolder.storename.setText(getDataAdapter1.getStoreName());
            viewHolder.price.setText(new DecimalFormat("##.##").format(getDataAdapter1.getPrice()));
//            viewHolder.noProducts.setVisibility(View.GONE);
        } else {
            viewHolder.networkImageView.setVisibility(View.GONE);
            viewHolder.ImageTitleNameView.setVisibility(View.GONE);
            viewHolder.sellerId.setVisibility(View.GONE);
            viewHolder.storename.setVisibility(View.GONE);
            viewHolder.price.setVisibility(View.GONE);
            viewHolder.addToCart.setVisibility(View.GONE);
            viewHolder.addToWishList.setVisibility(View.GONE);
        }


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetDataAdapter getDataAdapter1 = getDataAdapter.get(position);
                String sell = Product.sellerId;
                Log.d("sellnewid", sell);
                String product_id = getDataAdapter1.getProductId();

                Intent i = new Intent(context, ProductList.class);
              //  i.putExtra("seller_id", sell);
                i.putExtra("product_id", product_id);
                context.startActivity(i);
            }
        });

        viewHolder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customer_id = SQLiteHandler.user_id;
                String product_id = getDataAdapter1.getProductId();
                CART=GET_CART_COUNT_URL+"?cid="+customer_id+"&pid="+product_id;
                Log.d("Cart Url","Cart Url"+CART);
                addToCart();
            }
        });

        SharedPreferences preferences = context.getSharedPreferences(MY_WISHLIST,Context.MODE_PRIVATE);
        String sharedText = preferences.getString("ProductId",null);

        if(sharedText == getDataAdapter1.getProductId()) {
            viewHolder.addToWishList.setImageResource(R.drawable.liked);
        } else {
            viewHolder.addToWishList.setImageResource(R.drawable.like);
        }

        viewHolder.addToWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customer_id = SQLiteHandler.user_id;
                String product_id = getDataAdapter1.getProductId();
                WISHLIST= GET_WISHLIST+"?cid="+customer_id+"&pid="+product_id;
                viewHolder.addToWishList.setImageResource(R.drawable.liked);
                addToWishList();
            }
        });

    }

    public void addToCart() {

        jsonObjectRequest = new JsonObjectRequest(CART, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("sc","Response"+response);
              try {
                  count = Integer.parseInt(response.getString("count"));
                  Product.setBadgeCount(context,Product.icon, String.valueOf(count));

                  SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);
                  SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
                  sharedEditor.putString("TotalCart", String.valueOf(count));
                  sharedEditor.apply();

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
        requestQueue.add(jsonObjectRequest);

    }

    public void addToWishList() {

        jsonObjectRequest1 = new JsonObjectRequest(WISHLIST, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    productId=response.getString("product");
                    SharedPreferences wishlistAdded = context.getSharedPreferences(MY_WISHLIST,Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=wishlistAdded.edit();
                    editor.putString("ProductId",productId);
                    editor.commit();

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

    public void setFilter(List<GetDataAdapter> products) {
     getDataAdapter = new ArrayList<>();
     getDataAdapter.addAll(products);
     notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return getDataAdapter.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView ImageTitleNameView,storename,noProducts;
        public NetworkImageView networkImageView;
        public TextView sellerId,price;
        public ImageButton addToCart,addToWishList;
        public CardView cardview1;

        public ViewHolder(View itemView) {

            super(itemView);
            ImageTitleNameView = (TextView) itemView.findViewById(R.id.textView_item);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.VollyNetworkImageView1);
            sellerId = (TextView) itemView.findViewById(R.id.tx_seller_id);
            storename = (TextView) itemView.findViewById(R.id.nickname);
            addToCart = (ImageButton) itemView.findViewById(R.id.addToCart);
            addToWishList = (ImageButton) itemView.findViewById(R.id.addToWishList);
            price = (TextView) itemView.findViewById(R.id.price);
            cardview1 = (CardView) itemView.findViewById(R.id.cardview1);
//            noProducts = (TextView) itemView.findViewById(R.id.noProducts);
        }
    }
}
