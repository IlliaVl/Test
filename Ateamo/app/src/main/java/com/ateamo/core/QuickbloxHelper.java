package com.ateamo.core;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.List;

/**
 * Created by vlasovia on 21.02.15.
 */
public class QuickbloxHelper {
    static final QuickbloxHelper sharedInstance = new QuickbloxHelper();

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

    static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 30;

    private QBUser currentUser;
    private QBChatService chatService;


    private boolean loggedIn = false;
    private boolean loggingInProcess = false;



    private QuickbloxHelper() {
    }



    public static QuickbloxHelper getSharedInstance() {
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
        if (loggingInProcess || loggedIn || Member.getCurrent().getHash() == null) {
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
                loginToChat(user);
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


}
