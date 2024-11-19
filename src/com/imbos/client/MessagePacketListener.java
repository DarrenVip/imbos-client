package com.imbos.client;
/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import android.content.Intent;
import android.util.Log;

import com.imbos.chat.util.Constants;
import com.imbos.chat.util.LogUtil;

/** 
 * This class notifies the receiver of incoming message packets asynchronously.  
 *
 * @author 
 */
public class MessagePacketListener implements PacketListener {

    private static final String LOGTAG = LogUtil
            .makeLogTag(MessagePacketListener.class);

    private final XmppManager xmppManager;

    public MessagePacketListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    public XmppManager getXmppManager() {
		return xmppManager;
	}
    @Override
    public void processPacket(Packet packet) {
        Log.d(LOGTAG, "MessagePacketListener.processPacket()...");
        Log.d(LOGTAG, "packet.toXML()=" + packet.toXML());
       
		if (packet instanceof MessageIQ) {
			MessageIQ messageIQ = (MessageIQ) packet;
			if (messageIQ.getChildElementXML().contains(MessageIQ.IQ_NAMESPACE)) {
				
				if (IQ.Type.SET== messageIQ.getType()) {
					
					xmppManager.getContext().sendOrderedBroadcast(
							new Intent(Constants.ACTION_MESSAGE_RECEVER)
								.putExtra(Constants.EXTRA_DATA, messageIQ), null);
					
					//回复接收成功
					Packet reply = IQ.createResultIQ(messageIQ);
					reply.getExtension(MessageIQ.IQ_NAMESPACE);
					xmppManager.getConnection().sendPacket(reply);

				}else if(IQ.Type.RESULT == messageIQ.getType()){
					Log.d(LOGTAG,"msg result。。。。。。。。。。。。。。");
				}
			}
		}else if(packet instanceof IQ){
			if(IQ.Type.RESULT == ((IQ) packet).getType()){
				//DbManager.updateMessage(id, status, receiveDate);
			}
		}

    }

}
