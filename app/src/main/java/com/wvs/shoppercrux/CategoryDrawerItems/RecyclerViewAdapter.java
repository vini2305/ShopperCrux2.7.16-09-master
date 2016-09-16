package com.wvs.shoppercrux.CategoryDrawerItems;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.fragments.StoreFragment;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {

    private List<ItemObject> itemList;
    private Context context;
    public static String LOCATION_PIN="LocationPin";
    public static String categoryId;

    public RecyclerViewAdapter(Context context, List<ItemObject> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item2, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, final int position) {
        holder.categoryName.setText("" + itemList.get(position).getCategoryName());
        holder.categoryId.setText("" + itemList.get(position).getCategoryId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = context.getSharedPreferences(LOCATION_PIN,context.MODE_PRIVATE);
                String sharedText = preferences.getString("LocationId",null);
                if(sharedText != null) {
                    categoryId = itemList.get(position).getCategoryId();
                    StoreFragment fragment = new StoreFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("categoryId", categoryId);
                    bundle.putString("locationId",sharedText);
                    fragment.setArguments(bundle);
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .add(R.id.content_frame, fragment)
                            .commit();


                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
