package com.example.nowastesociety.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nowastesociety.Editaddress;
import com.example.nowastesociety.Myaddress;
import com.example.nowastesociety.R;
import com.example.nowastesociety.allurl.AllUrl;
import com.example.nowastesociety.internet.CheckConnectivity;
import com.example.nowastesociety.model.AddressviewModel;
import com.example.nowastesociety.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddressviewAdapter extends RecyclerView.Adapter<AddressviewAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<AddressviewModel> addressviewModelArrayList;
    Context ctx;
    String addressId;

    public AddressviewAdapter(Context ctx, ArrayList<AddressviewModel> addressviewModelArrayList) {

        inflater = LayoutInflater.from(ctx);
        this.addressviewModelArrayList = addressviewModelArrayList;
        this.ctx = ctx;


    }


    @NonNull
    @Override
    public AddressviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.rv_addresslist, parent, false);
        MyViewHolder holder = new MyViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddressviewAdapter.MyViewHolder holder, int position) {

        holder.tvaddressType.setText(addressviewModelArrayList.get(position).getTvaddressType());
        holder.tvAddressdetails.setText(addressviewModelArrayList.get(position).getTvAddressdetails());
        addressId = addressviewModelArrayList.get(position).getAddressId();
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(ctx, Editaddress.class);
                intent.putExtra("addressId", addressviewModelArrayList.get(position).getAddressId());
                intent.putExtra("landmark", addressviewModelArrayList.get(position).getLandmark());
                intent.putExtra("addressType", addressviewModelArrayList.get(position).getTvaddressType());
                intent.putExtra("flatOrHouseOrBuildingOrCompany",
                        addressviewModelArrayList.get(position).getFlatOrHouseOrBuildingOrCompany());
                intent.putExtra("areaOrColonyOrStreetOrSector",
                        addressviewModelArrayList.get(position).getTvAddressdetails());
                intent.putExtra("pinCode", addressviewModelArrayList.get(position).getPinCode());
                intent.putExtra("townOrCity", addressviewModelArrayList.get(position).getTownOrCity());
                intent.putExtra("userId", addressviewModelArrayList.get(position).getUserId());


                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);

            }
        });


        holder.btnDelet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.alertDialogDemo();

            }
        });





    }

    @Override
    public int getItemCount() {
        return addressviewModelArrayList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "Myapp";
        TextView tvaddressType, tvAddressdetails;
        ImageView btnEdit, btnDelet;
        SessionManager sessionManager;
        private static final String SHARED_PREFS = "sharedPrefs";
        String authToken;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvaddressType = (TextView) itemView.findViewById(R.id.tvaddressType);
            tvAddressdetails = (TextView) itemView.findViewById(R.id.tvAddressdetails);
            btnEdit = (ImageView) itemView.findViewById(R.id.btnEdit);
            btnDelet = (ImageView) itemView.findViewById(R.id.btnDelet);
            sessionManager = new SessionManager(itemView.getContext());
            SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences(SHARED_PREFS, 0);
            authToken = sharedPreferences.getString("authToken", "");
            Log.d(TAG, "AauthToken-->" + authToken);



        }


        void alertDialogDemo() {

            // get alert_dialog.xml view
            LayoutInflater li = LayoutInflater.from(itemView.getContext());
            View promptsView = li.inflate(R.layout.delete_dailouge, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    itemView.getContext());

            // set alert_dialog.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

//            final EditText userInput = (EditText) promptsView.findViewById(R.id.etUserInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // get user input and set it to result
                            // edit text

                            deleteaddress();

                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }


        void deleteaddress() {


            if (CheckConnectivity.getInstance(inflater.getContext()).isOnline()) {


                showProgressDialog();

                JSONObject params = new JSONObject();

                try {
                    params.put("addressId", addressId);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.DeleteAddress, params, response -> {

                    Log.i("Response-->", String.valueOf(response));

                    try {
                        JSONObject result = new JSONObject(String.valueOf(response));
                        String msg = result.getString("message");
                        boolean success = result.getBoolean("success");
                        if (success) {

                            Toast.makeText(itemView.getContext(), msg, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(itemView.getContext(), Myaddress.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            itemView.getContext().startActivity(intent);

                        } else {

                            Toast.makeText(itemView.getContext(), "invalid 1", Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    hideProgressDialog();

                    //TODO: handle success
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(itemView.getContext(), "Invalid", Toast.LENGTH_SHORT).show();

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", authToken);
                        return params;
                    }
                };

                Volley.newRequestQueue(itemView.getContext()).add(jsonRequest);

            } else {

                Toast.makeText(itemView.getContext(), "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show();

            }

        }


        public ProgressDialog mProgressDialog;

        public void showProgressDialog() {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(itemView.getContext());
                mProgressDialog.setMessage("Loading...");
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


}
