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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.nowastesociety.adapter.CardviewAdapter;
import com.example.nowastesociety.adapter.CartitemAdapter;
import com.example.nowastesociety.allurl.AllUrl;
import com.example.nowastesociety.internet.CheckConnectivity;
import com.example.nowastesociety.model.CardModel;
import com.example.nowastesociety.model.CartitemModel;
import com.example.nowastesociety.model.ResturantDetailsModel;
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

public class Cartdetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Myapp";
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ImageView navprofilePic;
    TextView navuserfirstName, navuserlastName, navuserEmail, tvSubtotal, tvTotalamount ;
    LinearLayoutCompat btnHome, btnProfile, btnList, btnFav, btnCart;
    String firstname, lastname, email, imgurl, cid, authToken, profilepic, loginId, vendorId;
    String cartid;
    SessionManager sessionManager;
    ActionBarDrawerToggle toggle;
    private static final String SHARED_PREFS = "sharedPrefs";
    private GoogleApiClient mGoogleApiClient;

    ArrayList<CartitemModel> cartitemModelArrayList = new ArrayList<>();
    private CartitemAdapter cartitemAdapter;
    private RecyclerView rvCartitems;
    LinearLayout ll_cart;
    RelativeLayout rl_empty_cart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartdetails);
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

        Log.d(TAG, "authToken-->" + authToken);


        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()) //Use app context to prevent leaks using activity
                //.enableAutoManage(this /* FragmentActivity */, connectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        setSupportActionBar(toolbar);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                Cartdetails.this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorHighlight));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navuserfirstName = header.findViewById(R.id.navuserfirstName);
        navuserlastName = header.findViewById(R.id.navuserlastName);
        navuserEmail = header.findViewById(R.id.navuserEmail);
        navprofilePic = header.findViewById(R.id.navprofilePic);

        btnHome = (LinearLayoutCompat) findViewById(R.id.btnHome);
        btnCart = (LinearLayoutCompat) findViewById(R.id.btnCart);
        btnFav = (LinearLayoutCompat) findViewById(R.id.btnFav);
        btnList = (LinearLayoutCompat) findViewById(R.id.btnList);
        btnProfile = (LinearLayoutCompat) findViewById(R.id.btnProfile);
        rvCartitems = (RecyclerView) findViewById(R.id.rvCartitems);
        ll_cart = (LinearLayout)findViewById(R.id.ll_cart);
        rl_empty_cart = (RelativeLayout)findViewById(R.id.rl_empty_cart);
        tvSubtotal = (TextView)findViewById(R.id.tvSubtotal);
        tvTotalamount = (TextView)findViewById(R.id.tvTotalamount);


        navigationView.setNavigationItemSelectedListener(this);

        navuserfirstName.setText(firstname);
        navuserlastName.setText(lastname);
        navuserEmail.setText(email);


        Glide.with(Cartdetails.this)
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

                startActivity(new Intent(Cartdetails.this, HomeActivity.class));
                finish();

            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Cartdetails.this, Editprofile.class);
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


            }
        });

        setupRecycler();
        cartitemList();

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


    public void cartitemList() {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();


            JSONObject params = new JSONObject();


            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, AllUrl.FetchCart, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");
                    if (success) {


//                        Toast.makeText(Cartdetails.this, msg, Toast.LENGTH_SHORT).show();
                        cartitemModelArrayList.clear();
                        JSONObject response_data = result.getJSONObject("response_data");
                        if(!response_data.has("cartTotal")){
                            cartitemAdapter.notifyDataSetChanged();
                            //show and hide
                            hideProgressDialog();
                            ll_cart.setVisibility(View.GONE);
                            rl_empty_cart.setVisibility(View.VISIBLE);
                            return;
                        }
                        String cartTotal = response_data.getString("cartTotal");
                        cartid = response_data.getString("_id");
                        String userId = response_data.getString("userId");
                        JSONObject vendorId = response_data.getJSONObject("vendorId");
                        String vid = vendorId.getString("restaurantName");
                        tvSubtotal.setText(cartTotal);
                        tvTotalamount.setText(cartTotal);
                        JSONArray itemArray = response_data.getJSONArray("item");
                        for (int i = 0; i < itemArray.length(); i++) {
                            JSONObject itemobj = itemArray.getJSONObject(i);
                            JSONObject subobj = itemobj.getJSONObject("itemId");
                            CartitemModel cartitemModel = new CartitemModel();

                            cartitemModel.setItemId(itemobj.getString("_id"));

                            cartitemModel.setItemname(subobj.getString("itemName"));
                            cartitemModel.setItemImg(subobj.getString("menuImage"));
//                            if (itemobj.has("description"))
//                                mCartitemModel.setDescription(itemobj.getString("description"));
//                            else
//                                mCartitemModel.setDescription(itemobj.getString(""));
                            cartitemModel.setItemprice(itemobj.getString("itemAmount"));
                            cartitemModel.setQuantity(itemobj.getInt("itemQuantity"));

                            cartitemModelArrayList.add(cartitemModel);
                        }

                        cartitemAdapter.notifyDataSetChanged();



                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(Cartdetails.this, "invalid", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Cartdetails.this, "Invalid", Toast.LENGTH_SHORT).show();

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
        rvCartitems.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        cartitemAdapter = new CartitemAdapter(this, cartitemModelArrayList);
        rvCartitems.setAdapter(cartitemAdapter);

    }

    public void removeToCart(CartitemModel cartitemModel) {


        new FancyGifDialog.Builder(this)
                .setTitle("Do you want to delete this item?")
                .setMessage("Food item will be remove from your cart!")
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground("#FF4081")
                .setPositiveBtnText("Delete")
                .setNegativeBtnBackground("#FFA9A7A8")
                .setGifResource(R.drawable.deletecart)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {


                        deleteItem(cartitemModel);


                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Toast.makeText(Cartdetails.this, "Cancel", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();


    }

    public void updateCart(CartitemModel cartitemModel){
//        Log.i("Response-->", "cart quantity updating");



        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();

            JSONObject params = new JSONObject();

            try {
                params.put("cartId", cartid);
                params.put("itemId", cartitemModel.getItemId());
                params.put("itemQuantity", cartitemModel.getQuantity());



            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.UpdateCart, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    boolean success = result.getBoolean("success");
                    if (success) {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                        cartitemList();

                    } else {

                        Toast.makeText(this, "invalid 1", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Cartdetails.this, "Invalid", Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", authToken);
                    return params;
                }
            };

            Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);

        } else {

            Toast.makeText(this, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show();

        }




    }


    public void deleteItem(CartitemModel cartitemModel) {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();

            JSONObject params = new JSONObject();

            try {
                params.put("cartId", cartid);
                params.put("itemId", cartitemModel.getItemId());


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.RemoveToCart, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    boolean success = result.getBoolean("success");
                    if (success) {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                        cartitemList();

                    } else {

                        Toast.makeText(this, "invalid 1", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Cartdetails.this, "Invalid", Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", authToken);
                    return params;
                }
            };

            Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);

        } else {

            Toast.makeText(this, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show();

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

                        Toast.makeText(Cartdetails.this, msg, Toast.LENGTH_SHORT).show();

                        sessionManager.logoutUser();
                        LoginManager.getInstance().logOut();
                        signOut();
                        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        settings.edit().clear().apply();
                        startActivity(new Intent(Cartdetails.this, Login.class));
                        finish();


                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(Cartdetails.this, "Login not successfull", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Cartdetails.this, "Invalid", Toast.LENGTH_SHORT).show();

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


            Intent intent = new Intent(Cartdetails.this, Editprofile.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        }

        if (id == R.id.nav_fav) {
            // Handle the camera action
            startActivity(new Intent(Cartdetails.this, Myfavourite.class));

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
            Intent intent = new Intent(Cartdetails.this, Myaddress.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
//
//        if (id == R.id.nav_issue) {
//            // Handle the camera action
//            startActivity(new Intent(MainActivity.this, SubjectDetails.class));
//
//        }
        if (id == R.id.nav_payment) {
            // Handle the camera action

            Intent intent = new Intent(Cartdetails.this, Payment.class);
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
