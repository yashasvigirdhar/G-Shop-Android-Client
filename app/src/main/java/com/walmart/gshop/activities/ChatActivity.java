package com.walmart.gshop.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pubnub.api.Callback;
import com.pubnub.api.PnGcmMessage;
import com.pubnub.api.PnMessage;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.walmart.gshop.Constants;
import com.walmart.gshop.MyApplication;
import com.walmart.gshop.ProductList;
import com.walmart.gshop.R;
import com.walmart.gshop.adapters.ChatRecyclerViewAdapter;
import com.walmart.gshop.callbacks.BasicCallback;
import com.walmart.gshop.models.ChatMessage;
import com.walmart.gshop.models.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main Activity is where all the magic happens. To keep this demo simple I did not use fragment
 * views, simply a ListView that is populated by a custom adapter, ChatAdapter. If you want to
 * make this chat app your own, go to http://www.pubnub.com/get-started/ and replace the Pub/Sub
 * keys found in Constants.java, then be sure to enable Storage & Playback, Presence, and Push.
 * For all features to work, you will also need to Register for GCM Messaging and update your
 * sender ID as well.
 * Sample data to test from console:
 * {"type":"groupMessage","data":{"chatUser":"Dev","chatMsg":"Hello World!","chatTime":1436642192966}}
 */
public class ChatActivity extends Fragment implements View.OnClickListener, ChatRecyclerViewAdapter.ChatMessageClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;


    private Pubnub mPubNub;
    private EditText mMessageET;
    private MenuItem mHereNow;
    private ListView mListView;
    Toolbar mToolbar;
    private SharedPreferences mSharedPrefs;

    private RecyclerView mRecyclerView;
    private ChatRecyclerViewAdapter mAdapter;

    private String username;
    private String channel = "MainChat";

    private GoogleCloudMessaging gcm;
    private String gcmRegId;

    ImageButton ibSendMedia;

    AlertDialog.Builder dialogBuilder;
    AlertDialog sendMediaDialog;

    public static ChatActivity newInstance(String channel) {
        ChatActivity ch = new ChatActivity();
        Bundle bundle = new Bundle();
        bundle.putString("channel", channel);
        ch.setArguments(bundle);
        return ch;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_chat, container, false);
        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mSharedPrefs = getContext().getSharedPreferences(Constants.CHAT_PREFS, getActivity().MODE_PRIVATE);

        mPubNub = MyApplication.getmPubNub();


        this.channel = getArguments().getString("channel");

        this.username = mSharedPrefs.getString(Constants.CHAT_USERNAME, "Anonymous");

        this.mMessageET = (EditText) getView().findViewById(R.id.message_et);

        getView().findViewById(R.id.bSendMessage).setOnClickListener(this);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewChat);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ChatRecyclerViewAdapter(getContext(), new ArrayList<ChatMessage>());
        mAdapter.userPresence(this.username, "join"); // Set user to online. Status changes handled in presence
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setChatMessageClickListener(this);

        mMessageET = (EditText) getView().findViewById(R.id.message_et);

        mToolbar = (Toolbar) getView().findViewById(R.id.toolbarChat);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(channel);

        ibSendMedia = (ImageButton) getView().findViewById(R.id.ibSendMedia);
        ibSendMedia.setOnClickListener(this);

        dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.send_media_dialog, null);
        dialogBuilder.setView(dialogView);
        sendMediaDialog = dialogBuilder.create();
        dialogView.findViewById(R.id.tvScreenShare).setOnClickListener(this);
        dialogView.findViewById(R.id.tvCameraShare).setOnClickListener(this);

        initPubNub();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO: Update to store messages in the array.
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        this.mHereNow = menu.findItem(R.id.action_here_now);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_main clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_here_now:
                hereNow(true);
                return true;
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_gcm_register:
                gcmRegister();
                return true;
            case R.id.action_gcm_unregister:
                gcmUnregister();
                return true;
            case R.id.action_where_now:
                whereNow();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Might want to unsubscribe from PubNub here and create background service to listen while
     * app is not in foreground.
     * PubNub will stop subscribing when screen is turned off for this demo, messages will be loaded
     * when app is opened through a call to history.
     * The best practice would be creating a background service in onStop to handle messages.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (this.mPubNub != null)
            this.mPubNub.unsubscribeAll();
    }

    /**
     * Instantiate PubNub object if it is null. Subscribe to channel and pull old messages via
     * history.
     */

    protected void onRestart() {

        if (this.mPubNub == null) {
            initPubNub();
        } else {
            subscribeChannelWithPresence();
            history();
        }
    }

    /**
     * I remove the PubNub object in onDestroy since turning the screen off triggers onStop and
     * I wanted PubNub to receive messages while the screen is off.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Instantiate PubNub object with username as UUID
     * Then subscribe to the current channel with presence.
     * Finally, populate the listview with past messages from history
     */
    private void initPubNub() {
        subscribeChannelWithPresence();
        history();
        gcmRegister();
    }

    /**
     * Use PubNub to send any sort of data
     *
     * @param type The type of the data, used to differentiate groupMessage from directMessage
     * @param data The payload of the publish
     */
    public void publish(String type, JSONObject data) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.mPubNub.publish(this.channel, json, new BasicCallback());
    }


    /**
     * Update here now number, uses a call to the pubnub hereNow function.
     *
     * @param displayUsers If true, display a modal of users in room.
     */
    public void hereNow(final boolean displayUsers) {
        this.mPubNub.hereNow(this.channel, new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                try {
                    JSONObject json = (JSONObject) response;
                    final int occ = json.getInt("occupancy");
                    final JSONArray hereNowJSON = json.getJSONArray("uuids");
                    Log.d("JSON_RESP", "Here Now: " + json.toString());
                    final Set<String> usersOnline = new HashSet<String>();
                    usersOnline.add(username);
                    for (int i = 0; i < hereNowJSON.length(); i++) {
                        usersOnline.add(hereNowJSON.getString(i));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mHereNow != null)
                                mHereNow.setTitle(String.valueOf(occ));
                            mAdapter.setOnlineNow(usersOnline);
                            if (displayUsers)
                                alertHereNow(usersOnline, "Here now");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void whereNow() {
        this.mPubNub.whereNow(this.mPubNub.getUUID(), new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                try {
                    JSONObject json = (JSONObject) response;
                    Log.i("Where now response", json.toString());
                    final JSONArray channels = json.getJSONArray("channels");
                    Log.d("JSON_RESP", "Where Now: " + json.toString());
                    final Set<String> usersOnline = new HashSet<>();
                    for (int i = 0; i < channels.length(); i++) {
                        usersOnline.add(channels.getString(i));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertHereNow(usersOnline, "Where now");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Called at login time, sets meta-data of users' log-in times using the PubNub State API.
     * Information is retrieved in getStateLogin
     */
    public void setStateLogin() {
        Callback callback = new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                Log.d("PUBNUB", "State: " + response.toString());
            }
        };
        try {
            JSONObject state = new JSONObject();
            state.put(Constants.STATE_LOGIN, System.currentTimeMillis());
            this.mPubNub.setState(this.channel, this.mPubNub.getUUID(), state, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get state information. Information is deleted when user unsubscribes from channel
     * so display a user not online message if there is no UUID data attached to the
     * channel's state
     *
     * @param user
     */
    public void getStateLogin(final String user) {
        Callback callback = new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                if (!(response instanceof JSONObject)) return; // Ignore if not JSON
                try {
                    JSONObject state = (JSONObject) response;
                    final boolean online = state.has(Constants.STATE_LOGIN);
                    final long loginTime = online ? state.getLong(Constants.STATE_LOGIN) : 0;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!online)
                                Toast.makeText(getContext(), user + " is not online.", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getContext(), user + " logged in since " + ChatRecyclerViewAdapter.formatTimeStamp(loginTime), Toast.LENGTH_SHORT).show();

                        }
                    });

                    Log.d("PUBNUB", "State: " + response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        this.mPubNub.getState(this.channel, user, callback);
    }

    /**
     * Subscribe to channel, when subscribe connection is established, in connectCallback, subscribe
     * to presence, set login time with setStateLogin and update hereNow information.
     * When a message is received, in successCallback, get the ChatMessage information from the
     * received JSONObject and finally put it into the listview's ChatAdapter.
     * Chat adapter calls notifyDatasetChanged() which updates UI, meaning must run on UI thread.
     */
    public void subscribeChannelWithPresence() {
        Callback subscribeCallback = new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                if (message instanceof JSONObject) {
                    try {
                        JSONObject jsonObj = (JSONObject) message;
                        JSONObject json = jsonObj.getJSONObject("data");
                        String name = json.getString(Constants.JSON_USER);
                        if (name.equals(mPubNub.getUUID())) return; // Ignore own messages

                        String msg = json.getString(Constants.JSON_MSG);
                        long time = json.getLong(Constants.JSON_TIME);

                        int isImage = json.getInt(Constants.JSON_IS_IMAGE);
                        final ChatMessage chatMsg;

                        if (isImage == 0) {
                            chatMsg = new ChatMessage(name, msg, time, null);
                        } else {
                            String encodedImage = json.getString(Constants.JSON_IMAGE_URI);
                            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                            Bitmap bt = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            chatMsg = new ChatMessage(name, msg, time, bt);
                            chatMsg.setImageUri(encodedImage);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.addMessage(chatMsg);
                                mRecyclerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //call smooth scroll
                                        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                                    }
                                });
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("PUBNUB", "Channel: " + channel + " Msg: " + message.toString());
            }

            @Override
            public void connectCallback(String channel, Object message) {
                Log.d("Subscribe", "Connected! " + message.toString());
                hereNow(false);
                setStateLogin();
            }
        };
        try {
            mPubNub.subscribe(this.channel, subscribeCallback);
            presenceSubscribe();
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to presence. When user join or leave are detected, update the hereNow number
     * as well as add/remove current user from the chat adapter's userPresence array.
     * This array is used to see what users are currently online and display a green dot next
     * to users who are online.
     */
    public void presenceSubscribe() {
        Callback callback = new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                Log.i("PN-pres", "Pres: " + response.toString() + " class: " + response.getClass().toString());
                if (response instanceof JSONObject) {
                    JSONObject json = (JSONObject) response;
                    Log.d("PN-main", "Presence: " + json.toString());
                    try {
                        final int occ = json.getInt("occupancy");
                        final String user = json.getString("uuid");
                        final String action = json.getString("action");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.userPresence(user, action);
                                if (mHereNow != null)
                                    mHereNow.setTitle(String.valueOf(occ));
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.d("Presence", "Error: " + error.toString());
            }
        };
        try {
            this.mPubNub.presence(this.channel, callback);
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get last 100 messages sent on current channel from history.
     */
    public void history() {
        mPubNub.history(channel, 100, false, new Callback() {
                    @Override
                    public void successCallback(String channel, final Object message) {
                        try {
                            JSONArray json = (JSONArray) message;
                            Log.d("History", json.toString());
                            final JSONArray messages = json.getJSONArray(0);
                            final List<ChatMessage> chatMsgs = new ArrayList<>();
                            for (int i = 0; i < messages.length(); i++) {
                                try {
                                    if (!messages.getJSONObject(i).has("data")) continue;
                                    JSONObject jsonMsg = messages.getJSONObject(i).getJSONObject("data");
                                    String name = jsonMsg.getString(Constants.JSON_USER);
                                    String msg = jsonMsg.getString(Constants.JSON_MSG);
                                    long time = jsonMsg.getLong(Constants.JSON_TIME);

                                    //check here if msg is text or image

                                    int isImage = jsonMsg.getInt(Constants.JSON_IS_IMAGE);

                                    final ChatMessage chatMsg;

                                    if (isImage == 0) {
                                        chatMsg = new ChatMessage(name, msg, time, null);
                                    } else {
                                        String encodedImage = jsonMsg.getString(Constants.JSON_IMAGE_URI);
                                        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

                                        Bitmap bt = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        chatMsg = new ChatMessage(name, msg, time, bt);
                                        chatMsg.setImageUri(encodedImage);
                                    }

                                    chatMsgs.add(chatMsg);
                                } catch (JSONException e) { // Handle errors silently
                                    e.printStackTrace();
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "RUNNIN", Toast.LENGTH_SHORT).show();
                                        mAdapter.setMessages(chatMsgs);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void errorCallback(String channel, PubnubError error) {
                        Log.d("History", error.toString());
                    }
                }

        );
    }


    /**
     * Log out, remove username from SharedPreferences, unsubscribe from PubNub, and send user back
     * to the LoginActivity
     */

    public void signOut() {
        this.mPubNub.unsubscribeAll();
        SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.remove(Constants.CHAT_USERNAME);
        edit.apply();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.putExtra("oldUsername", this.username);
        startActivity(intent);
    }

    /**
     * Setup the listview to scroll to bottom anytime it receives a message.
     */


    /**
     * Publish message to current channel.
     */
    public void sendMessage(int isImage, String message, Bitmap image) {

        ChatMessage chatMsg = new ChatMessage(username, message, System.currentTimeMillis(), image);
        try {
            JSONObject json = new JSONObject();
            json.put(Constants.JSON_USER, chatMsg.getUsername());
            json.put(Constants.JSON_MSG, chatMsg.getMessage());
            json.put(Constants.JSON_TIME, chatMsg.getTimeStamp());
            if (isImage == 1) {
                String encodedImage = convertAndCompressImage(image);
                json.put(Constants.JSON_IMAGE_URI, encodedImage);
            }
            json.put(Constants.JSON_IS_IMAGE, isImage);
            publish(Constants.JSON_GROUP, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter.addMessage(chatMsg);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                //call smooth scroll
                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
            }
        });
    }

    /**
     * Create an alert dialog with a list of users who are here now.
     * When a user's name is clicked, get their state information and display it with Toast.
     *
     * @param userSet
     */
    private void alertHereNow(Set<String> userSet, String title) {
        List<String> users = new ArrayList<>(userSet);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(title);
        alertDialog.setNegativeButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final ArrayAdapter<String> hnAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, users);
        alertDialog.setAdapter(hnAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                String user = hnAdapter.getItem(position);
                getStateLogin(user);
            }
        });
        alertDialog.show();
    }

    /**
     * GCM Functionality.
     * In order to use GCM Push notifications you need an API key and a Sender ID.
     * Get your key and ID at - https://developers.google.com/cloud-messaging/
     */

    private void gcmRegister() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(getContext());
            try {
                gcmRegId = getRegistrationId();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (gcmRegId.isEmpty()) {
                registerInBackground();
            } else {
                Toast.makeText(getContext(), "Registration ID already exists: " + gcmRegId, Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("GCM-register", "No valid Google Play Services APK found.");
        }
    }

    private void gcmUnregister() {
        new UnregisterTask().execute();
    }

    private void removeRegistrationId() {
        SharedPreferences prefs = getContext().getSharedPreferences(Constants.CHAT_PREFS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.GCM_REG_ID);
        editor.apply();
    }

    public void sendNotification(String toUser) {
        PnGcmMessage gcmMessage = new PnGcmMessage();
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.GCM_POKE_FROM, this.username);
            json.put(Constants.GCM_CHAT_ROOM, this.channel);
            gcmMessage.setData(json);


            PnMessage message = new PnMessage(
                    this.mPubNub,
                    toUser,
                    new BasicCallback(),
                    gcmMessage);
            message.put("pn_debug", true); // Subscribe to yourchannel-pndebug on console for reports
            message.publish();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e("GCM-check", "This device is not supported.");
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {
        new RegisterTask().execute();
    }

    private void storeRegistrationId(String regId) {
        SharedPreferences prefs = getContext().getSharedPreferences(Constants.CHAT_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.GCM_REG_ID, regId);
        editor.apply();
    }


    private String getRegistrationId() {
        SharedPreferences prefs = getContext().getSharedPreferences(Constants.CHAT_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(Constants.GCM_REG_ID, "");
    }

    private void sendRegistrationId(String regId) {
        this.mPubNub.enablePushNotificationsOnChannel(this.username, regId, new BasicCallback());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bSendMessage:
                String message = mMessageET.getText().toString();
                if (message.equals("")) return;
                mMessageET.setText("");
                sendMessage(0, message, null);
                break;
            case R.id.ibSendMedia:
                sendMediaDialog.show();
                break;
            case R.id.tvScreenShare:
                if (sendMediaDialog.isShowing())
                    sendMediaDialog.dismiss();
                takeScreenshotAndSend();
                break;
            case R.id.tvCameraShare:
                if (sendMediaDialog.isShowing())
                    sendMediaDialog.dismiss();
                dispatchTakePictureIntent();
        }
    }

    private void takeScreenshotAndSend() {

        sendMessage(1, ((MyApplication) getActivity().getApplication()).getScreenShareLink(), ((MyApplication) getActivity().getApplication()).getScreenShareBitmap());

    }

    public String convertAndCompressImage(Bitmap image) {

        int MAX_IMAGE_SIZE = 100 * 100;
        int streamLength = MAX_IMAGE_SIZE;
        int compressQuality = 105;
        ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
        while (streamLength >= MAX_IMAGE_SIZE && compressQuality > 5) {
            try {
                bmpStream.flush();//to avoid out of memory error
                bmpStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            compressQuality -= 5;
            image.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            Log.d("image processing", "Quality: " + compressQuality);
            Log.d("image processing", "Size: " + streamLength);
        }

        try {
            bmpStream.flush();//to avoid out of memory error
            bmpStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

        image.compress(Bitmap.CompressFormat.JPEG, 1, bmpStream); //bm is the bitmap object
        byte[] b = bmpStream.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encodedImage;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            sendMessage(1, "dummy", imageBitmap);
        }
    }

    @Override
    public void onItemClick(int position, View v) {
        ChatMessage msg = mAdapter.getItem(position);
        if (msg.isImage()) {
            String link = msg.getMessage();
            if (link.equals(Constants.PRODUCTS_LIST)) {
                Intent i = new Intent(getContext(), ProductListActivity.class);
                startActivity(i);
            } else {
                //get product
                Product product = ProductList.getProductByName(link);
                Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                intent.putExtra("productName", product.name);
                intent.putExtra("productDescription", product.description);
                intent.putExtra("productPrice", product.price);
                startActivity(intent);
            }
        }
    }

    private class RegisterTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(getContext());
                }
                gcmRegId = gcm.register(Constants.GCM_SENDER_ID);
                msg = "Device registered, registration ID: " + gcmRegId;

                sendRegistrationId(gcmRegId);

                storeRegistrationId(gcmRegId);
                Log.i("GCM-register", msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return msg;
        }
    }

    private class UnregisterTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(getContext());
                }

                // Unregister from GCM
                gcm.unregister();

                // Remove Registration ID from memory
                removeRegistrationId();

                // Disable Push Notification
                mPubNub.disablePushNotificationsOnChannel(username, gcmRegId);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
