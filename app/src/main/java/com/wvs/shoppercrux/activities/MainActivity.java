package com.wvs.shoppercrux.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.LayerDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.Stores.GetDataAdapter;
import com.wvs.shoppercrux.Stores.RecyclerViewAdapter;
import com.wvs.shoppercrux.activities.FragmentDrawer.FragmentDrawerListener;
import com.wvs.shoppercrux.fragments.HomeFragment;
import com.wvs.shoppercrux.fragments.NavigationDrawerFragment;
import com.wvs.shoppercrux.helper.SQLiteHandler;
import com.wvs.shoppercrux.helper.SessionManager;

import java.util.HashMap;
import java.util.List;

import static com.wvs.shoppercrux.R.id;

public class MainActivity extends AppCompatActivity implements FragmentDrawerListener {

    public static final String MY_PREFS_NAME = "CartCount";
    List<GetDataAdapter> GetDataAdapter1;
    RecyclerViewAdapter recyclerViewadapter;
    private Toolbar mToolbar;
    private SQLiteHandler db;
    private SessionManager session;
    private FragmentDrawer drawerFragment;
    private CategoryDrawer categoryDrawerFragment;
    private MenuItem mCart,mSearchItem;
    private SearchView searchView;
    private SearchManager searchManager;
    private LayerDrawable icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   Intent intent = getIntent();
        String id = intent.getStringExtra("fbEmail");
        String name = intent.getStringExtra("fbName");
//        Log.d("emaiId",id);
//        Log.d("Name",name);

        Log.w("Shoppercrux", "onCreate() method excecuted");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(mToolbar);

        // getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer1);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        // categoryDrawerFragment = (CategoryDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_category_drawer);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        isGpsEnabled(getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        HashMap<String, String> user1 = db.getGoogleUserDetails();

        // display the first navigation drawer view on app launch
        displayView(0);

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("deprecation")
    public static boolean isGpsEnabled(Context context) {

        if (PackageUtil.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String providers = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (TextUtils.isEmpty(providers)) {
                return false;
            }
            return providers.contains(LocationManager.GPS_PROVIDER);
        } else {
            final int locationMode;
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(),
                        Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            switch (locationMode) {

                case Settings.Secure.LOCATION_MODE_HIGH_ACCURACY:
                case Settings.Secure.LOCATION_MODE_SENSORS_ONLY:
                    return true;
                case Settings.Secure.LOCATION_MODE_BATTERY_SAVING:
                case Settings.Secure.LOCATION_MODE_OFF:
                default:
                    return false;
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(id.cart).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        dialog.cancel();
//                    }
//                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);
        LoginManager.getInstance().logOut();
        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
              case id.mycart:
                Intent intent = new Intent(MainActivity.this,CartActivity.class);
                startActivity(intent);
                return true;

            case id.action_logout:
                logoutUser();
                return true;

            case id.action_wishlist:
                Intent intent1=new Intent(MainActivity.this,WishListActivity.class);
                startActivity(intent1);
                return true;

            case id.action_reset:
                Intent intent2=new Intent(MainActivity.this,PasswordReset.class);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mCart = menu.findItem(R.id.cart);
        icon = (LayerDrawable) mCart.getIcon();

        SharedPreferences preferences = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String sharedText = preferences.getString("TotalCart",null);
        if(sharedText != null) {
            Product.setBadgeCount(this,icon,sharedText);
        } else {
            Product.setBadgeCount(this,icon,"0");
        }
       return true;
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        //  String title = getString(string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                //    title = getString(R.string.nav_item_home);
                break;
//            case 1:
//               fragment = new ProfileFragment();
//                title = "ProfileActivity";
//                break;
            case 1:
                logoutUser();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(id.content_frame, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            //getSupportActionBar().setTitle(title);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //buildAlertMessageNoGps();
        }
    }

    public static class PackageUtil {

        static boolean checkPermission(Context context, String accessFineLocation) {

            int res = context.checkCallingOrSelfPermission(accessFineLocation);
            return (res == PackageManager.PERMISSION_GRANTED);

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
























