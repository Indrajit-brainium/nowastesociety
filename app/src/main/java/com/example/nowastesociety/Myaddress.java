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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.nowastesociety.adapter.AddressviewAdapter;
import com.example.nowastesociety.adapter.ResturantAdapter;
import com.example.nowastesociety.allurl.AllUrl;
import com.example.nowastesociety.internet.CheckConnectivity;
import com.example.nowastesociety.model.AddressviewModel;
import com.example.nowastesociety.session.SessionManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.navigation.NavigationView;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Myaddress extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "Myapp";
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ImageView imgPrf;
    ImageView navprofilePic;
    String firstname, lastname, email, imgurl, item, cid, authToken, profilepic, loginId;
    TextView navuserfirstName, navuserlastName, navuserEmail;
    LinearLayoutCompat btnHome, btnProfile, btnList, btnFav, btnCart;
    Button btnAdd;
    ActionBarDrawerToggle toggle;
    SessionManager sessionManager;
    private GoogleApiClient mGoogleApiClient;
    private static final String SHARED_PREFS = "sharedPrefs";

    ArrayList<AddressviewModel> addressviewModelArrayList;
    private AddressviewAdapter addressviewAdapter;
    private RecyclerView rvAddress;

    LinearLayout ll_address;
    RelativeLayout rl_no_address;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaddress);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgPrf = (ImageView) findViewById(R.id.imgPrf);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        sessionManager = new SessionManager(getApplicationContext());

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
                Myaddress.this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
        rvAddress = (RecyclerView)findViewById(R.id.rvAddress);
        btnHome = (LinearLayoutCompat)findViewById(R.id.btnHome);
        btnCart = (LinearLayoutCompat)findViewById(R.id.btnCart);
        btnFav = (LinearLayoutCompat)findViewById(R.id.btnFav);
        btnList = (LinearLayoutCompat)findViewById(R.id.btnList);
        btnProfile = (LinearLayoutCompat)findViewById(R.id.btnProfile);
        rl_no_address = (RelativeLayout)findViewById(R.id.rl_no_address);
        ll_address = (LinearLayout)findViewById(R.id.ll_address);

        navuserfirstName.setText(firstname);
        navuserlastName.setText(lastname);
        navuserEmail.setText(email);


        Glide.with(Myaddress.this)
                .load(profilepic)
                .circleCrop()
                .placeholder(R.drawable.dp)
                .into(navprofilePic);

        findViewById(R.id.iv_menu).setOnClickListener(view -> {
            setDrawerLocked();
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Myaddress.this, Addressadd.class);
                startActivity(intent);


            }
        });


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Myaddress.this, HomeActivity.class));
                finish();

            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Myaddress.this, Editprofile.class));
                finish();


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


                Intent intent = new Intent(Myaddress.this, Cartdetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });





        addresslist();


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


    public void addresslist() {

        //ADDRESS LIST


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();


            JSONObject params = new JSONObject();



            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, AllUrl.AddressList, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");
                    if (success) {

                        if (msg.equals("No address found.")){

                            ll_address.setVisibility(View.GONE);
                            rl_no_address.setVisibility(View.VISIBLE);

                            hideProgressDialog();

                            return;
                        }

                        Toast.makeText(Myaddress.this, msg, Toast.LENGTH_SHORT).show();

                        addressviewModelArrayList = new ArrayList<>();
                        JSONArray response_data  = result.getJSONArray("response_data");
                        for (int i = 0; i < response_data.length(); i++) {

                            AddressviewModel addressviewModel = new AddressviewModel();
                            JSONObject responseobj = response_data.getJSONObject(i);
                            addressviewModel.setTvaddressType(responseobj.getString("addressType"));
                            addressviewModel.setTvAddressdetails(responseobj.getString("areaOrColonyOrStreetOrSector"));
                            addressviewModel.setAddressId(responseobj.getString("_id"));
                            addressviewModel.setLandmark(responseobj.getString("landmark"));
                            addressviewModel.setFlatOrHouseOrBuildingOrCompany(responseobj.getString("flatOrHouseOrBuildingOrCompany"));
                            addressviewModel.setPinCode(responseobj.getString("pinCode"));
                            addressviewModel.setTownOrCity(responseobj.getString("townOrCity"));
                            addressviewModel.setUserId(responseobj.getString("userId"));

                            addressviewModelArrayList.add(addressviewModel);

                        }


                        setupRecycler();



                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(Myaddress.this, "invalid 1", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Myaddress.this, "Invalid", Toast.LENGTH_SHORT).show();

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

        addressviewAdapter = new AddressviewAdapter(this, addressviewModelArrayList);
        rvAddress.setAdapter(addressviewAdapter);
        rvAddress.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

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

                        Toast.makeText(Myaddress.this, msg, Toast.LENGTH_SHORT).show();

                        sessionManager.logoutUser();
                        LoginManager.getInstance().logOut();
                        signOut();
                        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        settings.edit().clear().apply();
                        startActivity(new Intent(Myaddress.this, Login.class));
                        finish();


                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(Myaddress.this, "Login not successfull", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Myaddress.this, "Invalid", Toast.LENGTH_SHORT).show();

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

            super.onBackPressed();
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


            Intent intent = new Intent(this, Editprofile.class);
            startActivity(intent);
            finish();


        }

        if (id == R.id.nav_fav) {
            // Handle the camera action
            startActivity(new Intent(Myaddress.this, Myfavourite.class));

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

            Intent intent = new Intent(Myaddress.this, Payment.class);
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
