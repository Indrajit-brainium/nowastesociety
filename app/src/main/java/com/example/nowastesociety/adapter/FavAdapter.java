package com.example.nowastesociety.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nowastesociety.MainActivity;
import com.example.nowastesociety.R;
import com.example.nowastesociety.model.ResturentModel;
import com.like.LikeButton;

import java.util.ArrayList;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.MyViewHolder>  {

    private LayoutInflater inflater;
    private ArrayList<ResturentModel> resturentModelArrayList;
    Context ctx;


    public FavAdapter (Context ctx, ArrayList<ResturentModel> resturentModelArrayList) {
        inflater = LayoutInflater.from(ctx);;
        this.resturentModelArrayList = resturentModelArrayList;
        this.ctx = ctx;

    }


    @NonNull
    @Override
    public FavAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_fav_list, parent, false);
        FavAdapter.MyViewHolder holder = new FavAdapter.MyViewHolder(view);

        return holder;


    }

    @Override
    public void onBindViewHolder(@NonNull FavAdapter.MyViewHolder holder, int position) {

        holder.tvResturantname.setText(resturentModelArrayList.get(position).getName());
        holder.tvDistance.setText(resturentModelArrayList.get(position).getDistance());
        holder.tvRatings.setText(resturentModelArrayList.get(position).getRating());
        holder.tvDescription.setText(resturentModelArrayList.get(position).getDescription());

        Glide.with(ctx)
                .load(resturentModelArrayList.get(position).getLogo())
                .placeholder(R.drawable.item)
                .into(holder.itemImg);

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ctx, MainActivity.class);
                intent.putExtra("vendorId", resturentModelArrayList.get(position).getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return resturentModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvResturantname, tvDistance, tvRatings, tvDescription;
        ImageView itemImg;
        LinearLayout btnDetails;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvResturantname = (TextView) itemView.findViewById(R.id.tvResturantname);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            tvRatings = (TextView) itemView.findViewById(R.id.tvRatings);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            itemImg = (ImageView) itemView.findViewById(R.id.itemImg);
            btnDetails = (LinearLayout)itemView.findViewById(R.id.btnDetails);


        }
    }



}
