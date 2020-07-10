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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;

import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nowastesociety.allurl.AllUrl;
import com.example.nowastesociety.internet.CheckConnectivity;
import com.example.nowastesociety.session.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Forgotpassword extends AppCompatActivity {

    EditText etEmail;
    Button btnSend;
    String emailAddress;
    private static final String TAG = "Myapp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        etEmail = (EditText) findViewById(R.id.etEmail);
        btnSend = (Button) findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkblank();

            }
        });
    }


    public void checkblank() {

        emailAddress = etEmail.getText().toString();

        if (emailAddress.length() == 0) {

            Toast.makeText(this, "Please enter Valid Email", Toast.LENGTH_SHORT).show();

        } else {

            forgotpassword();
        }

    }


    public void forgotpassword() {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();


            JSONObject params = new JSONObject();

            try {
                params.put("email", emailAddress);
                params.put("userType", "customer");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.Forgotpasswordurl, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    Log.d(TAG, "msg-->" + msg);
                    Toast.makeText(Forgotpassword.this, msg, Toast.LENGTH_SHORT).show();

                    boolean success = result.getBoolean("success");
                    if (success) {


                        JSONObject response_data = result.getJSONObject("response_data");

                        String id = response_data.getString("id");
                        String email = response_data.getString("email");
                        int phone = response_data.getInt("phone");
                        String forgotPassOtp = response_data.getString("forgotPassOtp");


                        Intent intent = new Intent(this, Resetpassword.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();

                    } else {

                        Log.d(TAG, "unsuccessfull - " + "Error");
                        Toast.makeText(this, "invalid", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressDialog();

                //TODO: handle success
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(Forgotpassword.this, "invalid", Toast.LENGTH_SHORT).show();

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
