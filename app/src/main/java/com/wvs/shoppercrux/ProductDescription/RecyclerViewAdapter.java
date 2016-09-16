package com.wvs.shoppercrux.ProductDescription;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.List;

/**
 * Created by JUNED on 6/16/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<GetDataAdapter> getDataAdapter;
    ImageLoader imageLoader1;
    String GET_CART_COUNT_URL="http://shoppercrux.com/shopper_android_api/addtocart.php";
    public String CART,WISHLIST;
    public int count;
    public static String MY_PREFS_NAME="CartCount";
    public static String MY_WISHLIST="WishList";
    String GET_WISHLIST="http://shoppercrux.com/shopper_android_api/wishlist.php";
    JsonObjectRequest jsonObjectRequest,jsonObjectRequest1;
    RequestQueue requestQueue;
    public static String productId;

    public RecyclerViewAdapter(List<GetDataAdapter> getDataAdapter, Context context) {

        super();
        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_description_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

       final GetDataAdapter getDataAdapter1 = getDataAdapter.get(position);

        imageLoader1 = ServerImageParseAdapter.getInstance(context).getImageLoader();

        imageLoader1.get(getDataAdapter1.getProductImage(),
                ImageLoader.getImageListener(
                        viewHolder.productImage,//Server Image
                        R.mipmap.ic_launcher,//Before loading server image the default showing image.
                        android.R.drawable.ic_dialog_alert //Error image if requested image dose not found on server.
                )
        );

        viewHolder.productImage.setImageUrl(getDataAdapter1.getProductImage(), imageLoader1);

        viewHolder.productName.setText(getDataAdapter1.getProductName());
        viewHolder.productPrice.setText(new DecimalFormat("##.##").format(getDataAdapter1.getProductPrice()));
        viewHolder.productDescription.setText(stripHtml(getDataAdapter1.getProductDesription()));

        viewHolder.addtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customer_id = SQLiteHandler.user_id;
                String product_id = getDataAdapter1.getProductId();
                CART=GET_CART_COUNT_URL+"?cid="+customer_id+"&pid="+product_id;
                Log.d("Cart Url","Cart Url"+CART);
                addToCart();
            }
        });

        viewHolder.addToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customer_id = SQLiteHandler.user_id;
                String product_id = getDataAdapter1.getProductId();
                WISHLIST= GET_WISHLIST+"?cid="+customer_id+"&pid="+product_id;
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
                    ProductList.setBadgeCount(context,ProductList.icon, String.valueOf(count));
                    Product.setBadgeCount(context,Product.icon, String.valueOf(count));

                    SharedPreferences sharedPreferences = context.getSharedPreferences(Product.MY_PREFS_NAME,Context.MODE_PRIVATE);
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

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView productName;
        public NetworkImageView productImage;
        public TextView productPrice;
        public TextView productDescription;
        public Button addtoCart,addToWishlist;

        public ViewHolder(View itemView) {

            super(itemView);
            addtoCart = (Button) itemView.findViewById(R.id.addToCart);
            addToWishlist = (Button) itemView.findViewById(R.id.addToWishList);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            productImage = (NetworkImageView) itemView.findViewById(R.id.product_image);
            productPrice = (TextView) itemView.findViewById(R.id.product_price);
            productDescription = (TextView) itemView.findViewById(R.id.product_description);
        }
    }

    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }
}
