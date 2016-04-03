package com.walmart.gshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.walmart.gshop.models.ChatMessage;
import com.walmart.gshop.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



/**
 * Created by yashasvi on 1/21/16.
 */
public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static ChatMessageClickListener chatMessageClickListener;
    private Context context;
    private LayoutInflater inflater;
    private List<ChatMessage> values;
    private Set<String> onlineNow = new HashSet<>();

    public ChatRecyclerViewAdapter(Context context, List<ChatMessage> values) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.values = values;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case 1:
                view = inflater.inflate(R.layout.chat_text_row, parent, false);
                viewHolder = new TextDataObjectHolder(view);
                break;
            case 2:
                view = inflater.inflate(R.layout.chat_image_row, parent, false);
                viewHolder = new ImageDataObjectHolder(view);
                break;

        }
        return viewHolder;
    }

    public void setChatMessageClickListener(ChatMessageClickListener chatMessageClickListener) {
        this.chatMessageClickListener = chatMessageClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (values.get(position).isImage())
            return 2;
        else
            return 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMsg = values.get(position);

        switch (holder.getItemViewType()) {
            case 1:
                TextDataObjectHolder viewHolder1 = (TextDataObjectHolder) holder;
                viewHolder1.user.setText(chatMsg.getUsername());
                viewHolder1.message.setText(chatMsg.getMessage());
                viewHolder1.timeStamp.setText(formatTimeStamp(chatMsg.getTimeStamp()));
                viewHolder1.chatMsg = chatMsg;
                viewHolder1.userPresence.setBackgroundDrawable( // If online show the green presence dot
                        this.onlineNow.contains(chatMsg.getUsername())
                                ? context.getResources().getDrawable(R.drawable.online_circle)
                                : null);

                break;
            case 2:
                ImageDataObjectHolder viewHolder2 = (ImageDataObjectHolder) holder;
                viewHolder2.user.setText(chatMsg.getUsername());
                viewHolder2.message.setText(chatMsg.getMessage());
                viewHolder2.image.setImageBitmap(chatMsg.getImage());
                viewHolder2.timeStamp.setText(formatTimeStamp(chatMsg.getTimeStamp()));
                viewHolder2.chatMsg = chatMsg;
                viewHolder2.userPresence.setBackgroundDrawable( // If online show the green presence dot
                        this.onlineNow.contains(chatMsg.getUsername())
                                ? context.getResources().getDrawable(R.drawable.online_circle)
                                : null);

                break;
        }


    }

    /**
     * Format the long System.currentTimeMillis() to a better looking timestamp. Uses a calendar
     * object to format with the user's current time zone.
     *
     * @param timeStamp
     * @return
     */
    public static String formatTimeStamp(long timeStamp) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return formatter.format(calendar.getTime());
    }


    @Override
    public int getItemCount() {
        return values.size();
    }

    public ChatMessage getItem(int position) {
        return values.get(position);
    }

    public static class TextDataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView user;
        TextView message;
        TextView timeStamp;
        View userPresence;

        ChatMessage chatMsg;

        public TextDataObjectHolder(View convertView) {
            super(convertView);
            user = (TextView) convertView.findViewById(R.id.chat_user);
            message = (TextView) convertView.findViewById(R.id.chat_message);
            timeStamp = (TextView) convertView.findViewById(R.id.chat_time);
            userPresence = convertView.findViewById(R.id.user_presence);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            chatMessageClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public static class ImageDataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView user;
        TextView message;
        ImageView image;
        TextView timeStamp;
        View userPresence;

        ChatMessage chatMsg;

        public ImageDataObjectHolder(View convertView) {
            super(convertView);
            user = (TextView) convertView.findViewById(R.id.chat_user);
            message = (TextView) convertView.findViewById(R.id.chat_message);
            image = (ImageView) convertView.findViewById(R.id.ivChatMessage);
            timeStamp = (TextView) convertView.findViewById(R.id.chat_time);
            userPresence = convertView.findViewById(R.id.user_presence);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (chatMessageClickListener != null)
                chatMessageClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public interface ChatMessageClickListener {
        void onItemClick(int position, View v);
    }

    /**
     * Method to add a single message and update the listview.
     *
     * @param chatMsg Message to be added
     */
    public void addMessage(ChatMessage chatMsg) {
        this.values.add(chatMsg);
        notifyDataSetChanged();
    }

    /**
     * Method to add a list of messages and update the listview.
     *
     * @param chatMsgs Messages to be added
     */
    public void setMessages(List<ChatMessage> chatMsgs) {
        this.values.clear();
        this.values.addAll(chatMsgs);
        notifyDataSetChanged();
    }

    /**
     * Handle users. Fill the onlineNow set with current users. Data is used to display a green dot
     * next to users who are currently online.
     *
     * @param user   UUID of the user online.
     * @param action The presence action
     */
    public void userPresence(String user, String action) {
        boolean isOnline = action.equals("join") || action.equals("state-change");
        if (!isOnline && this.onlineNow.contains(user))
            this.onlineNow.remove(user);
        else if (isOnline && !this.onlineNow.contains(user))
            this.onlineNow.add(user);

        notifyDataSetChanged();
    }

    /**
     * Overwrite the onlineNow array with all the values attained from a call to hereNow().
     *
     * @param onlineNow
     */
    public void setOnlineNow(Set<String> onlineNow) {
        this.onlineNow = onlineNow;
        notifyDataSetChanged();
    }

    /**
     * Clear all values from the values array and update the listview. Used when changing rooms.
     */
    public void clearMessages() {
        this.values.clear();
        notifyDataSetChanged();
    }

}