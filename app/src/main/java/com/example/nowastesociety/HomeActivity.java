package com.example.nowastesociety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.nowastesociety.adapter.ResturantAdapter;
import com.example.nowastesociety.adapter.SlidingImage_Adapter;
import com.example.nowastesociety.allurl.AllUrl;
import com.example.nowastesociety.internet.CheckConnectivity;
import com.example.nowastesociety.model.ImageModel;
import com.example.nowastesociety.model.ResturentModel;
import com.example.nowastesociety.session.SessionManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.navigation.NavigationView;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Myapp";
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    TextView navuserfirstName, navuserlastName, navuserEmail, btnLoadmore;
    ImageView navprofilePic;
    String firstname, lastname, email, imgurl, cid, authToken, profilepic, loginId;
    LinearLayoutCompat btnHome, btnProfile, btnList, btnFav, btnCart;
    SessionManager sessionManager;
    ActionBarDrawerToggle toggle;
    private static final String SHARED_PREFS = "sharedPrefs";
    Context context;
    private GoogleApiClient mGoogleApiClient;

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageModelArrayList;
    private ArrayList<ResturentModel> resturentModelArrayList;
    private ResturantAdapter resturantAdapter;
    private RecyclerView rv_Resturantlist;


    private int[] myImageList = new int[]{R.drawable.res1, R.drawable.res2,
            R.drawable.res3, R.drawable.res4
            , R.drawable.res1, R.drawable.res2};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        sessionManager = new SessionManager(getApplicationContext());

        imageModelArrayList = new ArrayList<>();
        imageModelArrayList = populateList();

        init();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        firstname = sharedPreferences.getString("firstName", "");
        lastname = sharedPreferences.getString("lastName", "");
        email = sharedPreferences.getString("email", "");
        imgurl = sharedPreferences.getString("imgurl", "");
        cid = sharedPreferences.getString("id", "");
        authToken = sharedPreferences.getString("authToken", "");
        profilepic = sharedPreferences.getString("profilepic", "");
        loginId = sharedPreferences.getString("loginId", "");


        Log.d(TAG, "authToken-->" + authToken);


        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()) //Use app context to prevent leaks using activity
                //.enableAutoManage(this /* FragmentActivity */, connectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        toggle = new ActionBarDrawerToggle(
                HomeActivity.this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorHighlight));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navuserfirstName = header.findViewById(R.id.navuserfirstName);
        navuserlastName = header.findViewById(R.id.navuserlastName);
        navuserEmail = header.findViewById(R.id.navuserEmail);
        navprofilePic = header.findViewById(R.id.navprofilePic);
        navigationView.setNavigationItemSelectedListener(this);
        rv_Resturantlist = (RecyclerView) findViewById(R.id.rv_Resturantlist);
        btnHome = (LinearLayoutCompat) findViewById(R.id.btnHome);
        btnCart = (LinearLayoutCompat) findViewById(R.id.btnCart);
        btnFav = (LinearLayoutCompat) findViewById(R.id.btnFav);
        btnList = (LinearLayoutCompat) findViewById(R.id.btnList);
        btnProfile = (LinearLayoutCompat) findViewById(R.id.btnProfile);
        btnLoadmore = (TextView) findViewById(R.id.btnLoadmore);

        navuserfirstName.setText(firstname);
        navuserlastName.setText(lastname);
        navuserEmail.setText(email);

        Glide.with(HomeActivity.this)
                .load(profilepic)
                .circleCrop()
                .placeholder(R.drawable.dp)
                .into(navprofilePic);


        findViewById(R.id.iv_menu).setOnClickListener(view -> {
            setDrawerLocked();
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, Editprofile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, Cartdetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });


        btnLoadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, ResturantList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });


        resturantList();


    }


    private ArrayList<ImageModel> populateList() {

        ArrayList<ImageModel> list = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            ImageModel imageModel = new ImageModel();
            imageModel.setimageUrl(myImageList[i]);
            list.add(imageModel);
        }

        return list;
    }


    private void init() {

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(HomeActivity.this, imageModelArrayList));


        NUM_PAGES = imageModelArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);


    }


    public void setDrawerLocked() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    private void signOut() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }


    public void resturantList() {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();


            JSONObject params = new JSONObject();

            try {
                params.put("customerId", cid);
                params.put("latitude", "22.599331");
                params.put("userType", "customer");
                params.put("longitude", "88.444899");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.DashboardResturants, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {

                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");
                    if (success) {

//                        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();

                        resturentModelArrayList = new ArrayList<>();
                        JSONObject response_data = result.getJSONObject("response_data");
                        JSONArray vendorArray = response_data.getJSONArray("vendor");
                        for (int i = 0; i < vendorArray.length(); i++) {

                            ResturentModel resturentModel = new ResturentModel();
                            JSONObject vendorobj = vendorArray.getJSONObject(i);
                            resturentModel.setName(vendorobj.getString("name"));
                            resturentModel.setId(vendorobj.getString("id"));
                            if (vendorobj.has("description"))
                                resturentModel.setDescription(vendorobj.getString("description"));
                            else {
                                resturentModel.setDescription("");
                            }
                            resturentModel.setLogo(vendorobj.getString("logo"));
                            resturentModel.setDistance(vendorobj.getString("distance"));
                            resturentModel.setRating(vendorobj.getString("rating"));

                            resturentModelArrayList.add(resturentModel);
                        }

                        if (vendorArray.length() > 3) {

                            btnLoadmore.setVisibility(View.VISIBLE);
                        } else {

                            btnLoadmore.setVisibility(View.GONE);
                        }


                        setupRecycler();


                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(HomeActivity.this, "Error Loading", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(HomeActivity.this, "Invalid", Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", authToken);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(jsonRequest);

        } else {

            Toast.makeText(getApplicationContext(), "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show();

        }


    }


    private void setupRecycler() {

        resturantAdapter = new ResturantAdapter(this, resturentModelArrayList);
        rv_Resturantlist.setAdapter(resturantAdapter);
        rv_Resturantlist.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

    }


    public void addTofavourite(ResturentModel resturentModel) {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();


            JSONObject params = new JSONObject();

            try {

                params.put("vendorId", resturentModel.getId());


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.AddtoFav, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    String STATUSCODE = result.getString("STATUSCODE");
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");
                    if (success) {


                        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();


                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(HomeActivity.this, "Add to Favourite not successfull", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(HomeActivity.this, "Invalid", Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", authToken);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(jsonRequest);

        } else {

            Toast.makeText(getApplicationContext(), "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show();

        }


    }





    public void addTounfavourite(ResturentModel resturentModel){


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();


            JSONObject params = new JSONObject();

            try {

                params.put("vendorId", resturentModel.getId());


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.unFav, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    String STATUSCODE = result.getString("STATUSCODE");
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");
                    if (success) {


                        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();


                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(HomeActivity.this, "Add to Favourite not successfull", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(HomeActivity.this, "Invalid", Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", authToken);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(jsonRequest);

        } else {

            Toast.makeText(getApplicationContext(), "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show();

        }

    }









    public void logout() {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();


            JSONObject params = new JSONObject();

            try {
                params.put("loginId", loginId);
                params.put("customerId", cid);
                params.put("userType", "customer");


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.Logout, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");
                    if (success) {

                        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();

                        sessionManager.logoutUser();
                        LoginManager.getInstance().logOut();
                        signOut();
                        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        settings.edit().clear().apply();
                        startActivity(new Intent(HomeActivity.this, Login.class));
                        finish();


                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(HomeActivity.this, "Login not successfull", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(HomeActivity.this, "Invalid", Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", authToken);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(jsonRequest);

        } else {

            Toast.makeText(getApplicationContext(), "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show();

        }


    }


    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
// TODO Auto-generated method stub
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // builder.setCancelable(false);
            builder.setMessage("Do you want to Exit?");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    finishAffinity();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                    dialog.cancel();

                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


        int id = menuItem.getItemId();

        //to prevent current item select over and over
        if (menuItem.isChecked()) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }


        if (id == R.id.nav_profile) {
            // Handle the camera action


            Intent intent = new Intent(HomeActivity.this, Editprofile.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        }

        if (id == R.id.nav_fav) {
            // Handle the camera action
            startActivity(new Intent(HomeActivity.this, Myfavourite.class));

        }
//
//        if (id == R.id.nav_orders) {
//            // Handle the camera action
//            startActivity(new Intent(MainActivity.this, QuizActivity.class));
//
//        }
//
        if (id == R.id.nav_address) {
            // Handle the camera action
            startActivity(new Intent(this, Myaddress.class));

        }
//
//        if (id == R.id.nav_issue) {
//            // Handle the camera action
//            startActivity(new Intent(MainActivity.this, SubjectDetails.class));
//
//        }


        if (id == R.id.nav_payment) {
            // Handle the camera action

            Intent intent = new Intent(HomeActivity.this, Payment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }


        if (id == R.id.nav_logout) {
            // Handle the camera action

            // TODO Auto-generated method stub
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // builder.setCancelable(false);
            builder.setMessage("Logout. Continue?");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    logout();

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                    dialog.cancel();

                }
            });

            AlertDialog alert = builder.create();
            alert.show();


        }


        return false;
    }
}
