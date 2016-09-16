package com.wvs.shoppercrux.Cart;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.activities.Product;
import com.wvs.shoppercrux.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by root on 25/8/16.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<CartList> cartList;
    ImageLoader Imageloader1;
    String REMOVE_ITEM_URL,WISHLIST;
    String CART_REMOVE_URL="http://shoppercrux.com/shopper_android_api/removecart.php";
    JsonObjectRequest jsonObjectRequest,jsonObjectRequest1;
    public int count;
    String MY_PREFS_NAME="CartCount";
    private RequestQueue requestQueue;
    String GET_WISHLIST="http://shoppercrux.com/shopper_android_api/wishlist.php";
    public static String productId;
    public static String MY_WISHLIST="WishList";

    public CartAdapter(List<CartList> cartList, Context context){
        super();
        this.cartList = cartList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CartAdapter.ViewHolder holder, final int position) {

        final CartList cartPojo = cartList.get(position);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        Imageloader1 = ServerImageParseAdapter.getInstance(context).getImageLoader();

        if(cartPojo.getImageServerUrl() != null) {
            Imageloader1.get(cartPojo.getImageServerUrl(),
                    ImageLoader.getImageListener(
                            holder.prodImage,//Server Image
                            R.mipmap.ic_launcher,//Before loading server image the default showing image.
                            android.R.drawable.ic_dialog_alert //Error image if requested image dose not found on server.
                    )
            );

            holder.prodImage.setImageUrl(cartPojo.getImageServerUrl(),Imageloader1);
            holder.prodName.setText(cartPojo.getProductName());
            holder.quantity.setText(cartPojo.getProductQuantity());
            holder.price.setText(cartPojo.getTotalPrice());
            holder.increase.setImageResource(R.drawable.plus);
            holder.decrease.setImageResource(R.drawable.minus);

            holder.remove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pid= cartPojo.getProductId();
                    String customer_id = SQLiteHandler.user_id;
                    REMOVE_ITEM_URL=CART_REMOVE_URL+"?cid="+customer_id+"&pid="+pid;
                    Log.d("REMOVE_ITEM_URL","REMOVE_ITEM_URL:"+REMOVE_ITEM_URL);
                    cartList.remove(position);
                    notifyDataSetChanged();
                    removeCart();
                }
            });

            holder.movetowishlist.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pid= cartPojo.getProductId();
                    String customer_id = SQLiteHandler.user_id;
                    REMOVE_ITEM_URL=CART_REMOVE_URL+"?cid="+customer_id+"&pid="+pid;
                    WISHLIST= GET_WISHLIST+"?cid="+customer_id+"&pid="+pid;
                    cartList.remove(position);
                    notifyDataSetChanged();
                    removeCart();
                    addToWishList();
                }
            });
        } else if(cartPojo.getImageServerUrl() == null) {
            holder.buttons.setVisibility(View.GONE);
            holder.cardView.setVisibility(View.GONE);
            holder.linearLayout.setVisibility(View.GONE);
            holder.increase.setVisibility(View.GONE);
            holder.decrease.setVisibility(View.GONE);
            holder.prodImage.setVisibility(View.GONE);
            holder.price.setVisibility(View.GONE);
            holder.prodName.setVisibility(View.GONE);
            holder.quantity.setVisibility(View.GONE);
            holder.linearLayout.setLayoutParams(params);;
        }

        Log.d("Image URl","Bindview:"+cartPojo.getImageServerUrl()+","+position);

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
        return cartList.size();
    }

    public void removeCart() {
        jsonObjectRequest = new JsonObjectRequest(REMOVE_ITEM_URL, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("sc","Response"+response);

                try {
                        count = Integer.parseInt(response.getString("count"));
                        Product.setBadgeCount(context,Product.icon, String.valueOf(count));
                        SharedPreferences sharedPreferences = context.getSharedPreferences(Product.MY_PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
                        sharedEditor.putString("TotalCart", String.valueOf(count));
                        sharedEditor.apply();

                     Log.d("CartCount" ,"Cart count "+count);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        public NetworkImageView prodImage;
        public TextView prodName,quantity,price,noItemsCart;
        public ImageView increase,decrease;
        public CardView cardView;
        public LinearLayout linearLayout,buttons;
        public Button remove,movetowishlist;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.cartItems);
            cardView = (CardView) itemView.findViewById(R.id.cardview1);
            prodImage = (NetworkImageView) itemView.findViewById(R.id.cartProdImage);
            prodName = (TextView) itemView.findViewById(R.id.productName);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            price = (TextView) itemView.findViewById(R.id.price);
            increase = (ImageView) itemView.findViewById(R.id.increase);
            decrease = (ImageView) itemView.findViewById(R.id.decrease);
            remove = (Button) itemView.findViewById(R.id.remove_cart);
            movetowishlist = (Button) itemView.findViewById(R.id.move_to_wishlist);
            buttons = (LinearLayout) itemView.findViewById(R.id.buttons);
//            noItemsCart = (TextView) itemView.findViewById(R.id.noItemsCart);
        }
    }
}























