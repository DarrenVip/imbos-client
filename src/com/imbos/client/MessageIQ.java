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


import java.io.Serializable;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class MessageIQ extends IQ implements IQProvider,Serializable{
	/**
	* @Fields serialVersionUID : TODO
	*/
	private static final long serialVersionUID = 1L;
	
	public static String IQ_NAME = "msg";
	public static String IQ_NAMESPACE = "imbos:iq:msg";
    private String id;

    private String apiKey;

    private String froms;
    private String tos;

    private String content;

    private String date;

    public MessageIQ() {
    	
    }

    @Override
    public String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<").append(IQ_NAME).append(" xmlns=\"").append(
                IQ_NAMESPACE).append("\">");
        
        if (id != null) {
            buf.append("<id>").append(id).append("</id>");
            buf.append("<apiKey>").append(apiKey).append("</apiKey>");
            buf.append("<froms>").append(froms).append("</froms>");
            buf.append("<tos>").append(tos).append("</tos>");
            buf.append("<content>").append(content).append("</content>");
            buf.append("<date>").append(date).append("</date>");
        }
        buf.append("</").append(IQ_NAME).append("> ");
        return buf.toString();
    }
    
    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
    	MessageIQ messageIQ = new MessageIQ();
        for (boolean done = false; !done;) {
            int eventType = parser.next();
           
            if (eventType == XmlPullParser.START_TAG) {
                if ("id".equals(parser.getName())) {
                	messageIQ.setId(parser.nextText());
                }  
                if ("apiKey".equals(parser.getName())) {
                	messageIQ.setApiKey(parser.nextText());
                }
                if ("froms".equals(parser.getName())) {
                	messageIQ.setFroms(parser.nextText());
                }
                if ("tos".equals(parser.getName())) {
                	messageIQ.setTos(parser.nextText());
                }
                if ("content".equals(parser.getName())) {
                    messageIQ.setContent(parser.nextText());
                }
                if ("date".equals(parser.getName())) {
                    messageIQ.setDate(parser.nextText());
                }
            } else if (eventType == XmlPullParser.END_TAG
                    && MessageIQ.IQ_NAME.equals(parser.getName())) {
                done = true;
            }
        }
    	return messageIQ;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFroms() {
		return froms;
	}

	public void setFroms(String froms) {
		this.froms = froms;
	}

	public String getTos() {
		return tos;
	}

	public void setTos(String tos) {
		this.tos = tos;
	}
	
}