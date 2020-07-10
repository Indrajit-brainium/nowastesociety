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
import android.widget.ImageView;
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

public class Editaddress extends AppCompatActivity {

    private static final String TAG = "Myapp";
    EditText etaddressType, etflatOrHouse, etareaOrColony, etpinCode, ettownOrCity, etLandmark;
    String addresstype, flatorhouse, areaorcolony, pincode, townorcity, landmark, authToken;
    String addressId, Landmark, flatOrHouseOrBuildingOrCompany, areaOrColonyOrStreetOrSector, pinCode, townOrCity, userId, addressType;
    private static final String SHARED_PREFS = "sharedPrefs";
    SessionManager sessionManager;
    Button btnEditaddress;
    ImageView btn_back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editaddress);
        sessionManager = new SessionManager(getApplicationContext());
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken", "");

        Intent intent = getIntent();
        addressId = intent.getStringExtra("addressId");
        Landmark = intent.getStringExtra("landmark");
        flatOrHouseOrBuildingOrCompany = intent.getStringExtra("flatOrHouseOrBuildingOrCompany");
        areaOrColonyOrStreetOrSector = intent.getStringExtra("areaOrColonyOrStreetOrSector");
        pinCode = intent.getStringExtra("pinCode");
        townOrCity = intent.getStringExtra("townOrCity");
        addressType = intent.getStringExtra("addressType");



        Log.d(TAG, "addressId-->" + addressId);


        etaddressType = (EditText) findViewById(R.id.etaddressType);
        etflatOrHouse = (EditText) findViewById(R.id.etflatOrHouse);
        etareaOrColony = (EditText) findViewById(R.id.etareaOrColony);
        etpinCode = (EditText) findViewById(R.id.etpinCode);
        ettownOrCity = (EditText) findViewById(R.id.ettownOrCity);
        etLandmark = (EditText) findViewById(R.id.etLandmark);
        btnEditaddress = (Button) findViewById(R.id.btnEditaddress);
        btn_back = (ImageView) findViewById(R.id.btn_back);


        etaddressType.setText(addressType);
        etareaOrColony.setText(areaOrColonyOrStreetOrSector);
        etflatOrHouse.setText(flatOrHouseOrBuildingOrCompany);
        etpinCode.setText(pinCode);
        ettownOrCity.setText(townOrCity);
        etLandmark.setText(Landmark);


        btnEditaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkblank();

            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });


    }


    public void checkblank() {

        addresstype = etaddressType.getText().toString();
        flatorhouse = etflatOrHouse.getText().toString();
        areaorcolony = etareaOrColony.getText().toString();
        pincode = etpinCode.getText().toString();
        townorcity = ettownOrCity.getText().toString();
        landmark = etLandmark.getText().toString();

        if (addresstype.length() == 0) {

            Toast.makeText(this, "Enter Address Type", Toast.LENGTH_LONG).show();

        } else if (flatorhouse.length() == 0) {

            Toast.makeText(this, "Enter Flat or House", Toast.LENGTH_LONG).show();

        } else if (areaorcolony.length() == 0) {

            Toast.makeText(this, "Enter Area or Colony", Toast.LENGTH_LONG).show();

        } else if (pincode.length() == 0) {

            Toast.makeText(this, "Enter Pincode", Toast.LENGTH_LONG).show();

        } else if (townorcity.length() == 0) {

            Toast.makeText(this, "Enter Town", Toast.LENGTH_LONG).show();

        } else if (landmark.length() == 0) {

            Toast.makeText(this, "Enter Landmark", Toast.LENGTH_LONG).show();

        } else {

            editaddress();
        }


    }


    public void editaddress() {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();

            JSONObject params = new JSONObject();

            try {
                params.put("addressType", addresstype);
                params.put("flatOrHouseOrBuildingOrCompany", flatorhouse);
                params.put("areaOrColonyOrStreetOrSector", areaorcolony);
                params.put("pinCode", pincode);
                params.put("townOrCity", townorcity);
                params.put("landmark", landmark);
                params.put("isDefault", "0");
                params.put("addressId", addressId);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.EditAddress, params, response -> {

                Log.i("Response-->", String.valueOf(response));

                try {
                    JSONObject result = new JSONObject(String.valueOf(response));
                    String msg = result.getString("message");
                    boolean success = result.getBoolean("success");
                    if (success) {

                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, Myaddress.class);
                        startActivity(intent);
                        finish();

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
                    Toast.makeText(Editaddress.this, "Invalid", Toast.LENGTH_SHORT).show();

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
