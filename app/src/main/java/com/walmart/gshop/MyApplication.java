package com.walmart.gshop;

import android.app.Application;
import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

/**
 * Created by yashasvi on 4/2/16.
 */
public class MyApplication extends Application {

    private static final String LOG_TAG = "MyApplication";
    private Pubnub mPubNub;

    @Override
    public void onCreate() {
        super.onCreate();
        initPubNub();
    }

    public Pubnub getmPubNub() {
        if (mPubNub == null)
            initPubNub();
        return mPubNub;
    }

    private void initPubNub() {
        this.mPubNub = new Pubnub(Constants.PUBLISH_KEY, Constants.SUBSCRIBE_KEY);
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

}
