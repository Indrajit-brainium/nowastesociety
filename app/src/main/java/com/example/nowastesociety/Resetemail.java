package com.example.nowastesociety;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nowastesociety.allurl.AllUrl;
import com.example.nowastesociety.internet.CheckConnectivity;
import com.example.nowastesociety.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Resetemail extends AppCompatActivity {

    EditText etOTP, etNewmail, etConfirmmail;
    TextView userEmail;
    Button btnSubmit;
    private static final String TAG = "Myapp";
    SessionManager sessionManager;
    private static final String SHARED_PREFS = "sharedPrefs";
    String firstname, lastname, email, imgurl, item, cid, authToken, profilepic, loginId, phone, useremail;
    String otp, newEmail, confirmEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetemail);
        etOTP = (EditText)findViewById(R.id.etOTP);
        etNewmail = (EditText)findViewById(R.id.etNewmail);
        etConfirmmail = (EditText)findViewById(R.id.etConfirmmail);
        userEmail = (TextView)findViewById(R.id.userEmail);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        sessionManager = new SessionManager(getApplicationContext());
        Intent intent = getIntent();
        useremail = intent.getStringExtra("email");
        userEmail.setText(useremail);

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


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkblank();

            }
        });

    }



    public void checkblank() {

        otp = etOTP.getText().toString();
        newEmail = etNewmail.getText().toString();
        confirmEmail = etConfirmmail.getText().toString();

        if (otp.length()==0){

            Toast.makeText(this, "Please enter Valid OTP", Toast.LENGTH_SHORT).show();

        }else if (newEmail.length()==0){

            Toast.makeText(this, "Please enter new Email", Toast.LENGTH_SHORT).show();

        }else if (!newEmail.equals(confirmEmail)){

            Toast.makeText(this, "Please Check Email and match", Toast.LENGTH_SHORT).show();

        }else {

            resetEmail();
        }


    }



    public void resetEmail(){


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();


            JSONObject params = new JSONObject();

            try {
                params.put("otp", otp);
                params.put("email", confirmEmail);
                params.put("userType", "customer");


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.ResetEmail, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");
                    if (success) {

                        Toast.makeText(Resetemail.this, msg, Toast.LENGTH_SHORT).show();


                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", confirmEmail);
                        editor.apply();
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(Resetemail.this, "invalid", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Resetemail.this, "Invalid", Toast.LENGTH_SHORT).show();

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
        super.onBackPressed();
    }
}
