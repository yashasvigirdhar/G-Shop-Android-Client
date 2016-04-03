package com.walmart.gshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.walmart.gshop.activities.OrderActivity;
import com.walmart.gshop.activities.WalletActivity;

import com.walmart.gshop.R;

import java.util.List;

/**
 * Created by schatu2 on 4/3/16.
 */
public class AccAdapter extends RecyclerView.Adapter<AccAdapter.AccountViewHolder> {

    public static class AccountViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView accountItem;

        AccountViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            accountItem = (TextView)itemView.findViewById(R.id.account_item);
        }
    }

    List<String> items;
    Context context;

    public AccAdapter(List<String> items, Context context){
        this.items = items;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account, viewGroup, false);
        AccountViewHolder pvh = new AccountViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(AccountViewHolder accountViewHolder, int i) {
        accountViewHolder.accountItem.setText(items.get(i));
        if(items.get(i).equalsIgnoreCase("orders")){
            accountViewHolder.accountItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OrderActivity.class);
                    context.startActivity(intent);
                }
            });
        }
        else if(items.get(i).equalsIgnoreCase("wallet")){
            accountViewHolder.accountItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WalletActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}