package com.wvs.shoppercrux.StoreProfile;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.Stores.ServerImageParseAdapter;

import java.util.List;
import java.util.Locale;

/**
 * Created by root on 23/8/16.
 */
public class StoreProfileRecycle extends RecyclerView.Adapter<StoreProfileRecycle.ViewHolder> {

    Activity activity;
    Context context;
    List<StoreProfileAdapter> storeProfile;
    ImageLoader imageLoader;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    public StoreProfileRecycle(List<StoreProfileAdapter> storeProfile, Activity activity) {
        super();
        this.storeProfile = storeProfile;
        this.activity = activity;
    }

    @Override
    public StoreProfileRecycle.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_profile_recycle, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(StoreProfileRecycle.ViewHolder holder, int position) {
        final StoreProfileAdapter storePro = storeProfile.get(position);
        imageLoader = ServerImageParseAdapter.getInstance(context).getImageLoader();

        imageLoader.get(storePro.getImageServerUrl(),
                ImageLoader.getImageListener(
                        holder.storeBanner,//Server Image
                        R.mipmap.ic_launcher,//Before loading server image the default showing image.
                        android.R.drawable.ic_dialog_alert //Error image if requested image dose not found on server.
                )
        );
        holder.storeBanner.setImageUrl(storePro.getImageServerUrl(), imageLoader);
        holder.storeName.setText(storePro.getStoreName());
        holder.storeDescription.setText(stripHtml(storePro.getStoreDescription()));
        holder.storeAddress.setText(storePro.getStoreAddress());
        holder.storeContact.setText(storePro.getStoreContact());

        final double latitude = Double.parseDouble(storePro.getLatitude());
        final double longitude = Double.parseDouble(storePro.getLongitude());

        holder.call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

               if (ActivityCompat.checkSelfPermission(activity, permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.CALL_PHONE)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        ActivityCompat.requestPermissions((Activity) context.getApplicationContext(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.CALL_PHONE},
                                2);


                    }
             } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+storePro.getStoreContact()));
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(callIntent);
                }

            }
        });

        holder.map.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH,"geo:"+latitude+","+longitude+"?q="+latitude+","+longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storeProfile.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView storeName,storeAddress,storeContact,storeDescription;
        private NetworkImageView storeBanner;
        private ImageButton call,map;
        public ViewHolder(View itemView) {
            super(itemView);
            storeName = (TextView) itemView.findViewById(R.id.store_name);
            storeAddress = (TextView) itemView.findViewById(R.id.seller_address);
            storeContact = (TextView) itemView.findViewById(R.id.seller_contact);
            storeDescription = (TextView) itemView.findViewById(R.id.seller_desc);
            storeBanner = (NetworkImageView) itemView.findViewById(R.id.store_banner);
            call = (ImageButton) itemView.findViewById(R.id.call);
            map = (ImageButton) itemView.findViewById(R.id.map);
        }
    }

    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }
}
