package com.wvs.shoppercrux.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.classes.CatDrawerItem;
import com.wvs.shoppercrux.classes.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by root on 25/7/16.
 */
public class CategoryDrawerAdapter extends RecyclerView.Adapter<CategoryDrawerAdapter.MyViewHolder> {

    List<CatDrawerItem> data = Collections.emptyList();

    ArrayList<Category> list= new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    public CategoryDrawerAdapter(Context context, ArrayList<Category> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    public void delete(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cat_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
