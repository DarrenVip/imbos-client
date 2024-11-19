package com.imbos.chat.util;
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


/**
 * Static constants for this package.
 * 
 * @author 
 */
public class Constants {

    public static final String SHARED_PREFERENCE_NAME = "client_preferences";
    
    public static final String SHARED_SET_NOTIFY_SOUND = "set_notify_sound";
    public static final String SHARED_SET_NOTIFY_VIBRATOR = "set_notify_vibrator";
    public static final String SHARED_SET_NOTIFY_ON = "set_notify_on";
    // PREFERENCE KEYS

    public static final String CALLBACK_ACTIVITY_PACKAGE_NAME = "CALLBACK_ACTIVITY_PACKAGE_NAME";

    public static final String CALLBACK_ACTIVITY_CLASS_NAME = "CALLBACK_ACTIVITY_CLASS_NAME";

    public static final String API_KEY = "API_KEY";

    public static final String VERSION = "VERSION";

    public static final String XMPP_HOST = "XMPP_HOST";

    public static final String XMPP_PORT = "XMPP_PORT";

    public static final String XMPP_USERNAME = "XMPP_USERNAME";

    public static final String XMPP_PASSWORD = "XMPP_PASSWORD";
    
    

    // public static final String USER_KEY = "USER_KEY";

    public static final String DEVICE_ID = "DEVICE_ID";

    public static final String EMULATOR_DEVICE_ID = "EMULATOR_DEVICE_ID";


    public static final String ACTION_START_SERVICE = "com.imbos.SERVICE_START";
    public static final String ACTION_SEND = "com.imbos.MESSAGE_SEND";
    
    public static final String ACTION_MESSAGE_RECEVER = "com.imbos.MESSAGE_RECEVER";
    public static final String ACTION_MESSAGE_SEND = "com.imbos.MESSAGE_SEND";
    
    public static final String EXTRA_DATA = "com.imbos.extra.data";
    public static final String EXTRA_USER = "com.imbos.extra.user";
    


    public static final String EXTRA_MSG_ID = "com.imbos.extra.msg.id";
    public static final String EXTRA_MSG_FROMS = "com.imbos.extra.msg.froms";
    public static final String EXTRA_MSG_TOS = "com.imbos.extra.msg.tos";
    public static final String EXTRA_MSG_DATE = "com.imbos.extra.msg.date";
    public static final String EXTRA_MSG_CONTENT = "com.imbos.extra.msg.content";
    
    public static final String SHARED_FILE_DIR = "fileDir";
    public static final String SHARED_TOKEN = "token";
    public static final String SHARED_USER_TYPE = "user.type";
    
    public static final String QRCODE_PREFIX_MB = "imbos.ucard:"; 
    public static final String QRCODE_PREFIX_EP = "imbos.ecard:";
    
    public static final int MESSAGE_FINISH=11;
    public static final int MESSAGE_FAILURE=12;
    
    public static final String PREFIX_FILE = "file:";
    public static final String PREFIX_LINK = "link:";
    
    public static final String SUFFIX_SOUND = ".zs";
    public static final String SUFFIX_JPG = ".jpg";
    public static final String SUFFIX_JPEG = ".jpeg";
    public static final String SUFFIX_PNG = ".png";
    
    public static final int FIRSTPAGE = 1;
    public static final int PERPAGECOUNT = 10;
    
    public enum MessageStatus{
    	READY,FAILED,FINISH,DOWLOADING,UPLOADING
    }
    public enum MessageDirection{
    	SEND,RECEIVE
    }
    public interface ABOUT_LINK{
    	String UNDERSTAND = "understandIMBOS.jsp";
    	String PLAY = "palyIMBOS.jsp";
    	String HELP = "helpIMBOS.jsp";
    } 
    public interface SEX{
    	
    	String BOY = "3322";
    	String GIRL = "3323";
    }
    
    /**
     * picid：个人相片；photoid：企业相片；fundid：资金图片；gsid：商品\服务图片；projectid：项目图片。
     * @author Administrator
     *
     */
    public interface PHOTO_TYPE{
    	
    	String HEAD = "head";
    	String MEMBER = "picid";
    	String ENTERPRISE  = "photoid";
    	String FUND  = "fundid";
    }
    
    /**
     * 用户类型
     * @author Kingly
     *
     */
    public interface USER_TYPE{
    	/**
    	 * 个人帐号
    	 */
    	String MB = "511";
    	/**
    	 * 企业帐号
    	 */
    	String EP = "512";
    }
    
    public interface MENTHOD{

		String LOGN="login_index";
		String LOGIN_REGISTERED="login_registered";
		String AUTOUPDATE_GETVERSION="autoUpdate_getVersion";
		
		String FRIENDS="friend_all";
		String FRIEND_ADD="friend_add";
		
		String FRIEND_ADD_STAR="friend_addstar";
		String FRIEND_DEL_STAR="friend_deletestar";
		
		String FRIEND_DEL="friend_del";
		String FRIEND_BLACK="friend_black";
		String FRIEND_UNBLACK="friend_unblack";
		String FRIEND_ALIAS="friend_alias";
		String FRIEND_RECOMMEND ="friend_findrecommend";
		
		String UPLOAD_HEAD="upload_head";
		String USER_SET="membersetting_findhomepages";
		String USER_EDIT_FILED="membersetting_edithomepages";
		String USER_EDIT_PWD="membersetting_editpassword";
		String USER_FIND_ACCOUNT="membersetting_findaccount";
		
		String UPLOAD_IMAGE="common_addreceivefile";
		
		String MEMBER_SIMPLE="member_simple";
		String MEMBER_DETAIL="member_detail";
		String MEMBER_FINDID="member_findid";
		String MEMBER_FINDADV="member_findadv";
		String MEMBER_NEARBY="member_nearby";
		String MEMBER_CONTRAST="member_contrast";
		
		String EMEMBER_FINDID="enterprise_findid";
		String EMEMBER_FINDHOME="enterprise_findenterprisehome";
		String EMEMBER_ABOUTUS="enterprise_findenterprisedetail";
		String ENTERPRISE_FINDEIDFRIEND="enterprise_findeidfriend";
		String ENTERPRISE_NEARENTERPRISE="enterprise_nearenterprise";
		String ENTERPRISE_SEARCHITEMS="enterprise_searchItems";
		
		String FIND_FUNDS = "enterprise_findfund";
		String FIND_SUBJECT = "enterprise_findproject";
		String FIND_GS = "enterprise_findgs";
		
		String EMEMBER_ADDATTENTION="member_addattention";
		String EMEMBER_ADDPRAISE="common_addpraise";
		String EMEMBER_ADDCOMMENT="common_addcomment";
		String EMEMBER_FINDCOMMENT="common_findcomment";
		String EMEMBER_FINDCONSULT="enterprise_findconsult";
		
		String EMEMBER_EDITEIM="enterprisesetting_editeim";
		String EMEMBER_FINDEIM="enterprisesetting_findeim";
		String EMEMBER_FINDVIPEXPLAIN="enterprisesetting_findvipexplain";
		String EMEMBER_FINDENTERPRISEUSER="enterprisesetting_findenterpriseuser";
		String EMEMBER_EDITENTERPRISEUSER="enterprisesetting_editenterpriseuser";
		String EMEMBER_FINDFUND="enterprise_searchfund";
		String EMEMBER_FINDPROJECT="enterprise_searchproject";
		String EMEMBER_FINDGS="enterprise_searchgs";
		
		
		String FIND_ATTENTION="membersetting_findattention"; 
		
		
		String COMMON_DIC="common_dic";
		String COMMON_ADDCODE="common_addcode";
		String COMMON_PHOTO="common_findphoto";
		
		String COMMON_PWD_VALICODE = "common_findpwd";
		String COMMON_EDIT_NEWPWD = "common_editnewpwd";
		
		
		String MSG_SEND="/imbos-server/api.do?action=send";
		String MSG_UPLOAD="/imbos-server/api.do?action=sendFile";
		
	
	}
}
