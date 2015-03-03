package com.ateamo.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ateamo.adapters.ChatAdapter;
import com.ateamo.ateamo.R;
import com.ateamo.core.QBHelper;
import com.ateamo.core.Team;
import com.ateamo.definitions.Consts;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.QBRequestCanceler;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import viewbadger.BadgeView;


public class ChatFragment extends Fragment {
    private static final String TAG = "CHAT";

    private OnFragmentInteractionListener mListener;

    private final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";

    private EditText messageEditText;
    private ListView messagesContainer;
    private Button sendButton;
    private Button attachmentButton;
    private ProgressBar progressBar;

    public static enum Mode {PRIVATE, GROUP}
    private Mode mode = Mode.PRIVATE;
    private QBHelper qbHelper;
    private ChatAdapter adapter;

    private ArrayList<QBChatMessage> history;
    private boolean opponentLogin = false;

    private Uri attachmentUri;
    private String attachmentQBId;
    BadgeView attachmentBadge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    private void initViews(View view) {
        messagesContainer = (ListView) view.findViewById(R.id.messagesContainer);
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
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        qbHelper = QBHelper.getSharedInstance();
        container.removeView(meLabel);
        container.removeView(companionLabel);

        progressBar.setVisibility(View.VISIBLE);
    }



    public void sendButtonClicked() {
        String messageText = messageEditText.getText().toString();
        if (TextUtils.isEmpty(messageText) || qbHelper.getCurrentDialog() == null) {
            return;
        }
        // Send chat message
        //
        final QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(messageText);
        chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
        chatMessage.setDateSent(new Date().getTime()/1000);

        if (attachmentUri != null) {
            File file = new File(getPath(attachmentUri));
            QBRequestCanceler requestCanceler = QBContent.uploadFileTask(file, true, null, new QBEntityCallbackImpl<QBFile>() {
                @Override
                public void onSuccess(QBFile qbFile, Bundle params) {
                    attachmentQBId = qbFile.getId().toString();
                    attachmentBadge.hide();
                    // File public url. Will be null if fileIsPublic=false in query
//                    String publicUrl = qbFile.getPublicUrl();
                    QBAttachment attachment = new QBAttachment("photo");
                    attachment.setId(attachmentQBId);
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
        try {
            qbHelper.sendMessage(message);
        } catch (XMPPException e) {
            Log.e(TAG, "failed to send a message", e);
        } catch (SmackException sme){
            Log.e(TAG, "failed to send a message", sme);
        }
        messageEditText.setText("");
        sendPushMessage(message);
    }



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
                String attachment = cursor.getString(columnIndex);
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



    public void joinGroupChat() {
        if (qbHelper.getCurrentDialog() == null) {
            return;
        }
        qbHelper.joinGroupChat(new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                loadChatHistory();
            }

            @Override
            public void onError(List list) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("error when join group chat: " + list.toString()).create().show();
            }
        });
    }



    private void loadChatHistory(){
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);
        customObjectRequestBuilder.sortDesc("date_sent");

        QBChatService.getDialogMessages(qbHelper.getCurrentDialog(), customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                history = messages;

                adapter = new ChatAdapter(getActivity(), new ArrayList<QBChatMessage>());
                messagesContainer.setAdapter(adapter);

                for (int i = messages.size() - 1; i >= 0; --i) {
                    QBChatMessage msg = messages.get(i);
                    showMessage(msg);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(List<String> errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("load chat history errors: " + errors).create().show();
            }
        });
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
//        ArrayList<Integer> occupants = qbHelper.getCurrentDialog().getOccupants();
//        occupants.remove(qbHelper.getCurrentUser().getId());
//        for (int i = occupants.size() - 1; i >= 0; --i) {
//            if (!onlineUsers.contains(occupants.get(i))) {
//                toUsersList.add(occupants.get(i));
//            }
//        }
        ArrayList<Integer> occupants = qbHelper.getCurrentDialog().getOccupants();
        ArrayList<Integer> toUsersList = occupants;
        toUsersList.removeAll(qbHelper.getOnlineUsers());
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
            if (!opponentLogin) {
                json.put(Consts.GCM_USERS_ID, occupants);
//                json.put(Consts.GCM_NOTIFICATION_TYPE_ID, );
            } else {
//                json.put(Consts.GCM_OPPONENT_ID, );
//                json.put(Consts.GCM_OPPONENT_HASH_ID, );
//                json.put(Consts.GCM_NOTIFICATION_TYPE_ID, );
            }
            json.put(Consts.GCM_DIALOG_NAME_ID, qbHelper.getCurrentDialog().getName());
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



    private void scrollDown() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }



    @Override
    public void onPause() {
        super.onPause();
        QBHelper.getSharedInstance().leaveRoom();
    }



    @Override
    public void onResume() {
        super.onResume();
        joinGroupChat();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        initViews(view);
        return view;
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
}
