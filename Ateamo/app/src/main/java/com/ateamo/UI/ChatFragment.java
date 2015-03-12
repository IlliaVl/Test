package com.ateamo.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ateamo.adapters.ChatAdapter;
import com.ateamo.ateamo.R;
import com.ateamo.core.QBHelper;
import com.ateamo.core.Team;
import com.ateamo.definitions.Consts;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.listeners.QBParticipantListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import viewbadger.BadgeView;


public class ChatFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "CHAT";

    private OnFragmentInteractionListener mListener;

    private EditText messageEditText;
    private ListView messagesListView;
    private Button sendButton;
    private Button attachmentButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    private QBHelper qbHelper;
    private MessageListener messageListener;
    private QBParticipantListener participantListener;
    private ArrayList<Integer> onlineUsers = new ArrayList<Integer>();
    private QBDialog dialog;
    private QBGroupChat chat;
    private ChatAdapter adapter;

    private ArrayList<QBChatMessage> history;
//    private boolean opponentLogin = false;

    private Uri attachmentUri;
    private String attachmentQBId;
    BadgeView attachmentBadge;

    private int numberOfPageMessages = 10;
    private int numberOfMessages;
    private int numberOfDisplayedMessages = numberOfPageMessages;
    private int numberOfSkippedMessages;
    private boolean updateProcess = false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageListener = new MessageListener();
        initParticipantListener();
    }



    private void initParticipantListener(){
        participantListener = new QBParticipantListener() {
            @Override
            public void processPresence(QBGroupChat chat, QBPresence presence) {
                Log.i(TAG, "teamChat: " + chat.getJid() + ", presence: " + presence);
                try {
                    onlineUsers = new ArrayList<Integer>(chat.getOnlineUsers());
                } catch (XMPPException e) {

                }
            }
        };
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        initViews(view);
        initRefreshControl(view);
        return view;
    }



    private void initViews(View view) {
        messagesListView = (ListView) view.findViewById(R.id.messagesContainer);
        messageEditText = (EditText) view.findViewById(R.id.messageEdit);

        sendButton = (Button) view.findViewById(R.id.chatSendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButtonClicked();
            }
        });

        attachmentButton = (Button) view.findViewById(R.id.attachmentButton);
        attachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentButtonClicked();
            }
        });
        attachmentBadge = new BadgeView(getActivity(), attachmentButton);
        attachmentBadge.setTextSize(6);
        attachmentBadge.setText("1");

        TextView meLabel = (TextView) view.findViewById(R.id.meLabel);
        TextView companionLabel = (TextView) view.findViewById(R.id.companionLabel);
        RelativeLayout container = (RelativeLayout) view.findViewById(R.id.container);

        qbHelper = QBHelper.getSharedInstance();
        container.removeView(meLabel);
        container.removeView(companionLabel);
    }



    private void initRefreshControl(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(this);
    }



    public void sendButtonClicked() {
        String messageText = messageEditText.getText().toString();
        if (TextUtils.isEmpty(messageText) || dialog == null) {
            return;
        }
        // Send chat message
        //
        final QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(messageText);
        chatMessage.setProperty("save_to_history", "1");
        chatMessage.setDateSent(new Date().getTime()/1000);

        if (attachmentUri != null) {
            File file = new File(getPath(attachmentUri));
            QBContent.uploadFileTask(file, true, null, new QBEntityCallbackImpl<QBFile>() {
                @Override
                public void onSuccess(QBFile qbFile, Bundle params) {
                    attachmentQBId = qbFile.getId().toString();
                    attachmentBadge.hide();
                    // File public url. Will be null if fileIsPublic=false in query
//                    String publicUrl = qbFile.getPublicUrl();
                    QBAttachment attachment = new QBAttachment("photo");
                    attachment.setId(attachmentQBId);
                    attachment.setUrl(qbFile.getPublicUrl());
                    chatMessage.addAttachment(attachment);
                    sendMessage(chatMessage);
                }

                @Override
                public void onError(List<String> errors) {

                }
            }, new QBProgressCallback() {
                @Override
                public void onProgressUpdate(int progress) {

                }
            });
//            QBAttachment attachment = new QBAttachment("photo");
//            attachment.setUrl();
//            chatMessage.addAttachment(attachment);
        } else {
            sendMessage(chatMessage);
        }
    }



    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(attachmentUri, projection, null, null, null);
//        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    private void sendMessage(QBChatMessage message) {
        if (chat != null) {
            try {
                chat.sendMessage(message);
                messageEditText.setText("");
                sendPushMessage(message);
                numberOfMessages++;
                numberOfDisplayedMessages++;
            } catch (SmackException.NotConnectedException nce){
                nce.printStackTrace();
            } catch (IllegalStateException e){
                e.printStackTrace();

                Toast.makeText(MainActivity.getInstance(), "You are still joining a group chat, please white a bit", Toast.LENGTH_LONG).show();
            } catch (XMPPException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(MainActivity.getInstance(), "Join unsuccessful", Toast.LENGTH_LONG).show();
        }
    }


//    private void sendMessage(QBChatMessage message) {
//        try {
//            qbHelper.sendMessage(chat, message);
//        } catch (XMPPException e) {
//            Log.e(TAG, "failed to send a message", e);
//        } catch (SmackException sme){
//            Log.e(TAG, "failed to send a message", sme);
//        }
//        messageEditText.setText("");
//        sendPushMessage(message);
//        numberOfMessages++;
//        numberOfDisplayedMessages++;
//    }



    public void attachmentButtonClicked() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("*/*");
//        startActivityForResult(galleryIntent, Consts.RESULT_LOAD_IMG);
        getActivity().startActivityForResult(galleryIntent, Consts.RESULT_LOAD_IMG);
    }



    public void attachmentAdded(Intent intent) {
        if (intent.getData() == null) {
            attachmentUri = null;
            attachmentBadge.hide();
        } else {
            attachmentUri = intent.getData();
            attachmentBadge.show();
        }
    }



    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        try {
            // When an Image is picked
            if (requestCode == Consts.RESULT_LOAD_IMG && intent != null) {
                // Get the Image from data

                attachmentUri = intent.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(attachmentUri, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                String attachment = cursor.getString(columnIndex);
                cursor.close();
//                ImageView imgView = (ImageView) findViewById(R.id.imgView);
//                // Set the Image in ImageView after decoding the String
//                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

            } else {
                Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }



//    public void joinPrivateChat() {
//        qbHelper.joinPrivateChat(opponent, new QBEntityCallbackImpl() {
//            @Override
//            public void onSuccess() {
//                loadChatHistory(qbHelper.getPrivateDialog());
//            }
//
//            @Override
//            public void onError(List list) {
//                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//                dialog.setMessage("error when join group chat: " + list.toString()).create().show();
//            }
//        });
//    }



    public void joinChat() {
        if (dialog == null) {
            return;
        }
        chat = qbHelper.joinChat(dialog, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                chat.addMessageListener(messageListener);
                chat.addParticipantListener(participantListener);
                loadChatHistory(dialog);
            }

            @Override
            public void onError(List list) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("error when join group chat: " + list.toString()).create().show();
            }
        });
    }



    private void loadChatHistory(final QBDialog dialog){
        updateProcess = true;
        swipeRefreshLayout.setRefreshing(true);
        QBChatService.getDialogMessagesCount(dialog.getDialogId(), new QBEntityCallbackImpl<Integer>() {
            @Override
            public void onSuccess(Integer result, Bundle params) {
                super.onSuccess(result, params);
                numberOfMessages = result;
                numberOfSkippedMessages = numberOfMessages - numberOfPageMessages <= 0 ? 0 : numberOfMessages - numberOfPageMessages;
                getChatHistory(dialog, false);
            }

            @Override
            public void onError(List<String> errors) {
                super.onError(errors);
                swipeRefreshLayout.setRefreshing(false);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("load chat history errors: " + errors).create().show();
                updateProcess = false;
            }
        });
    }



    private void getChatHistory(QBDialog dialog, boolean upRefresh){
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(numberOfMessages - numberOfSkippedMessages);
        customObjectRequestBuilder.setPagesSkip(numberOfSkippedMessages);
        customObjectRequestBuilder.sortAsc("date_sent");
        if (upRefresh) {
            messagesListView.setStackFromBottom(false);
        } else {
            messagesListView.setStackFromBottom(true);
        }

        QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                history = messages;
                adapter = new ChatAdapter(getActivity(), messages);
                messagesListView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(List<String> errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("load chat history errors: " + errors).create().show();
                updateProcess = false;
            }
        });
    }



    @Override
    public void onRefresh() {
        if (numberOfSkippedMessages == 0) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        updateProcess = true;
        numberOfSkippedMessages = numberOfSkippedMessages - numberOfPageMessages <= 0 ? 0 : numberOfSkippedMessages - numberOfPageMessages;
        numberOfDisplayedMessages += numberOfMessages - numberOfSkippedMessages;
        getChatHistory(dialog, true);
    }



    public void showMessage(QBChatMessage message) {
        adapter.add(message);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                scrollDown();
            }
        });
    }



    private void sendPushMessage(QBChatMessage message) {
//        ArrayList<Integer> onlineUsers = qbHelper.getOnlineUsers();
//        ArrayList<Integer> toUsersList = new ArrayList<Integer>();
//        ArrayList<Integer> occupants = qbHelper.getCurrentTeamDialog().getOccupants();
//        occupants.remove(qbHelper.getCurrentUser().getId());
//        for (int i = occupants.size() - 1; i >= 0; --i) {
//            if (!onlineUsers.contains(occupants.get(i))) {
//                toUsersList.add(occupants.get(i));
//            }
//        }
        ArrayList<Integer> occupants = qbHelper.getCurrentTeamDialog().getOccupants();
        ArrayList<Integer> toUsersList = occupants;
        toUsersList.removeAll(onlineUsers);
        if (toUsersList.size() == 0) {
                return;
        }

        QBEvent event = new QBEvent();
//        event.setUserIds(new StringifyArrayList<Integer>(toUsersList));
        StringifyArrayList<Integer> testAPNSUsers = new StringifyArrayList<Integer>();
        testAPNSUsers.add(2242466);
        testAPNSUsers.add(2242477);
        testAPNSUsers.add(2370901);
        testAPNSUsers.add(2370921);
        testAPNSUsers.add(2398799);
        event.setUserIds(new StringifyArrayList<Integer>(testAPNSUsers));
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);


        JSONObject json = new JSONObject();
        try {
            json.put("message", message.getBody());
            JSONObject apsJson = new JSONObject();
            apsJson.put("sound", "default");
            apsJson.put("badge", 1);
            apsJson.put("alert", message.getBody());
            json.put("aps", apsJson);
            putJsonDataForPushMessage(json, occupants);
            json.put(Consts.GCM_DIALOG_NAME_ID, qbHelper.getCurrentTeamDialog().getName());
            //TODO Заменить на нормальную работу после завершения сервера. Было:
//            json.put(Consts.GCM_GROUP_NAME_ID, Team.getCurrent().getName());
//            json.put(Consts.GCM_GROUP_HASH_ID, Team.getCurrent().getHash());

            json.put(Consts.GCM_GROUP_NAME_ID, Team.getCurrent() == null ? "" : Team.getCurrent().getName());
            json.put(Consts.GCM_GROUP_HASH_ID, Team.getCurrent() == null ? "" : Team.getCurrent().getHash());
        } catch (Exception e) {
            e.printStackTrace();
        }
        event.setMessage(json.toString());

        QBMessages.createEvent(event, new QBEntityCallbackImpl<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
            }

            @Override
            public void onError(List<String> errors) {

            }
        });
    }



    protected void putJsonDataForPushMessage(JSONObject json, ArrayList<Integer> occupants) {
        try {
            json.put(Consts.GCM_USERS_ID, occupants);
//                json.put(Consts.GCM_NOTIFICATION_TYPE_ID, );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void scrollDown() {
        int hhh = messagesListView.getCount();
        messagesListView.setSelection(messagesListView.getCount() - 1);
    }



    @Override
    public void onPause() {
        super.onPause();
        leaveChat();
//        try {
//            QBHelper.getSharedInstance().release(chat);
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        }
//        QBHelper.getSharedInstance().leaveRoom(chat);
    }



    private void leaveChat() {
        (new AsyncTask<Void, Void, Void>(){
            Exception exception;

            @Override
            protected Void doInBackground(Void... objects) {
                if(chat == null){
                    Log.i(TAG, "Please join room first");
                    return null;
                }

                try {
                    chat.leave();
                    chat.removeMessageListener(messageListener);
                    chat = null;
                } catch (XMPPException e) {
                    exception = e;
                } catch (SmackException.NotConnectedException e) {
                    exception = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (exception == null) {
                    Log.i(TAG, "Leave success");
                } else {
                    Log.i(TAG, "Leave error: " + exception.getClass().getSimpleName());
                }
            }
        }).execute();
    }



    @Override
    public void onResume() {
        super.onResume();
        joinChat();
//        if (opponent == null) {
//        } else {
//            joinPrivateChat();
//        }
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public Uri getAttachmentUri() {
        return attachmentUri;
    }

    public String getAttachmentQBId() {
        return attachmentQBId;
    }



    public void clearAttachment() {
        attachmentUri = null;
        attachmentQBId = null;
    }

    public void setDialog(QBDialog dialog) {
        this.dialog = dialog;
    }

    public void setChat(QBGroupChat chat) {
        this.chat = chat;
    }



    private class MessageListener extends QBMessageListenerImpl<QBGroupChat> {
        @Override
        public void processMessage(QBGroupChat sender, QBChatMessage message) {
            super.processMessage(sender, message);
            ChatFragment.this.showMessage(message);
        }

        @Override
        public void processError(QBGroupChat sender, QBChatException exception, QBChatMessage message) {
            super.processError(sender, exception, message);
        }
    }
}
