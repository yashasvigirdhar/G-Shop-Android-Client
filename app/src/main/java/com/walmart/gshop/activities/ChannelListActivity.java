package com.walmart.gshop.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.walmart.gshop.Constants;
import com.walmart.gshop.MyApplication;
import com.walmart.gshop.adapters.ChannelsRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.kevingleason.pubnubchat.R;


public class ChannelListActivity extends AppCompatActivity implements View.OnClickListener, ChannelsRecyclerViewAdapter.MyClickListener {

    private final static String LOG_TAG = "ChannelListActivity";

    private SharedPreferences mSharedPrefs;

    private Pubnub mPubNub;

    CoordinatorLayout coordinatorLayout;

    private RecyclerView mRecyclerView;
    private ChannelsRecyclerViewAdapter mAdapter;


    List<String> channelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_channels);

        mPubNub = ((MyApplication) getApplication()).getmPubNub();

        mSharedPrefs = getSharedPreferences(Constants.CHAT_PREFS, MODE_PRIVATE);
        if (!mSharedPrefs.contains(Constants.CHAT_USERNAME)) {
            Intent toLogin = new Intent(this, LoginActivity.class);
            startActivity(toLogin);
            return;
        }
        mPubNub.setUUID(mSharedPrefs.getString(Constants.CHAT_USERNAME, "Anonymous"));
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewChannels);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChannelsRecyclerViewAdapter(new ArrayList<String>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        channelList = new ArrayList<>();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarChannelList);
        setSupportActionBar(mToolbar);
        whereNow();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddChannel);
        fab.setOnClickListener(this);
    }

    public void whereNow() {
        mPubNub.whereNow(mPubNub.getUUID(), new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                try {
                    JSONObject json = (JSONObject) response;
                    Log.i("Where now response", json.toString());
                    final JSONArray channels = json.getJSONArray("channels");
                    Log.d("JSON_RESP", "Where Now: " + json.toString());
                    channelList.clear();
                    for (int i = 0; i < channels.length(); i++) {
                        channelList.add(channels.getString(i));
                    }
                    ChannelListActivity.this.runOnUiThread(new Runnable() {
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        whereNow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddChannel:
                LayoutInflater li = LayoutInflater.from(this);
                View promptsView = li.inflate(R.layout.channel_change, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

                alertDialogBuilder
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String newChannel = userInput.getText().toString();
                                        if (newChannel.equals("")) return;
                                        Intent i = new Intent(getBaseContext(), ChatActivity.class);
                                        i.putExtra(Constants.CHAT_ROOM, newChannel);
                                        startActivity(i);
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
        Intent i = new Intent(getBaseContext(), ChatActivity.class);
        i.putExtra(Constants.CHAT_ROOM, channelList.get(position));
        startActivity(i);
    }
}
