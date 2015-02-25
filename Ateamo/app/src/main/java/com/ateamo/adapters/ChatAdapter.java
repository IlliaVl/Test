package com.ateamo.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ateamo.ateamo.R;
import com.ateamo.core.Member;
import com.ateamo.core.QBHelper;
import com.ateamo.utils.TimeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.users.model.QBUser;

import java.util.List;

public class ChatAdapter extends BaseAdapter {

    private final List<QBChatMessage> chatMessages;
    private Activity context;

    public ChatAdapter(Activity context, List<QBChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public QBChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        QBChatMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.list_item_message, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QBUser currentUser = QBHelper.getSharedInstance().getCurrentUser();
        boolean isOutgoing = chatMessage.getSenderId() == null || chatMessage.getSenderId().equals(currentUser.getId());
        setAlignment(holder, isOutgoing);
        holder.messageTextView.setText(chatMessage.getBody());
        holder.dateTextView.setText(getTimeText(chatMessage));
        holder.nameTextView.setText(chatMessage.getSenderId().toString());
        ImageLoader.getInstance().displayImage(Member.getCurrent().getProfilePictureURL(), holder.iconImageView);
        return convertView;
    }

    public void add(QBChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<QBChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isOutgoing) {
        holder.itemContentLinearLayout.removeAllViews();
        if (isOutgoing) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.itemContentLinearLayout.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.itemContentLinearLayout.setLayoutParams(lp);

            holder.messageLinearLayout.setBackgroundResource(R.drawable.right_bubble_bg);
            holder.itemContentLinearLayout.addView(holder.messageDateLinearLayout);
            holder.itemContentLinearLayout.addView(holder.iconNameLinearLayout);
        } else {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.itemContentLinearLayout.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.itemContentLinearLayout.setLayoutParams(lp);

            holder.messageLinearLayout.setBackgroundResource(R.drawable.left_bubble_bg);
            holder.itemContentLinearLayout.addView(holder.iconNameLinearLayout);
            holder.itemContentLinearLayout.addView(holder.messageDateLinearLayout);
        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.itemContentRelativeLayout = (RelativeLayout) v.findViewById(R.id.itemContentRelativeLayout);
        holder.messageTextView = (TextView) v.findViewById(R.id.messageTextView);
        holder.itemContentLinearLayout = (LinearLayout) v.findViewById(R.id.content);
        holder.messageLinearLayout = (LinearLayout) v.findViewById(R.id.messageLinearLayout);
        holder.iconNameLinearLayout = (LinearLayout) v.findViewById(R.id.iconNameLinearLayout);
        holder.messageDateLinearLayout = (LinearLayout) v.findViewById(R.id.messageDateLinearLayout);
        holder.dateTextView = (TextView) v.findViewById(R.id.dateTextView);
        holder.nameTextView = (TextView) v.findViewById(R.id.nameTextView);
        holder.iconImageView = (ImageView) v.findViewById(R.id.iconImageView);
        return holder;
    }

    private String getTimeText(QBChatMessage message) {
        return TimeUtils.millisToLongDHMS(message.getDateSent() * 1000);
    }

    private static class ViewHolder {
        public ImageView iconImageView;
        public TextView messageTextView;
        public TextView dateTextView;
        public TextView nameTextView;
        public LinearLayout itemContentLinearLayout;
        public LinearLayout messageLinearLayout;
        public LinearLayout iconNameLinearLayout;
        public LinearLayout messageDateLinearLayout;
        public RelativeLayout itemContentRelativeLayout;
    }
}
