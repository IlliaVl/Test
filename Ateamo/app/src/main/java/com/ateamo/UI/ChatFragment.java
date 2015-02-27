package com.ateamo.UI;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
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

import com.ateamo.adapters.ChatAdapter;
import com.ateamo.ateamo.R;
import com.ateamo.core.QBHelper;
import com.ateamo.core.Team;
import com.ateamo.definitions.Consts;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    private static final String TAG = "CHAT";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_DIALOG = "dialog";
    private final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";

    private EditText messageEditText;
    private ListView messagesContainer;
    private Button sendButton;
    private ProgressBar progressBar;

    public static enum Mode {PRIVATE, GROUP}
    private Mode mode = Mode.PRIVATE;
    private QBHelper qbHelper;
    private ChatAdapter adapter;

    private ArrayList<QBChatMessage> history;
    private boolean opponentLogin = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

//    public ChatFragment() {
//        // Required empty public constructor
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    private void initViews(View view) {
        messagesContainer = (ListView) view.findViewById(R.id.messagesContainer);
        messageEditText = (EditText) view.findViewById(R.id.messageEdit);
        sendButton = (Button) view.findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) view.findViewById(R.id.meLabel);
        TextView companionLabel = (TextView) view.findViewById(R.id.companionLabel);
        RelativeLayout container = (RelativeLayout) view.findViewById(R.id.container);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        qbHelper = QBHelper.getSharedInstance();
        container.removeView(meLabel);
        container.removeView(companionLabel);

        progressBar.setVisibility(View.VISIBLE);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageEditText.getText().toString();
                if (TextUtils.isEmpty(messageText) || qbHelper.getCurrentDialog() == null) {
                    return;
                }

                // Send chat message
                //
                QBChatMessage chatMessage = new QBChatMessage();
                chatMessage.setBody(messageText);
                chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
                chatMessage.setDateSent(new Date().getTime()/1000);

                try {
                    qbHelper.sendMessage(chatMessage);
                } catch (XMPPException e) {
                    Log.e(TAG, "failed to send a message", e);
                } catch (SmackException sme){
                    Log.e(TAG, "failed to send a message", sme);
                }

                messageEditText.setText("");

                if(mode == Mode.PRIVATE) {
                    showMessage(chatMessage);
                }
                sendPushMessage(chatMessage);
            }
        });
        joinGroupChat();
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
            json.put(Consts.GCM_GROUP_NAME_ID, Team.getCurrent().getName());
            json.put(Consts.GCM_GROUP_HASH_ID, Team.getCurrent().getHash());
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

}
