package com.ateamo.core;

import com.quickblox.chat.model.QBChatMessage;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

public interface ChatManager {

    public void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException;

    public void release() throws XMPPException;
}
