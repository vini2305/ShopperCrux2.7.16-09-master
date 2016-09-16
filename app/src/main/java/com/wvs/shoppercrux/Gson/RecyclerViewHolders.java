package com.wvs.shoppercrux.Gson;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wvs.shoppercrux.R;

public class RecyclerViewHolders extends RecyclerView.ViewHolder {

    public TextView locationName;
    public TextView locationId;

    public RecyclerViewHolders(View itemView) {
        super(itemView);
        locationName = (TextView)itemView.findViewById(R.id.locationName);
        locationId = (TextView)itemView.findViewById(R.id.locationId);
    }
}
