package com.example.nowastesociety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;


import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.nowastesociety.allurl.AllUrl;
import com.example.nowastesociety.internet.CheckConnectivity;
import com.example.nowastesociety.retrofit.ApiClient;
import com.example.nowastesociety.retrofit.ApiInterface;
import com.example.nowastesociety.session.SessionManager;
import com.example.nowastesociety.utils.GetRealPathFromUri;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Editprofile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Myapp";
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ImageView imgPrf;
    ImageView navprofilePic;
    EditText etFirstname, etLastname, etPhone, etEmail;
    ImageView btneditMail;
    Spinner spGender;
    String firstname, lastname, email, imgurl, item, cid, authToken, profilepic, loginId, phone;
    String firstName, lastName, Email, phoneNumber, selectGender;
    TextView navuserfirstName, navuserlastName, navuserEmail, btnChangepassword;
    Button btnSubmit, btnUpload;
    LinearLayoutCompat btnHome, btnProfile, btnList, btnFav, btnCart;
    SessionManager sessionManager;
    private int PICK_IMAGE_REQUEST = 1;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 112;




    private static final String SHARED_PREFS = "sharedPrefs";
    Context context;
    String filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        imgPrf = (ImageView) findViewById(R.id.imgPrf);
        etFirstname = (EditText) findViewById(R.id.etFirstname);
        etLastname = (EditText) findViewById(R.id.etLastname);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        spGender = (Spinner) findViewById(R.id.spGender);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnHome = (LinearLayoutCompat)findViewById(R.id.btnHome);
        btnCart = (LinearLayoutCompat)findViewById(R.id.btnCart);
        btnFav = (LinearLayoutCompat)findViewById(R.id.btnFav);
        btnList = (LinearLayoutCompat)findViewById(R.id.btnList);
        btnProfile = (LinearLayoutCompat)findViewById(R.id.btnProfile);
        btnChangepassword = (TextView)findViewById(R.id.btnChangepassword);
        btneditMail = (ImageView)findViewById(R.id.btneditMail);

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
                Editprofile.this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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


        requestAppPermissions();


        navuserfirstName.setText(firstname);
        navuserlastName.setText(lastname);
        navuserEmail.setText(email);

        etEmail.setText(email);
        etFirstname.setText(firstname);
        etLastname.setText(lastname);
        etPhone.setText(phone);
        etPhone.setEnabled(false);
        etEmail.setEnabled(false);

        Glide.with(Editprofile.this)
                .load(profilepic)
                .circleCrop()
                .placeholder(R.drawable.dp)
                .into(navprofilePic);


        Glide.with(Editprofile.this)
                .load(profilepic)
                .circleCrop()
                .placeholder(R.drawable.dp)
                .into(imgPrf);



        findViewById(R.id.iv_menu).setOnClickListener(view -> {
            setDrawerLocked();
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkblank();


            }
        });


        btnChangepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Editprofile.this, Changepassword.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });


        btneditMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Editprofile.this, ChangeEmail.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Editprofile.this, HomeActivity.class));
                finish();
            }
        });



        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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


                Intent intent = new Intent(Editprofile.this, Cartdetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });





        imgPrf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showFileChooser();

            }
        });
//
//        btnUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//            }
//        });


        onclick();
        spgender();


    }

    //
    private void showFileChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                uploadToServer(GetRealPathFromUri.getPathFromUri(Editprofile.this, filePath));


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadToServer(String filePath) {
        showProgressDialog();
        Retrofit retrofit = ApiClient.getRetrofitClient(this);
        ApiInterface uploadAPIs = retrofit.create(ApiInterface.class);
        //Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), fileReqBody);
        //Create request body with text description and text media type
        RequestBody userType = RequestBody.create(MediaType.parse("text/plain"), "customer");
        //userType

        Call<ResponseBody> mcall = uploadAPIs.uploadImage(part, userType, authToken);
        mcall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        String result = response.body().string();
                        Log.v("response-->", result);
                        JSONObject mjonsresponse = new JSONObject(result);
                        //{"success":true,"STATUSCODE":200,"message":"Profile pic uploaded successfully.","response_data":{"profileImage":"https://nodeserver.brainiuminfotech.com:2100/img/profile-pic/5ee8e2cc87413b5ae1135708-1592569582667.jpg"}}
                        if (mjonsresponse.getBoolean("success")) {
                            hideProgressDialog();
                            String picurl = mjonsresponse.getJSONObject("response_data").getString("profileImage");
                            
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("profilepic", picurl);
                            editor.apply();

                            Log.v("picurl-->", picurl);
                            Glide.with(Editprofile.this)
                                    .load(picurl)
                                    .circleCrop()
                                    .placeholder(R.drawable.dp)
                                    .into(imgPrf);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("onFailure", t.getMessage());
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


    private void onclick() {

        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {


                    item = adapterView.getItemAtPosition(i).toString();


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void spgender() {

        List<String> gender = new ArrayList<String>();
        gender.add("Male");
        gender.add("Female");
        gender.add("Other");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gender);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(arrayAdapter);


    }


    public void checkblank() {


        firstName = etFirstname.getText().toString();
        lastName = etLastname.getText().toString();
        Email = etEmail.getText().toString();
        phoneNumber = etPhone.getText().toString();
        selectGender = item;


        if (firstName.length() == 0) {

            Toast.makeText(this, "Please enter Name", Toast.LENGTH_SHORT).show();

        } else if (lastName.length() == 0) {

            Toast.makeText(this, "Please enter Last name", Toast.LENGTH_SHORT).show();

        } else if (Email.length() == 0) {

            Toast.makeText(this, "Please enter Valid Email", Toast.LENGTH_SHORT).show();

        } else if (phoneNumber.length() == 0) {

            Toast.makeText(this, "Please enter Valid Phone Number", Toast.LENGTH_SHORT).show();


        } else {

            submitProfile();
        }

    }


    public void submitProfile() {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();


            JSONObject params = new JSONObject();

            try {
                params.put("firstName", firstName);
                params.put("lastName", lastName);
                params.put("customerId", cid);
                params.put("userType", "customer");
                params.put("loginType", "EMAIL");
                params.put("gender", "M");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.Editprofileurl, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");
                    if (success) {

                        Toast.makeText(Editprofile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();

                        JSONObject response_data = result.getJSONObject("response_data");
                        String gender = response_data.getString("gender");
//                        String phone = response_data.getString("phone");
                        String password = response_data.getString("password");
                        String location = response_data.getString("location");
                        String profileImage = response_data.getString("profileImage");
                        String isActive = response_data.getString("isActive");
                        String deviceToken = response_data.getString("deviceToken");
                        String loginType = response_data.getString("loginType");
                        String socialId = response_data.getString("socialId");
                        String pushMode = response_data.getString("pushMode");
                        String _id = response_data.getString("_id");
                        String firstName = response_data.getString("firstName");
                        String lastName = response_data.getString("lastName");
                        String email = response_data.getString("email");
                        String appType = response_data.getString("appType");
                        String __v = response_data.getString("__v");


                        //UpdateShareprefrence

                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("firstName", firstName);
                        editor.putString("lastName", lastName);
//                        editor.putString("email", email);
                        editor.putString("profileImage", profileImage);
                        editor.putString("id", _id);
                        editor.apply();


                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(Editprofile.this, "invalid 1", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Editprofile.this, "Invalid", Toast.LENGTH_SHORT).show();

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

                        Toast.makeText(Editprofile.this, msg, Toast.LENGTH_SHORT).show();

                        sessionManager.logoutUser();
                        LoginManager.getInstance().logOut();
                        signOut();
                        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        settings.edit().clear().apply();
                        startActivity(new Intent(Editprofile.this, Login.class));
                        finish();


                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(Editprofile.this, "Login not successfull", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Editprofile.this, "Invalid", Toast.LENGTH_SHORT).show();

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


    // RUN TIME PERMISSIONS FOR READ AND WRITE

    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_WRITE_STORAGE_REQUEST_CODE); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }


    // END


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
            startActivity(new Intent(Editprofile.this, Myfavourite.class));

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

            Intent intent = new Intent(Editprofile.this, Payment.class);
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
