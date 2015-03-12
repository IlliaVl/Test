package com.ateamo.UI;

import com.ateamo.core.DialogCallback;
import com.ateamo.core.Member;
import com.ateamo.core.QBHelper;
import com.quickblox.chat.model.QBDialog;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vlasovia on 07.03.15.
 */
public class PrivateChatFragment extends ChatFragment {

    private Member opponent;

    @Override
    public void joinChat() {
        QBHelper.getSharedInstance().getDialog(new DialogCallback() {
            @Override
            public void response(QBDialog dialog) {
            }
        }, Member.getCurrent().getHash() + opponent.getHash(), opponent.getHash() + Member.getCurrent().getHash());

        super.joinChat();
    }



    @Override
    protected void putJsonDataForPushMessage(JSONObject json, ArrayList<Integer> occupants) {
//        try {
////                json.put(Consts.GCM_OPPONENT_ID, );
////                json.put(Consts.GCM_OPPONENT_HASH_ID, );
////                json.put(Consts.GCM_NOTIFICATION_TYPE_ID, );
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }


    public Member getOpponent() {
        return opponent;
    }

    public void setOpponent(Member opponent) {
        this.opponent = opponent;
    }
}
