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
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Verifyaccount extends AppCompatActivity {

    String email, OTP, cid, otp, authToken, phonenumber;
    TextView userNumber, btnResend;
    EditText etOTP;
    Button btnSubmit;

    private static final String TAG = "Myapp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyaccount);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        cid = intent.getStringExtra("cid");
        otp = intent.getStringExtra("otp");
        phonenumber = intent.getStringExtra("phonenumber");
        authToken = intent.getStringExtra("authToken");
        userNumber = (TextView) findViewById(R.id.userNumber);
        btnResend = (TextView) findViewById(R.id.btnResend);
        etOTP = (EditText) findViewById(R.id.etOTP);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        userNumber.setText(email);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkblank();

            }
        });



        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                resendOTP();





            }
        });

    }


    public void checkblank() {

        OTP = etOTP.getText().toString();

        if (OTP.length() == 0) {

            Toast.makeText(this, "Please enter 4 Digit OTP", Toast.LENGTH_SHORT).show();

        } else if (OTP.length() < 4) {

            Toast.makeText(this, "Please enter 4 Digit OTP", Toast.LENGTH_SHORT).show();


        } else {

            OTPverification();
        }
    }



    public void resendOTP(){



        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();


            JSONObject params = new JSONObject();

            try {
                params.put("phone", phonenumber);
                params.put("usedFor", "ChangeEmail");
                params.put("userType", "customer");

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

                        Toast.makeText(Verifyaccount.this, msg, Toast.LENGTH_SHORT).show();


                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(Verifyaccount.this, "invalid", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Verifyaccount.this, "Invalid", Toast.LENGTH_SHORT).show();

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


    public void OTPverification() {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();

            JSONObject params = new JSONObject();

            try {
                params.put("otp", otp);
                params.put("phone", phonenumber);
                params.put("cid", cid);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.OTPurl, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    Toast.makeText(Verifyaccount.this, msg, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");
                    if (success) {
                        Intent intent = new Intent(this, Login.class);
                        startActivity(intent);
                        finish();

                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(Verifyaccount.this, "invalid", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(Verifyaccount.this, "invalid", Toast.LENGTH_SHORT).show();

                    error.printStackTrace();
                    //TODO: handle failure
                }
            });

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


}
