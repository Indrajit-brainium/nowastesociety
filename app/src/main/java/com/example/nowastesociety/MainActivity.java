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
import com.example.nowastesociety.adapter.ResturantDetailsAdapter;
import com.example.nowastesociety.allurl.AllUrl;
import com.example.nowastesociety.internet.CheckConnectivity;
import com.example.nowastesociety.model.ResturantDetailsModel;
import com.example.nowastesociety.model.ResturentModel;
import com.example.nowastesociety.session.SessionManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Myapp";
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ImageView navprofilePic;
    TextView navuserfirstName, navuserlastName, navuserEmail;
    LinearLayoutCompat btnHome, btnProfile, btnList, btnFav, btnCart;
    TextView tvName, tvDescription, tvDistance, tvTiming;
    ImageView imgLogo, imgBanner;
    String firstname, lastname, email, imgurl, cid, authToken, profilepic, loginId, vendorId;
    SessionManager sessionManager;
    ActionBarDrawerToggle toggle;
    private static final String SHARED_PREFS = "sharedPrefs";
    Context context;
    private GoogleApiClient mGoogleApiClient;

    ArrayList<ResturantDetailsModel> resturantDetailsModelArrayList;
    private ResturantDetailsAdapter resturantDetailsAdapter;
    private RecyclerView rv_Resturantdetails;
    String description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        Log.d(TAG, "firstname-->" + firstname);


        Intent intent = getIntent();
        vendorId = intent.getStringExtra("vendorId");

        Log.d(TAG, "vendorId-->" + vendorId);


        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()) //Use app context to prevent leaks using activity
                //.enableAutoManage(this /* FragmentActivity */, connectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorHighlight));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navuserfirstName = header.findViewById(R.id.navuserfirstName);
        navuserlastName = header.findViewById(R.id.navuserlastName);
        navuserEmail = header.findViewById(R.id.navuserEmail);
        navprofilePic = header.findViewById(R.id.navprofilePic);
        rv_Resturantdetails = (RecyclerView) findViewById(R.id.rv_Resturantdetails);
        tvName = (TextView) findViewById(R.id.tvName);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvTiming = (TextView) findViewById(R.id.tvTiming);
        imgBanner = (ImageView) findViewById(R.id.imgBanner);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        btnHome = (LinearLayoutCompat) findViewById(R.id.btnHome);
        btnCart = (LinearLayoutCompat) findViewById(R.id.btnCart);
        btnFav = (LinearLayoutCompat) findViewById(R.id.btnFav);
        btnList = (LinearLayoutCompat) findViewById(R.id.btnList);
        btnProfile = (LinearLayoutCompat) findViewById(R.id.btnProfile);


        navigationView.setNavigationItemSelectedListener(this);

        navuserfirstName.setText(firstname);
        navuserlastName.setText(lastname);
        navuserEmail.setText(email);

        Glide.with(MainActivity.this)
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

                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();

            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Editprofile.class);
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


                Intent intent = new Intent(MainActivity.this, Cartdetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });


        resturantDetails();


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


    public void resturantDetails() {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();


            JSONObject params = new JSONObject();

            try {
                params.put("userType", "customer");
                params.put("customerId", cid);
                params.put("vendorId", vendorId);
                params.put("restaurantInfo", "YES");
                params.put("categoryId", "");
                params.put("latitude", "22.599331");
                params.put("longitude", "88.444899");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.ResturantDetails, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {

                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");
                    if (success) {

                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                        resturantDetailsModelArrayList = new ArrayList<>();
                        JSONObject response_data = result.getJSONObject("response_data");
                        JSONObject restaurant = response_data.getJSONObject("restaurant");
                        String restaurantName = restaurant.getString("name");
                        if (restaurant.has("description"))
                            description = restaurant.getString("description");
                        else {
                            description = "No Description";
                        }
                        String rating = restaurant.getString("rating");
                        String logo = restaurant.getString("logo");
                        String banner = restaurant.getString("banner");
                        String distance = restaurant.getString("distance");
                        tvName.setText(restaurantName);
                        tvDescription.setText(description);
                        tvDistance.setText(" (" + distance + ")");


                        JSONObject catitem = response_data.getJSONObject("catitem");
                        JSONArray itemArray = catitem.getJSONArray("item");
                        for (int i = 0; i < itemArray.length(); i++) {

                            ResturantDetailsModel resturantDetailsModel = new ResturantDetailsModel();
                            JSONObject itemobj = itemArray.getJSONObject(i);
                            resturantDetailsModel.setItemId(itemobj.getString("itemId"));
//                            resturantDetailsModel.setCategoryId(itemobj.getString("categoryId"));
                            resturantDetailsModel.setItemName(itemobj.getString("itemName"));
                            resturantDetailsModel.setType(itemobj.getString("type"));
                            resturantDetailsModel.setPrice(itemobj.getString("price"));
                            resturantDetailsModel.setDescription(itemobj.getString("description"));
                            resturantDetailsModel.setMenuImage(itemobj.getString("menuImage"));

                            resturantDetailsModelArrayList.add(resturantDetailsModel);


                        }

                        setupRecycler();


                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(MainActivity.this, "Error Loading", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Invalid", Toast.LENGTH_SHORT).show();

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

        resturantDetailsAdapter = new ResturantDetailsAdapter(this, resturantDetailsModelArrayList);
        rv_Resturantdetails.setAdapter(resturantDetailsAdapter);
        rv_Resturantdetails.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
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

                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                        sessionManager.logoutUser();
                        LoginManager.getInstance().logOut();
                        signOut();
                        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        settings.edit().clear().apply();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finish();


                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(MainActivity.this, "Login not successfull", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Invalid", Toast.LENGTH_SHORT).show();

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


    JSONArray itemarry = new JSONArray();

    public void addToCart(ResturantDetailsModel resturantDetailsModel) {
        if(resturantDetailsModel==null){
            itemarry = new JSONArray();
            return;
        }

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            try {
                for (int i = 0; i < itemarry.length(); i++) {
                    JSONObject item = itemarry.getJSONObject(i);
                    if (item.getString("itemId").equals(resturantDetailsModel.getItemId())) {
                        if (item.getInt("quantity") == (resturantDetailsModel.getQuantity())) {
                            Toast.makeText(getApplicationContext(), "Already added", Toast.LENGTH_SHORT).show();
                        } else {
                            item.put("quantity", resturantDetailsModel.getQuantity());
                            Log.v("cart status", "cart updated");
                        }
                        return;
                    }
                }

                JSONObject itemobject = new JSONObject();
                itemobject.put("name", resturantDetailsModel.getItemName());
                itemobject.put("quantity", resturantDetailsModel.getQuantity());
                itemobject.put("price", resturantDetailsModel.getPrice());
                itemobject.put("itemId", resturantDetailsModel.getItemId());
                itemarry.put(itemobject);
                Log.v("Items arry", itemarry.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            showProgressDialog();


            JSONObject params = new JSONObject();

            try {
                params.put("itemId", resturantDetailsModel.getItemId());
                params.put("itemAmount", resturantDetailsModel.getPrice());
                params.put("itemQuantity", resturantDetailsModel.getQuantity());
                params.put("vendorId", vendorId);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.AddToCart, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    String STATUSCODE = result.getString("STATUSCODE");
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");
                    if (success) {

                        new FancyGifDialog.Builder(this)
                                .setTitle("Successfully!")
                                .setMessage(msg)
                                .setPositiveBtnText("Ok")
                                .setPositiveBtnBackground("#FF4081")
                                .setGifResource(R.drawable.foodanim)   //Pass your Gif here
                                .isCancellable(true)
                                .OnPositiveClicked(new FancyGifDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .build();


                    } else if (STATUSCODE.equals("422")) {

                        new FancyGifDialog.Builder(this)
                                .setTitle("Attention!")
                                .setMessage(msg)
                                .setNegativeBtnText("Cancel")
                                .setPositiveBtnBackground("#FF4081")
                                .setPositiveBtnText("Ok")
                                .setNegativeBtnBackground("#FFA9A7A8")
                                .isCancellable(false)
                                .OnPositiveClicked(new FancyGifDialogListener() {
                                    @Override
                                    public void OnClick() {
//                                        Toast.makeText(MainActivity.this,"Ok",Toast.LENGTH_SHORT).show();

                                        removePreviousWholeCart();
                                        addToCart(null);


                                    }
                                })
                                .OnNegativeClicked(new FancyGifDialogListener() {
                                    @Override
                                    public void OnClick() {

                                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .build();


                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(MainActivity.this, "Add to Cart not successfull", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Invalid", Toast.LENGTH_SHORT).show();

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


    public void removePreviousWholeCart() {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();


            JSONObject params = new JSONObject();

            try {
                params.put("allowMultipleRestaurant", true);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.RemovePreviousWholeCart, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {

                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");

                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Invalid", Toast.LENGTH_SHORT).show();

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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


        int id = menuItem.getItemId();

        //to prevent current item select over and over
        if (menuItem.isChecked()) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }


        if (id == R.id.nav_profile) {
            // Handle the camera action


            Intent intent = new Intent(MainActivity.this, Editprofile.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        }

        if (id == R.id.nav_fav) {
            // Handle the camera action
            startActivity(new Intent(MainActivity.this, Myfavourite.class));

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

            Intent intent = new Intent(MainActivity.this, Payment.class);
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
