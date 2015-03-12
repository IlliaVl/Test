package com.ateamo.core;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ateamo.UI.MainActivity;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vlasovia on 21.02.15.
 */
public class QBHelper {
    static final QBHelper sharedInstance = new QBHelper();
    private static final String TAG = "GroupChatManagerImpl";


    static private Context context;

    private static final String APP_ID = "17684";
    private static final String AUTH_KEY = "Gx6fuF5sMj-bzcP";
    private static final String AUTH_SECRET = "hMH7bO6TNwk7c-Q";
    //    [QBConnection registerServiceKey:@"Gx6fuF5sMj-bzcP"];
    //
//    private static final String USER_LOGIN = "mish";
//    private static final String USER_LOGIN = "loca";
//    private static final String USER_LOGIN = "c02e33a07f2811e4b5c1001851012600";//iliavl@list.ru
    private static final String USER_LOGIN = "b8ddb9307f2911e4b5c1001851012600";//Sheldon Cooper
    //    private static final String USER_LOGIN = "illia";
    private static final String USER_PASSWORD = "qqqqqqqq";
//    private static final String USER_LOGIN = "c02e33a07f2811e4b5c1001851012600";
//    private static final String USER_PASSWORD = "p@ssword";

    static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 80;

    private QBGroupChatManager groupChatManager;
    private QBUser currentUser;
    private QBChatService chatService;
    private QBDialog currentTeamDialog;

    private boolean loggedIn = false;
    private boolean loggingInProcess = false;

    private PlayServicesHelper playServicesHelper;

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
        //TODO Заменить на нормальную работу после завершения сервера
        user.setLogin(USER_LOGIN);
        user.setPassword(USER_PASSWORD);

        loggingInProcess = true;
        QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle args) {
//                initParticipantListener();
                playServicesHelper = new PlayServicesHelper(context);
                loggingInProcess = false;
                loggedIn = true;
                user.setId(session.getUserId());
                currentUser = user;
                loginToChat(user);
                loginToCurrentTeamChat();
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
                if (currentTeamDialog != null && MainActivity.getInstance() != null) {
                    MainActivity.getInstance().refreshChat();
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
        //TODO Заменить на нормальную работу после завершения сервера
        //TODO Добавить проверку  || Team.getCurrent() == null
        if (loggingInProcess || !loggedIn) {
            return;
        }
        getDialog(new DialogCallback() {
            @Override
            public void response(QBDialog dialog) {
                currentTeamDialog = dialog;
                if (groupChatManager != null && MainActivity.getInstance() != null) {
                    MainActivity.getInstance().refreshChat();
                }
            }
        }, "mish_illia");
    }



    public QBGroupChat joinChat(QBDialog dialog, QBEntityCallback callback){
        QBGroupChat chat = groupChatManager.createGroupChat(dialog.getRoomJid());
        join(chat, callback);
        return chat;
    }



    public void getDialog(final DialogCallback dialogCallback, final String... names){
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.in("name", names);
//        ArrayList<String> names1 = new ArrayList<String>();
//        names1.add("8b0e82b0bb7a11e2860f00185191c0d9");
//
////        requestBuilder.in("name", names1);
//        requestBuilder.in("name", "8b0e82b0bb7a11e2860f00185191c0d9", "f1ae7a6c917e11e4b5c1001851012600");
//        requestBuilder.in("name", "mish_illia");
//        requestBuilder.in("name", "test_dDDDDDhh", "mish_illia"); WORKS
//        requestBuilder.addParameter("name", "mish_illia");
//        requestBuilder.addParameter("name", "8b0e82b0bb7a11e2860f00185191c0d9");

        QBChatService.getChatDialogs(null, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBDialog> dialogs, Bundle args) {
                if (dialogs.size() > 0) {
                    dialogCallback.response(dialogs.get(0));
                }
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
            }

            @Override
            public void onError(List<String> errors) {

            }
        });
    }



    private void join(final QBGroupChat chat, final QBEntityCallback callback) {
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0);

        chat.join(history, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess();

                        Toast.makeText(MainActivity.getInstance(), "Join successful", Toast.LENGTH_LONG).show();
                    }
                });
                Log.w("Chat", "Join successful");
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onError(final List list) {
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(list);
                    }
                });


                Log.w("Could not join chat, errors:", Arrays.toString(list.toArray()));
            }
        });
    }

    public QBDialog getCurrentTeamDialog() {
        return currentTeamDialog;
    }

    public QBUser getCurrentUser() {
        return currentUser;
    }
}
