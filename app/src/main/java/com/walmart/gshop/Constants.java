package com.walmart.gshop;

/**
 * Created by GleasonK on 6/8/15.
 * <p/>
 * Constants used by this chatting application.
 * TODO: Replace PUBLISH_KEY and SUBSCRIBE_KEY with your personal keys.
 * TODO: Register app for GCM and replace GCM_SENDER_ID
 */
public class Constants {
    public static final String PUBLISH_KEY = "pub-c-69ab3b55-875d-4fc7-be5a-c5143f0b01da";
    public static final String SUBSCRIBE_KEY = "sub-c-404cb59e-f7f9-11e5-861b-02ee2ddab7fe";

    public static String username = "yash";

    public static final String CHAT_PREFS = "com.walmart.gshop.SHARED_PREFS";
    public static final String CHAT_USERNAME = "com.walmart.gshop.SHARED_PREFS.USERNAME";
    public static final String CHAT_ROOM = "com.walmart.gshop.CHAT_ROOM";

    public static final String JSON_GROUP = "groupMessage";
    public static final String JSON_DM = "directMessage";
    public static final String JSON_USER = "chatUser";
    public static final String JSON_MSG = "chatMsg";
    public static final String JSON_TIME = "chatTime";

    public static final String STATE_LOGIN = "loginTime";

    public static final String GCM_REG_ID = "gcmRegId";
    public static final String GCM_SENDER_ID = "623474712154"; // Get this from
    public static final String GCM_POKE_FROM = "gcmPokeFrom"; // Get this from
    public static final String GCM_CHAT_ROOM = "gcmChatRoom"; // Get this from
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

}
