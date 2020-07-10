package com.example.nowastesociety.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nowastesociety.Cartdetails;
import com.example.nowastesociety.MainActivity;
import com.example.nowastesociety.R;
import com.example.nowastesociety.model.CartitemModel;
import com.example.nowastesociety.model.ResturantDetailsModel;

import java.util.ArrayList;

public class CartitemAdapter extends RecyclerView.Adapter<CartitemAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<CartitemModel> cartitemModelArrayList;
    private Context ctx;

    public CartitemAdapter (Context ctx, ArrayList<CartitemModel> cartitemModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.cartitemModelArrayList = cartitemModelArrayList;
        this.ctx = ctx;


    }


    @NonNull
    @Override
    public CartitemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.rv_cartitems, parent, false);
        CartitemAdapter.MyViewHolder holder = new CartitemAdapter.MyViewHolder(view);



        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull CartitemAdapter.MyViewHolder holder, int position) {

        holder.tvItemname.setText(cartitemModelArrayList.get(position).getItemname());
        holder.tvItemprice.setText(cartitemModelArrayList.get(position).getItemprice());
        holder.tvQuantity.setText(cartitemModelArrayList.get(position).getQuantity()+"");

        Glide.with(ctx)
                .load(cartitemModelArrayList.get(position).getItemImg())
                .placeholder(R.drawable.item)
                .into(holder.itemImg);


        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                ((Cartdetails) ctx).removeToCart(cartitemModelArrayList.get(position));



            }
        });

        holder.ivDecrese.setOnClickListener(view -> {

            int val = cartitemModelArrayList.get(position).getQuantity() - 1;
            if(val==0){
                ((Cartdetails)ctx).deleteItem(cartitemModelArrayList.get(position));
            }
            if (val > -1) {
                holder.tvQuantity.setText("" + val);
                cartitemModelArrayList.get(position).setQuantity(val);
            }
            if(val>0){
                ((Cartdetails)ctx).updateCart(cartitemModelArrayList.get(position));
            }


        });

        holder.ivIncrease.setOnClickListener(view -> {

            int val = cartitemModelArrayList.get(position).getQuantity() + 1;
            if (val > -1) {
                holder.tvQuantity.setText("" + val);
                cartitemModelArrayList.get(position).setQuantity(val);
            }

            if(val>0){
                ((Cartdetails)ctx).updateCart(cartitemModelArrayList.get(position));
            }
        });

//        holder.tvDescription.setText(cartitemModelArrayList.get(position).getTvDescription());



    }

    @Override
    public int getItemCount() {
        return cartitemModelArrayList.size();
    }




    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemname, tvItemprice, tvDescription, tvQuantity;
        ImageView itemImg;
        LinearLayoutCompat btnRemove;
        ImageView ivDecrese;
        ImageView ivIncrease;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemname = (TextView)itemView.findViewById(R.id.tvItemname);
            tvItemprice = (TextView)itemView.findViewById(R.id.tvItemprice);
            tvDescription = (TextView)itemView.findViewById(R.id.tvDescription);
            tvQuantity = (TextView)itemView.findViewById(R.id.tvQuantity);
            ivDecrese = itemView.findViewById(R.id.ivDecrese);
            ivIncrease = itemView.findViewById(R.id.ivIncrease);
            itemImg = (ImageView)itemView.findViewById(R.id.itemImg);
            btnRemove = (LinearLayoutCompat)itemView.findViewById(R.id.btnRemove);

        }
    }




}
