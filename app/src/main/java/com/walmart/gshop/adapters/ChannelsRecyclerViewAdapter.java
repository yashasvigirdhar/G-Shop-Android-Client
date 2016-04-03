package com.walmart.gshop.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.walmart.gshop.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yashasvi on 1/21/16.
 */
public class ChannelsRecyclerViewAdapter extends RecyclerView.Adapter<ChannelsRecyclerViewAdapter.DataObjectHolder> {

    private List<String> mDataset;
    private static MyClickListener myClickListener;
   // private final AdapterView.OnItemClickListener listener;

    public ChannelsRecyclerViewAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
      //  this.listener = listener;
    }


    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel_card, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        holder.tvChannelName.setText(mDataset.get(position));

    }

    public void updateData(List<String> data) {
        mDataset = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public String getItem(int position)
    {
        return mDataset.get(position);
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
