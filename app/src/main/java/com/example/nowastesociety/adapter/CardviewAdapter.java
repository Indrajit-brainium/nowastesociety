package com.example.nowastesociety.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nowastesociety.R;
import com.example.nowastesociety.model.AddressviewModel;
import com.example.nowastesociety.model.CardModel;

import java.util.ArrayList;

public class CardviewAdapter extends RecyclerView.Adapter<CardviewAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<CardModel> cardviewAdapterArrayList;
    Context ctx;
    private int lastSelectedPosition = -1;


    public CardviewAdapter(Context ctx, ArrayList<CardModel> cardviewAdapterArrayList){

        inflater = LayoutInflater.from(ctx);
        this.cardviewAdapterArrayList = cardviewAdapterArrayList;
        this.ctx = ctx;

    }

    @NonNull
    @Override
    public CardviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.rv_carddetails, parent, false);
        CardviewAdapter.MyViewHolder holder = new CardviewAdapter.MyViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardviewAdapter.MyViewHolder holder, int position) {

        holder.tvCardNumber.setText(cardviewAdapterArrayList.get(position).getCardNumber());
        holder.tvCardName.setText(cardviewAdapterArrayList.get(position).getNameOnCard());
        holder.radioButton.setChecked(lastSelectedPosition == position);



    }

    @Override
    public int getItemCount() {
        return cardviewAdapterArrayList.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCardNumber, tvCardName;
        ImageView imgCard;
        RadioButton radioButton;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCardNumber = (TextView)itemView.findViewById(R.id.tvCardNumber);
            tvCardName = (TextView)itemView.findViewById(R.id.tvCardName);
            radioButton = (RadioButton)itemView.findViewById(R.id.radioButton);

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    lastSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });

        }
    }


}
