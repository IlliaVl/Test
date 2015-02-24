package com.ateamo.core;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ateamo.ateamo.MainActivity;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vlasovia on 21.02.15.
 */
public class QBHelper extends QBMessageListenerImpl<QBGroupChat> implements ChatManager {
    static final QBHelper sharedInstance = new QBHelper();
    private static final String TAG = "GroupChatManagerImpl";

    private MainActivity chatActivity;


    static private Context context;

    private static final String APP_ID = "17684";
    private static final String AUTH_KEY = "Gx6fuF5sMj-bzcP";
    private static final String AUTH_SECRET = "hMH7bO6TNwk7c-Q";
    //    [QBConnection registerServiceKey:@"Gx6fuF5sMj-bzcP"];
    //
    private static final String USER_LOGIN = "mish";
    //    private static final String USER_LOGIN = "illia";
    private static final String USER_PASSWORD = "password";
//    private static final String USER_LOGIN = "c02e33a07f2811e4b5c1001851012600";
//    private static final String USER_PASSWORD = "p@ssword";

    static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 80;

    private QBGroupChatManager groupChatManager;
    private QBUser currentUser;
    private QBChatService chatService;
    private QBDialog currentDialog;
    private QBGroupChat groupChat;


    private boolean loggedIn = false;
    private boolean loggingInProcess = false;



    private QBHelper() {
    }



    public static QBHelper getSharedInstance() {
        return sharedInstance;
    }



    public void init(Context contextCurrent) {
        context = contextCurrent;
        QBChatService.setDebugEnabled(true);
        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);
        if (!QBChatService.isInitialized()) {
            QBChatService.init(context);
        }
        chatService = QBChatService.getInstance();
    }



    public void login() {
        if (loggingInProcess || loggedIn || Member.getCurrent() == null) {
            return;
        }
        final QBUser user = new QBUser();
        user.setLogin(USER_LOGIN);
        user.setPassword(USER_PASSWORD);

        loggingInProcess = true;
        QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle args) {
                loggingInProcess = false;
                loggedIn = true;
                user.setId(session.getUserId());
                currentUser = user;
                loginToCurrentTeamChat();
                loginToChat(user);
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
            }

            @Override
            public void onError(List<String> errors) {
                loggingInProcess = false;
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("create session errors: " + errors).create().show();
            }
        });
    }



    private void loginToChat(final QBUser user){

        chatService.login(user, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                try {
                    groupChatManager = chatService.getGroupChatManager();
                    chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                }
                // go to Dialogs screen
                //
//                Intent intent = new Intent(SplashActivity.this, DialogsActivity.class);
//                startActivity(intent);
//                finish();
            }

            @Override
            public void onError(List errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("chat login errors: " + errors).create().show();
            }
        });
    }



    public void loginToCurrentTeamChat() {
        if (loggingInProcess || !loggedIn || Team.getCurrent() == null) {
            return;
        }
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
//        requestBuilder.addParameter("name", Team.getCurrent().getHash());
//        requestBuilder.addParameter("name", "daad19a87f2811e4b5c1001851012600");
        requestBuilder.addParameter("name", "mish, illia");

        QBChatService.getChatDialogs(null, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBDialog> dialogs, Bundle args) {
                if (dialogs.size() > 0) {
                    currentDialog = dialogs.get(0);
                }
            }

            @Override
            public void onError(List<String> errors) {

            }
        });
    }



    public void joinGroupChat(MainActivity activity, QBEntityCallback callback){
        chatActivity = activity;
        groupChat = groupChatManager.createGroupChat(currentDialog.getRoomJid());
        join(groupChat, callback);
    }

    private void join(final QBGroupChat groupChat, final QBEntityCallback callback) {
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0);

        groupChat.join(history, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {

                groupChat.addMessageListener(QBHelper.this);

                chatActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess();

                        Toast.makeText(chatActivity, "Join successful", Toast.LENGTH_LONG).show();
                    }
                });
                Log.w("Chat", "Join successful");
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onError(final List list) {
                chatActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(list);
                    }
                });


                Log.w("Could not join chat, errors:", Arrays.toString(list.toArray()));
            }
        });
    }

    @Override
    public void release() throws XMPPException {
        if (groupChat != null) {
            try {
                groupChat.leave();
            } catch (SmackException.NotConnectedException nce){
                nce.printStackTrace();
            }

            groupChat.removeMessageListener(this);
        }
    }

    @Override
    public void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException {
        if (groupChat != null) {
            try {
                groupChat.sendMessage(message);
            } catch (SmackException.NotConnectedException nce){
                nce.printStackTrace();
            } catch (IllegalStateException e){
                e.printStackTrace();

                Toast.makeText(chatActivity, "You are still joining a group chat, please white a bit", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(chatActivity, "Join unsuccessful", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void processMessage(QBGroupChat groupChat, QBChatMessage chatMessage) {
        // Show message
        Log.w(TAG, "new incoming message: " + chatMessage);
        chatActivity.showMessage(chatMessage);
    }

    @Override
    public void processError(QBGroupChat groupChat, QBChatException error, QBChatMessage originMessage){

    }

    public QBDialog getCurrentDialog() {
        return currentDialog;
    }

    public QBUser getCurrentUser() {
        return currentUser;
    }
}
