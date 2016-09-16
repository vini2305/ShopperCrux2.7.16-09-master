package com.wvs.shoppercrux.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wvs.shoppercrux.Cart.CartList;
import com.wvs.shoppercrux.R;

import java.util.List;

/**
 * Created by Varuni on 06-09-2016.
 */
public class CartSummaryAdapter extends RecyclerView.Adapter<CartSummaryAdapter.ViewHolder> {

    Context context;
    List<CartList> cartList;

    public CartSummaryAdapter(List<CartList> cartList, Context context) {
        super();
        this.cartList = cartList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_summary, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CartList cartPojo = cartList.get(position);

        if(position != 0) {
            holder.name.setText(cartPojo.getProductName());
            holder.quantity.setText(cartPojo.getProductQuantity());
        }
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name,quantity;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
        }
    }
}
