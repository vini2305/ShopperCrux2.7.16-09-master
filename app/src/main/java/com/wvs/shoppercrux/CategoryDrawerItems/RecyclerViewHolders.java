package com.wvs.shoppercrux.CategoryDrawerItems;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wvs.shoppercrux.R;

public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView categoryName;
    public TextView categoryId;

    public RecyclerViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        categoryName = (TextView)itemView.findViewById(R.id.categoryName);
        categoryId = (TextView)itemView.findViewById(R.id.categoryId);
    }

    @Override
    public void onClick(View view) {

    }
}
