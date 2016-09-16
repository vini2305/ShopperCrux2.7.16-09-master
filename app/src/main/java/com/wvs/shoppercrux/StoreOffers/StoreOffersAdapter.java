package com.wvs.shoppercrux.StoreOffers;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wvs.shoppercrux.R;

import java.util.List;

/**
 * Created by Varuni on 08-09-2016.
 */
public class StoreOffersAdapter extends RecyclerView.Adapter<StoreOffersAdapter.ViewHolder> {

    Activity activity;
    List<StoreOffersPojo> storeOffers;

    public StoreOffersAdapter(List<StoreOffersPojo> storeOffers,Activity activity) {
        super();
        this.storeOffers = storeOffers;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_offers_recycle, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StoreOffersPojo values = storeOffers.get(position);

            if(values.getMessage() == null){
           // Log.d("value", "Name:" + values.getName());
            //  Log.d("value","Message:"+values.getMessage());
            holder.offer.setText(values.getName() + " is being sold at " + values.getPrice() + " per " + values.getQuanity() + " items");
            //  holder.noOffer.setVisibility(View.GONE);
        } else {
            // holder.noOffer.setText(values.getMessage());
            holder.offer.setText("No offers yet");
        }


    }

    @Override
    public int getItemCount() {
        return storeOffers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView offer,noOffer;

        public ViewHolder(View itemView) {
            super(itemView);
            offer = (TextView) itemView.findViewById(R.id.offer);
            noOffer = (TextView) itemView.findViewById(R.id.no_offer);
        }
    }
}
