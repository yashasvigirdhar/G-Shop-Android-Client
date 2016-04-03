package com.walmart.gshop.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.kevingleason.pubnubchat.R;

/**
 * Created by yashasvi on 1/21/16.
 */
public class ChannelsRecyclerViewAdapter extends RecyclerView.Adapter<ChannelsRecyclerViewAdapter.DataObjectHolder> {

    String LOG_TAG = "ChannelsRecyclerViewAdapter";
    private List<String> mDataset;
    private static MyClickListener myClickListener;

    public ChannelsRecyclerViewAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel_card, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.tvChannelName.setText(mDataset.get(position));
    }

    public void updateData(List<String> data) {
        Log.i(LOG_TAG, "update data");
        mDataset = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        ChannelsRecyclerViewAdapter.myClickListener = myClickListener;
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView tvChannelName;

        public DataObjectHolder(View itemView) {
            super(itemView);
            tvChannelName = (TextView) itemView.findViewById(R.id.tvCityName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }


}
