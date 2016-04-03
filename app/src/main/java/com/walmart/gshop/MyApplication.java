package com.walmart.gshop;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.flipkart.chatheads.ui.ChatHeadContainer;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

/**
 * Created by yashasvi on 4/2/16.
 */
public class MyApplication extends Application {

    private static final String LOG_TAG = "MyApplication";
    private static Pubnub mPubNub;


    public static Bitmap screenShareBitmap;

    public static String screenShareLink;

    public static ChatHeadContainer getChatContainer() {
        return chatContainer;
    }

    public static void setChatContainer(ChatHeadContainer chatContainer1) {
        chatContainer = chatContainer1;
    }

    public static ChatHeadContainer chatContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        initPubNub();
    }

    public static Pubnub getmPubNub() {
        if (mPubNub == null)
            initPubNub();
        return mPubNub;
    }

    private static void initPubNub() {
        mPubNub = new Pubnub(Constants.PUBLISH_KEY, Constants.SUBSCRIBE_KEY);
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                Log.i(LOG_TAG, "time api : " + response.toString());
            }

            public void errorCallback(String channel, PubnubError error) {
                Log.i(LOG_TAG, "time api : " + error.toString());
            }
        };
        mPubNub.time(callback);
    }

    public Bitmap getScreenShareBitmap() {
        return screenShareBitmap;
    }

    public void setScreenShareBitmap(Bitmap screenShareBitmap) {
        MyApplication.screenShareBitmap = screenShareBitmap;
    }

    public String getScreenShareLink() {
        return screenShareLink;
    }

    public void setScreenShareLink(String screenShareLink) {
        MyApplication.screenShareLink = screenShareLink;
    }

}
