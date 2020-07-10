package com.example.nowastesociety;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nowastesociety.allurl.AllUrl;
import com.example.nowastesociety.internet.CheckConnectivity;
import com.example.nowastesociety.session.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    EditText etFirstname, etLastname, etEmail, etPhone, etPassword, etConfirmpassword;
    Button btnSignup;
    CheckBox btnCheck;
    TextView tvSignin;

    String firstName, lastName, emailAddress, phoneNumber, password, confirmPassword, socailId, socialpassword, socailemail;
    String fname, lname, email;

    private static final String SHARED_PREFS = "sharedPrefs";
    SessionManager sessionManager;


    private static final String TAG = "Myapp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Intent intent = getIntent();

        fname = intent.getStringExtra("firstName");
        lname = intent.getStringExtra("lastName");
        email = intent.getStringExtra("email");

        sessionManager = new SessionManager(getApplicationContext());



        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");
        Log.d(TAG, "PushToken-->" + FirebaseInstanceId.getInstance().getToken());


        etFirstname = (EditText) findViewById(R.id.etFirstname);
        etLastname = (EditText) findViewById(R.id.etLastname);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmpassword = (EditText) findViewById(R.id.etConfirmpassword);
        btnCheck = (CheckBox) findViewById(R.id.btnCheck);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        tvSignin = (TextView) findViewById(R.id.tvSignin);

        socailId = intent.getStringExtra("socail");


        if (socailId == null) {

            socailId = "";


        } else {

            socailId = intent.getStringExtra("socail");

            etPassword.setVisibility(View.GONE);
            etConfirmpassword.setVisibility(View.GONE);

        }

        Log.d(TAG, "socailId-->" + socailId);


        if (fname != null && fname.length() > 0) {

            etFirstname.setText(fname);
        } else {

            etFirstname.setText("");
        }

        if (lname != null && lname.length() > 0) {

            etLastname.setText(lname);
        } else {

            etLastname.setText("");
        }

        if (email != null && email.length() > 0) {

            etEmail.setText(email);
        } else {

            etEmail.setText("");
        }


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (socailId.equals("")){
                    checkblank();

                }else {

                    checkblankforsocial();
                }

            }
        });


        tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
            }
        });

    }


    public void checkblank() {
        firstName = etFirstname.getText().toString();
        lastName = etLastname.getText().toString();
        emailAddress = etEmail.getText().toString();
        phoneNumber = etPhone.getText().toString();
        password = etPassword.getText().toString();
        confirmPassword = etConfirmpassword.getText().toString();

        if (firstName.length() == 0) {
            Toast.makeText(this, "Please enter First Name", Toast.LENGTH_SHORT).show();
        } else if (lastName.length() == 0) {

            Toast.makeText(this, "Please enter Last Name", Toast.LENGTH_SHORT).show();

        } else if (emailAddress.length() == 0) {

            Toast.makeText(this, "Please enter a Valid Email", Toast.LENGTH_SHORT).show();

        } else if (phoneNumber.length() == 0) {

            Toast.makeText(this, "Please enter a Phone Number", Toast.LENGTH_SHORT).show();

        } else if (password.length() == 0) {

            Toast.makeText(this, "Please enter a Password", Toast.LENGTH_SHORT).show();

        } else if (confirmPassword.length() == 0) {

            Toast.makeText(this, "Please enter a Confirm Password", Toast.LENGTH_SHORT).show();


        } else if (!password.equals(confirmPassword)) {

            Toast.makeText(this, "Please Check Password and match", Toast.LENGTH_SHORT).show();

        } else if (btnCheck.isChecked()) {

            Regstration();


        }

    }



    public void checkblankforsocial() {
        firstName = etFirstname.getText().toString();
        lastName = etLastname.getText().toString();
        socailemail = etEmail.getText().toString();
        phoneNumber = etPhone.getText().toString();
        socialpassword = "";

        if (firstName.length() == 0) {
            Toast.makeText(this, "Please enter First Name", Toast.LENGTH_SHORT).show();
        } else if (lastName.length() == 0) {

            Toast.makeText(this, "Please enter Last Name", Toast.LENGTH_SHORT).show();

        } else if (socailemail.length() == 0) {

            Toast.makeText(this, "Please enter a Valid Email", Toast.LENGTH_SHORT).show();

        } else if (phoneNumber.length() == 0) {

            Toast.makeText(this, "Please enter a Phone Number", Toast.LENGTH_SHORT).show();

        } else if (btnCheck.isChecked()) {

            Regstration();


        }

    }







    public void Regstration() {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            if (socailId.equals("")) {

                showProgressDialog();

                JSONObject params = new JSONObject();

                try {
                    params.put("firstName", firstName);
                    params.put("lastName", lastName);
                    params.put("email", emailAddress);
                    params.put("phone", phoneNumber);
                    params.put("location", "xyz");
                    params.put("password", password);
                    params.put("confirmPassword", confirmPassword);
                    params.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
                    params.put("appType", "ANDROID");
                    params.put("pushMode", "P");
                    params.put("loginType", "EMAIL");
                    params.put("socialId", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.RegistrationUrl, params, response -> {

                    Log.i("Response-->", String.valueOf(response));

                    try {
                        JSONObject result = new JSONObject(String.valueOf(response));
                        String msg = result.getString("message");
                        Toast.makeText(Registration.this, msg, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "msg-->" + msg);
                        boolean success = result.getBoolean("success");
                        if (success) {
                            JSONObject response_data = result.getJSONObject("response_data");
                            JSONObject userDetails = response_data.getJSONObject("userDetails");
                            String name = userDetails.getString("firstName");
                            String phonenumber = userDetails.getString("phone");
                            String cid = userDetails.getString("_id");
                            String otp = userDetails.getString("otp");
                            String email = userDetails.getString("email");

                            String authToken = response_data.getString("authToken");
                            Intent intent = new Intent(this, Verifyaccount.class);
                            intent.putExtra("email", email);
                            intent.putExtra("phonenumber", phonenumber);
                            intent.putExtra("cid", cid);
                            intent.putExtra("otp", otp);
                            intent.putExtra("authToken", authToken);
                            startActivity(intent);
                            finish();

                        } else {

                            Log.d(TAG, "unsuccessfull - " + "Error");
                            Toast.makeText(Registration.this, "invalid", Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    hideProgressDialog();

                    //TODO: handle success
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(Registration.this, "invalid", Toast.LENGTH_SHORT).show();

                        error.printStackTrace();
                        //TODO: handle failure
                    }
                });

                Volley.newRequestQueue(this).add(jsonRequest);


            } else {


                showProgressDialog();

                JSONObject params = new JSONObject();

                try {
                    params.put("firstName", firstName);
                    params.put("lastName", lastName);
                    params.put("email", socailemail);
                    params.put("phone", phoneNumber);
                    params.put("location", "xyz");
                    params.put("password", socialpassword);
                    params.put("confirmPassword", socialpassword);
                    params.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
                    params.put("appType", "ANDROID");
                    params.put("pushMode", "P");
                    params.put("loginType", "EMAIL");
                    params.put("socialId", socailId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.RegistrationUrl, params, response -> {

                    Log.i("Response-->", String.valueOf(response));

                    try {
                        JSONObject result = new JSONObject(String.valueOf(response));
                        String msg = result.getString("message");
                        Toast.makeText(Registration.this, msg, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "msg-->" + msg);
                        boolean success = result.getBoolean("success");
                        if (success) {
                            JSONObject response_data = result.getJSONObject("response_data");
                            JSONObject userDetails = response_data.getJSONObject("userDetails");
                            String firstName = userDetails.getString("firstName");
                            String lastName = userDetails.getString("lastName");
                            String email = userDetails.getString("email");
                            String phone = userDetails.getString("phone");
                            String profileImage = userDetails.getString("profileImage");
                            String id = userDetails.getString("_id");
                            String loginId = userDetails.getString("loginId");


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

                            sessionManager.createLoginSession(socailemail,socialpassword);
                            Intent intent = new Intent(Registration.this, HomeActivity.class);
                            startActivity(intent);
                            finish();


                        } else {

                            Log.d(TAG, "unsuccessfull - " + "Error");
                            Toast.makeText(Registration.this, "invalid", Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    hideProgressDialog();

                    //TODO: handle success
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(Registration.this, "invalid", Toast.LENGTH_SHORT).show();

                        error.printStackTrace();
                        //TODO: handle failure
                    }
                });

                Volley.newRequestQueue(this).add(jsonRequest);


            }


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
        //Execute your code here
        finish();

    }


}




