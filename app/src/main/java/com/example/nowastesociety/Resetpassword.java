package com.example.nowastesociety;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nowastesociety.allurl.AllUrl;
import com.example.nowastesociety.internet.CheckConnectivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Resetpassword extends AppCompatActivity {

    EditText etOTP, etNewpassword, etConfirmpassword;
    String newOTP, newPassword, confirmPassword, email;
    Button btnDone;
    private static final String TAG = "Myapp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");


        etOTP = (EditText) findViewById(R.id.etOTP);
        etNewpassword = (EditText) findViewById(R.id.etNewpassword);
        etConfirmpassword = (EditText) findViewById(R.id.etConfirmpassword);
        btnDone = (Button) findViewById(R.id.btnDone);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkblank();

            }
        });


    }


    public void checkblank() {

        newOTP = etOTP.getText().toString();
        newPassword = etNewpassword.getText().toString();
        confirmPassword = etConfirmpassword.getText().toString();

        if (newOTP.length() == 0) {

            Toast.makeText(this, "Please enter a Valid OTP", Toast.LENGTH_SHORT).show();

        } else if (newPassword.length() == 0) {

            Toast.makeText(this, "Please enter a Valid Password", Toast.LENGTH_SHORT).show();


        } else if (confirmPassword.length() == 0) {

            Toast.makeText(this, "Please enter Confirm Password", Toast.LENGTH_SHORT).show();

        } else if (!newPassword.equals(confirmPassword)) {

            Toast.makeText(this, "Please Check Password and match", Toast.LENGTH_SHORT).show();

        } else {

            resetpassword();
        }

    }


    public void resetpassword() {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();


            JSONObject params = new JSONObject();

            try {
                params.put("email", email);
                params.put("password", newPassword);
                params.put("confirmPassword", confirmPassword);
                params.put("userType", "customer");
                params.put("otp", newOTP);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.Resetpasswordurl, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    Log.d(TAG, "msg-->" + msg);
                    boolean success = result.getBoolean("success");
                    if (success) {

                        Toast.makeText(Resetpassword.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(this, Login.class);
                        startActivity(intent);
                        finish();

                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(Resetpassword.this, "invalid", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Resetpassword.this, "Invalid", Toast.LENGTH_SHORT).show();

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
