package com.example.nowastesociety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.nowastesociety.allurl.AllUrl;
import com.example.nowastesociety.internet.CheckConnectivity;
import com.example.nowastesociety.session.SessionManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeEmail extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {


    private static final String TAG = "Myapp";
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ImageView imgPrf;
    ImageView navprofilePic;
    String firstname, lastname, email, imgurl, item, cid, authToken, profilepic, loginId, phone;
    private GoogleApiClient mGoogleApiClient;
    private static final String SHARED_PREFS = "sharedPrefs";
    TextView navuserfirstName, navuserlastName, navuserEmail, btnChangepassword;
    EditText etmail;
    Button btnSubmit;
    String Email;
    LinearLayoutCompat btnHome, btnProfile, btnList, btnFav, btnCart;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        imgPrf = (ImageView) findViewById(R.id.imgPrf);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnHome = (LinearLayoutCompat)findViewById(R.id.btnHome);
        btnCart = (LinearLayoutCompat)findViewById(R.id.btnCart);
        btnFav = (LinearLayoutCompat)findViewById(R.id.btnFav);
        btnList = (LinearLayoutCompat)findViewById(R.id.btnList);
        btnProfile = (LinearLayoutCompat)findViewById(R.id.btnProfile);
        etmail = (EditText)findViewById(R.id.etmail);
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
        phone = sharedPreferences.getString("phone", "");

        Log.d(TAG, "firstname--->" + firstname);

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()) //Use app context to prevent leaks using activity
                //.enableAutoManage(this /* FragmentActivity */, connectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                ChangeEmail.this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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


        navuserfirstName.setText(firstname);
        navuserlastName.setText(lastname);
        navuserEmail.setText(email);
        etmail.setText(email);

        Glide.with(ChangeEmail.this)
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

                startActivity(new Intent(ChangeEmail.this, HomeActivity.class));
                finish();
            }
        });



        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(ChangeEmail.this, Editprofile.class));
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


                Intent intent = new Intent(ChangeEmail.this, Cartdetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkblank();

            }
        });



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



    public void checkblank() {

        Email = etmail.getText().toString();

        if (Email.length()==0){

            Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show();

        }else{


            changeEmail();
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

                        Toast.makeText(ChangeEmail.this, msg, Toast.LENGTH_SHORT).show();

                        sessionManager.logoutUser();
                        LoginManager.getInstance().logOut();
                        signOut();
                        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        settings.edit().clear().apply();
                        startActivity(new Intent(ChangeEmail.this, Login.class));
                        finish();


                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(ChangeEmail.this, "Login not successfull", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ChangeEmail.this, "Invalid", Toast.LENGTH_SHORT).show();

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



    public void changeEmail(){


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();


            JSONObject params = new JSONObject();

            try {
                params.put("email", Email);
                params.put("userType", "customer");


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.ChangeEmail, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");
                    if (success) {

                        Toast.makeText(ChangeEmail.this, msg, Toast.LENGTH_SHORT).show();

                        JSONObject response_data = result.getJSONObject("response_data");
                        String email = response_data.getString("email");


                        Intent intent = new Intent(this, Resetemail.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();

                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(ChangeEmail.this, "invalid", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ChangeEmail.this, "Invalid", Toast.LENGTH_SHORT).show();

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


        if (menuItem.isChecked()) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }


        if (id == R.id.nav_profile) {
            // Handle the camera action


            Intent intent = new Intent(this, Editprofile.class);
            intent.putExtra("firstname", firstname);
            intent.putExtra("lastname", lastname);
            intent.putExtra("email", email);
            intent.putExtra("imgurl", imgurl);
            startActivity(intent);
            finish();


        }

        if (id == R.id.nav_fav) {
            // Handle the camera action
            startActivity(new Intent(ChangeEmail.this, Myfavourite.class));

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

            Intent intent = new Intent(ChangeEmail.this, Payment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }



        if (id == R.id.nav_logout) {

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
