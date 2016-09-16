package com.wvs.shoppercrux.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.wvs.shoppercrux.Gson.RecyclerViewAdapter;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.Utils.PermissionUtils;
import com.wvs.shoppercrux.activities.LocationActivity;
import com.wvs.shoppercrux.activities.LoginActivity;
import com.wvs.shoppercrux.activities.MainActivity;
import com.wvs.shoppercrux.helper.SQLiteHandler;
import com.wvs.shoppercrux.helper.SessionManager;

public class HomeFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public double latitude, longitude;
    private LocationManager locationManager;
    private SQLiteHandler db;
    private SessionManager session;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private Location mLocation;
    private String provider;
//    private FloatingActionButton floatingActionButton;
//    private Boolean isFabOpen = false;
//    private FloatingActionButton fab,fab1,fab2;
//    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private FloatingActionMenu menu;
    private View view;
    private FloatingActionButton setLocation,listView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =inflater.inflate(R.layout.content_home, container, false);
        // SqLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        // session manager
        session = new SessionManager(getActivity().getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        enableMyLocation();

        setLocation = (FloatingActionButton) view.findViewById(R.id.locationBtn);
        listView = (FloatingActionButton) view.findViewById(R.id.listBtn);
        menu = (FloatingActionMenu) view.findViewById(R.id.menu);

        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager2 = getFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                LocationActivity fragment2 = new LocationActivity();
//                fragmentTransaction2.addToBackStack("xyz");
                fragmentTransaction2.hide(HomeFragment.this);
                fragmentTransaction2.add(R.id.content_frame, fragment2);
                fragmentTransaction2.commit();

            }
        });
        listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager2 = getFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                StoreFragment fragment2 = new StoreFragment();
                Bundle bundle = new Bundle();
                String data = RecyclerViewAdapter.locationData;
                bundle.putString("location_id", data);
                fragment2.setArguments(bundle);
//                fragmentTransaction2.addToBackStack("xyz");
                fragmentTransaction2.hide(HomeFragment.this);
                fragmentTransaction2.add(R.id.content_frame, fragment2);
                fragmentTransaction2.commit();
            }
        });


//        fab = (FloatingActionButton)view.findViewById(R.id.fab);
//
//        fab1 = (FloatingActionButton)view.findViewById(R.id.fab1);
//        fab2 = (FloatingActionButton)view.findViewById(R.id.fab2);
//        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
//        fab_close = AnimationUtils.loadAnimation(getContext(),R.anim.fab_close);
//        rotate_forward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_forward);
//        rotate_backward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_backward);
//        fab.setOnClickListener(this);
//        fab1.setOnClickListener(this);
//        fab2.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;

    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        enableMyLocation();
        mMap.setOnMyLocationButtonClickListener(this);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,
                                           int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getActivity().getSupportFragmentManager(), "dialog");
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {

        MainActivity activity = (MainActivity) getActivity();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(activity, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);

            if(provider!=null && !provider.equals("")){

                // Get the location from the given provider
                Location location = locationManager.getLastKnownLocation(provider);

                locationManager.requestLocationUpdates(provider, 20000, 1, this);

                if(location!=null) {
                    onLocationChanged(mLocation);
                    mMap.getUiSettings().setZoomControlsEnabled(false);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Your Location"));

                }
                else {
                    Toast.makeText(getActivity(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        switch (id){
//            case R.id.fab:
//
//                animateFAB();
//                break;
//            case R.id.fab1:
//              startActivity(new Intent(getContext(), LocationActivity.class));
//                break;
//            case R.id.fab2:
//
//                break;
//        }
//    }
//    public void animateFAB(){
//
//        if(isFabOpen){
//
//            fab.startAnimation(rotate_backward);
//            fab1.startAnimation(fab_close);
//            fab2.startAnimation(fab_close);
//            fab1.setClickable(false);
//            fab2.setClickable(false);
//            isFabOpen = false;
//            Log.d("ShopperCrux", "close");
//
//        } else {
//
//            fab.startAnimation(rotate_forward);
//            fab1.startAnimation(fab_open);
//            fab2.startAnimation(fab_open);
//            fab1.setClickable(true);
//            fab2.setClickable(true);
//            isFabOpen = true;
//            Log.d("ShopperCrux","open");
//
//        }
//    }
}







