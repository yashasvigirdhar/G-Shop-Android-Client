package com.walmart.gshop.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.walmart.gshop.Constants;
import com.walmart.gshop.MyApplication;
import com.walmart.gshop.R;
import com.walmart.gshop.adapters.ChannelsRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ChannelListActivity extends Fragment implements View.OnClickListener, ChannelsRecyclerViewAdapter.MyClickListener {

    private final static String LOG_TAG = "ChannelListActivity";

    private SharedPreferences mSharedPrefs;

    private Pubnub mPubNub;

    public static String newChannel;


    private RecyclerView mRecyclerView;
    private ChannelsRecyclerViewAdapter mAdapter;

    public static ChannelListActivity newInstance() {
        ChannelListActivity ch = new ChannelListActivity();
        Bundle b = new Bundle();
        b.putString("text", "text");
        ch.setArguments(b);
        return ch;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_list_channels, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPubNub = MyApplication.getmPubNub();

        mSharedPrefs = getContext().getSharedPreferences(Constants.CHAT_PREFS, getActivity().MODE_PRIVATE);


        mPubNub.setUUID(mSharedPrefs.getString(Constants.CHAT_USERNAME, "Anonymous"));
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewChannels);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChannelsRecyclerViewAdapter(new ArrayList<String>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);


        whereNow();

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fabAddChannel);
        fab.setOnClickListener(this);
    }

    public void whereNow() {
        mPubNub.whereNow(mPubNub.getUUID(), new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                try {
                    JSONObject json = (JSONObject) response;
                    Log.i("Where now response", json.toString());
                    Log.i("Where now response", mPubNub.getUUID());
                    final JSONArray channels = json.getJSONArray("channels");
                    Log.d("JSON_RESP", "Where Now: " + json.toString());
                    final List<String> channelList = new ArrayList<>();
                    for (int i = 0; i < channels.length(); i++) {
                        channelList.add(channels.getString(i));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.updateData(channelList);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddChannel:
                LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.channel_change, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

                alertDialogBuilder
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        newChannel = userInput.getText().toString();
                                        if (newChannel.equals("")) return;
                                        ((MyApplication) getActivity().getApplication()).chatContainer.addChatHead(newChannel, false, true);
                                        ((MyApplication) getActivity().getApplication()).chatContainer.selectChatHead(newChannel);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
        }

    }

    @Override
    public void onItemClick(int position, View v) {
        if (((MyApplication) getActivity().getApplication()).chatContainer.removeChatHead(mAdapter.getItem(position), true))
            ;
        ((MyApplication) getActivity().getApplication()).chatContainer.addChatHead(mAdapter.getItem(position), false, true);
        ((MyApplication) getActivity().getApplication()).chatContainer.selectChatHead(mAdapter.getItem(position));
    }
}
