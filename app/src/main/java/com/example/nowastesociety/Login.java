package com.example.nowastesociety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;

import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nowastesociety.allurl.AllUrl;
import com.example.nowastesociety.internet.CheckConnectivity;
import com.example.nowastesociety.session.SessionManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;


import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    EditText etEmai, etPassword;
    TextView btnForgotpassword, btnSignup;
    Button btnLogin;
    LoginButton login_button;
    SignInButton sign_in_button;
    String useremail, password, fbloginstatus;
    SessionManager sessionManager;
    LinearLayoutCompat btnFb, btnG;

    private static final String TAG = "Myapp";
    private static final String SHARED_PREFS = "sharedPrefs";
    Context context;
    CallbackManager callbackManager;
    private GoogleApiClient mGoogleApi;
    private static final int RC_SIGN_IN = 7;
    String deviceToken;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");
        deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"TOKEN--->"+deviceToken);

        etEmai = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnForgotpassword = (TextView) findViewById(R.id.btnForgotpassword);
        btnSignup = (TextView) findViewById(R.id.btnSignup);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnFb = (LinearLayoutCompat) findViewById(R.id.btnFb);
        btnG = (LinearLayoutCompat) findViewById(R.id.btnG);
        login_button = (LoginButton) findViewById(R.id.login_button);
        sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApi = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


        callbackManager = CallbackManager.Factory.create();

        sessionManager = new SessionManager(getApplicationContext());





        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkblank();

            }
        });


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });


        btnForgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Login.this, Forgotpassword.class);
                startActivity(intent);

            }
        });


        btnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                login_button.performClick();
            }
        });


        btnG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent signIngoogle = Auth.GoogleSignInApi.getSignInIntent(mGoogleApi);
                startActivityForResult(signIngoogle,RC_SIGN_IN);

            }
        });


        loginwithfacebook();


    }









    public void checkblank() {

        useremail = etEmai.getText().toString();
        password = etPassword.getText().toString();

        if (useremail.length() == 0) {

            Toast.makeText(this, "Please enter a Valid Email", Toast.LENGTH_SHORT).show();

        } else if (password.length() == 0) {

            Toast.makeText(this, "Please enter a Password", Toast.LENGTH_SHORT).show();

        } else {

            login();
        }

    }


    public void login() {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();


            JSONObject params = new JSONObject();

            try {
                params.put("user", useremail);
                params.put("password", password);
                params.put("userType", "customer");
                params.put("loginType", "EMAIL");
                params.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
                params.put("appType", "ANDROID");
                params.put("pushMode", "P");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.loginUrl, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    Log.d(TAG, "msg-->" + msg);
                    Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
                    boolean success = result.getBoolean("success");
                    if (success) {


                        JSONObject response_data = result.getJSONObject("response_data");
                        JSONObject userDetails = response_data.getJSONObject("userDetails");
                        String firstName = userDetails.getString("firstName");
                        String lastName = userDetails.getString("lastName");
                        String email = userDetails.getString("email");
                        String phone = userDetails.getString("phone");
                        String socialId = userDetails.getString("socialId");
                        String id = userDetails.getString("id");
                        String loginId = userDetails.getString("loginId");
                        String profileImage = userDetails.getString("profileImage");
                        String userType = userDetails.getString("userType");
                        String loginType = userDetails.getString("loginType");
//
//
                        String authToken = response_data.getString("authToken");


                        //SharedPref
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("firstName", firstName);
                        editor.putString("lastName", lastName);
                        editor.putString("email", email);
                        editor.putString("authToken", authToken);
                        editor.putString("id", id);
                        editor.putString("loginId", loginId);
                        editor.putString("profilepic",profileImage);
                        editor.apply();


                        sessionManager.createLoginSession(useremail, password);
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(Login.this, "invalid", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(Login.this, "invalid", Toast.LENGTH_SHORT).show();

                    error.printStackTrace();
                    //TODO: handle failure
                }
            });

            Volley.newRequestQueue(this).add(jsonRequest);

        } else {

            Toast.makeText(getApplicationContext(), "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show();

        }
    }


    public void loginwithfacebook() {


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                fbloginstatus = loginResult.getAccessToken().getToken();
                Log.d(TAG, "tokenlogin-->" + fbloginstatus);
                Profile profile = Profile.getCurrentProfile();
                Log.d(TAG, "Fbdetails-->" + profile.getId());

                showProgressDialog();

                JSONObject params = new JSONObject();

                try {
                    params.put("user", profile.getId());
                    params.put("password", "");
                    params.put("userType", "customer");
                    params.put("loginType", "FACEBOOK");
                    params.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
                    params.put("appType", "ANDROID");
                    params.put("pushMode", "P");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.loginUrl, params, response -> {

                    Log.i("Response-->", String.valueOf(response));

                    try {
                        JSONObject result = new JSONObject(String.valueOf(response));
                        String msg = result.getString("message");
                        Log.d(TAG, "msg-->" + msg);
                        Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
                        int statuscode = result.getInt("STATUSCODE");
                        Log.d(TAG, "statuscode-->" + statuscode);

                        boolean success = result.getBoolean("success");
                        if (statuscode == 201) {

                            Intent intent = new Intent(Login.this, Registration.class);
                            intent.putExtra("socailId", profile.getId());
                            intent.putExtra("firstName",profile.getFirstName());
                            intent.putExtra("lastName", profile.getLastName());
                            intent.putExtra("email", "");

                            startActivity(intent);
                            finish();


                        } else if (statuscode == 200) {


                            JSONObject response_data = result.getJSONObject("response_data");
                            Log.d(TAG, "responsedata-->" + response_data);
                            JSONObject userDetails = response_data.getJSONObject("userDetails");
                            String firstName = userDetails.getString("firstName");
                            String lastName = userDetails.getString("lastName");
                            String email = userDetails.getString("email");
                            String phone = userDetails.getString("phone");
                            String socialId = userDetails.getString("socialId");
                            String id = userDetails.getString("id");
                            String loginId = userDetails.getString("loginId");
                            String profileImage = userDetails.getString("profileImage");
                            String userType = userDetails.getString("userType");
                            String loginType = userDetails.getString("loginType");
////
////
                            String authToken = response_data.getString("authToken");


                            //SharedPref
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("firstName", firstName);
                            editor.putString("lastName", lastName);
                            editor.putString("email", email);
                            editor.putString("profilepic", profileImage);
                            editor.putString("authToken", authToken);
                            editor.putString("loginId", loginId);
                            editor.putString("id", id);
                            editor.apply();
//
//
//
                            sessionManager.createLoginSession(useremail, password);
                            Intent intent = new Intent(Login.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
//

                        } else {

                            Log.d(TAG, "unsuccessfull - " + "Error");
                            Toast.makeText(Login.this, "invalid", Toast.LENGTH_SHORT).show();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    hideProgressDialog();

                    //TODO: handle success
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(Login.this, "invalid", Toast.LENGTH_SHORT).show();

                        error.printStackTrace();
                        //TODO: handle failure
                    }
                });

                Volley.newRequestQueue(Login.this).add(jsonRequest);


            }

            @Override
            public void onCancel() {

                Toast.makeText(getApplicationContext(), "Facebook Login cancel", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onError(FacebookException error) {

                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "error--->" + error.getMessage());


            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("GoogleLogincode :", String.valueOf(requestCode));


        if (requestCode == RC_SIGN_IN){

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handelSigninresult(result);
        }


    }

    private void handelSigninresult(GoogleSignInResult result) {

        Log.e("GoogleLogin :", String.valueOf(result.isSuccess()));
        if (result.isSuccess()){

            GoogleSignInAccount account = result.getSignInAccount();
            String socialid = account.getId();
            Log.d(TAG, "GoogleID-->" + account.getId());

            showProgressDialog();

            JSONObject params = new JSONObject();

            try {
                params.put("user", socialid);
                params.put("password", "");
                params.put("userType", "customer");
                params.put("loginType", "GOOGLE");
                params.put("deviceToken", deviceToken);
                params.put("appType", "ANDROID");
                params.put("pushMode", "P");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.loginUrl, params, response -> {


                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result2 = new JSONObject(String.valueOf(response));
                    String msg = result2.getString("message");
                    Log.d(TAG, "msg-->" + msg);
                    Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
                    int statuscode = result2.getInt("STATUSCODE");
                    Log.d(TAG, "statuscode-->" + statuscode);

                    boolean success = result2.getBoolean("success");
                    if (statuscode == 201) {

                        Intent intent = new Intent(Login.this, Registration.class);
                        intent.putExtra("socail", socialid);
                        intent.putExtra("firstName",account.getGivenName());
                        intent.putExtra("lastName", "");
                        intent.putExtra("email", account.getEmail());
                        startActivity(intent);
                        finish();


                    } else if (statuscode == 200) {


                        JSONObject response_data = result2.getJSONObject("response_data");
                        Log.d(TAG, "responsedata-->" + response_data);
                        JSONObject userDetails = response_data.getJSONObject("userDetails");
                        String firstName = userDetails.getString("firstName");
                        String lastName = userDetails.getString("lastName");
                        String email = userDetails.getString("email");
                        String phone = userDetails.getString("phone");
                        String socialId = userDetails.getString("socialId");
                        String id = userDetails.getString("id");
                        String loginId = userDetails.getString("loginId");
                        String profileImage = userDetails.getString("profileImage");
                        String userType = userDetails.getString("userType");
                        String loginType = userDetails.getString("loginType");
////
////
                        String authToken = response_data.getString("authToken");


                        //SharedPref
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("firstName", firstName);
                        editor.putString("lastName", lastName);
                        editor.putString("email", email);
                        editor.putString("profilepic", profileImage);
                        editor.putString("authToken", authToken);
                        editor.putString("loginId", loginId);
                        editor.putString("id", id);
                        editor.apply();
//
//
//
                        sessionManager.createLoginSession(useremail, password);
                        Intent intent = new Intent(Login.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
//

                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(Login.this, "invalid", Toast.LENGTH_SHORT).show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(Login.this, "invalid", Toast.LENGTH_SHORT).show();

                    error.printStackTrace();
                    //TODO: handle failure
                }
            });

            Volley.newRequestQueue(Login.this).add(jsonRequest);


        }else {

            Toast.makeText(getApplicationContext(), "Google Login not Success", Toast.LENGTH_SHORT).show();

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
        //Execute your code here

        finishAffinity();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
