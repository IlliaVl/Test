package com.ateamo.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ateamo.UI.MainActivity;
import com.ateamo.ateamo.R;
import com.ateamo.core.Member;
import com.ateamo.core.QBHelper;
import com.ateamo.utils.TimeUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
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
//        TODO проверить задвоенность отправки сообщения
        if (chatMessage.getSenderId() == null) {
            return convertView;
        }

        QBUser currentUser = QBHelper.getSharedInstance().getCurrentUser();
        boolean isOutgoing = chatMessage.getSenderId() == null || chatMessage.getSenderId().equals(currentUser.getId());
        setAlignment(holder, isOutgoing);
        holder.messageTextView.setText(chatMessage.getBody());
        holder.dateTextView.setText(getTimeText(chatMessage));
        holder.nameTextView.setText(chatMessage.getSenderId().toString());
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage(Member.getCurrent().getProfilePictureURL(), holder.iconImageView, options);

        if (chatMessage.getAttachments().size() > 0) {
            QBAttachment attachment = (new ArrayList<QBAttachment>(chatMessage.getAttachments())).get(0);
            MainActivity mainActivity = MainActivity.getInstance();
            if (mainActivity.getAttachmentUri() != null && mainActivity.getAttachmentQBId() != null && chatMessage.getAttachments().size() > 0) {
                if (mainActivity.getAttachmentQBId().equals(attachment.getId())) {
                    Uri attachmentUri = mainActivity.getAttachmentUri();
                    if (attachmentUri != null) {
                        ContentResolver contentResolver = mainActivity.getContentResolver();
                        String attachmentType = contentResolver.getType(attachmentUri);
                        if (attachmentType.contains("image")) {
                            String[] filePathColumn = { MediaStore.Images.Media.DATA };
                            Cursor cursor = contentResolver.query(attachmentUri, filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String imageDecodableString = cursor.getString(columnIndex);
                            cursor.close();
                            holder.attachmentImageView.setImageBitmap(BitmapFactory.decodeFile(imageDecodableString));
                        }
                    }
                    mainActivity.clearAttachment();
                }
            } else {
//                ImageLoader.getInstance().displayImage(attachment.getUrl(), holder.attachmentImageView);
                String attachmentURL = attachment.getUrl();
                ImageLoader.getInstance().displayImage(attachment.getUrl(), holder.attachmentImageView, options);
            }
        }
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
            holder.itemContentLinearLayout.setGravity(Gravity.RIGHT);
            holder.messageLinearLayout.setBackgroundResource(R.drawable.right_bubble_bg);
            holder.itemContentLinearLayout.addView(holder.messageDateLinearLayout);
            holder.itemContentLinearLayout.addView(holder.iconNameLinearLayout);
        } else {
            holder.itemContentLinearLayout.setGravity(Gravity.LEFT);
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
        holder.attachmentImageView = (ImageView) v.findViewById(R.id.attachmentImageView);
        return holder;
    }

    private String getTimeText(QBChatMessage message) {
        return TimeUtils.millisToLongDHMS(message.getDateSent() * 1000);
    }

    private static class ViewHolder {
        public ImageView iconImageView;
        public ImageView attachmentImageView;
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
