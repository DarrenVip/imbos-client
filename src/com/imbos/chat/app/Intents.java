package com.imbos.chat.app;

public interface Intents {
	
	
	
	String ACTION_MEMBER_FIND_BYID = "com.imbos.member.findById";
	String ACTION_MEMBER_FIND_ADV = "com.imbos.member.findAdv";
	String ACTION_MEMBER_FIND_NIERBY = "com.imbos.member.findNearby";
	
	String ACTION_DATA = "cn.sklgroup.action.data";
	
	String ACTION_SYNC = "com.imbos.sync";
	String ACTION_SYNC_PROGRESS = "com.imbos.sync.progress";
	
	String EXTRA_TASK = "imbos.extra.task";
	String EXTRA_TASK_MAX = "imbos.extra.task.max";
	String EXTRA_TASK_CUR = "imbos.extra.task.cur";
	String EXTRA_TASK_TIP = "imbos.extra.task.tip";
	
	
	String EXTRA_TITLE = "extra.title";
	String EXTRA_MENU_ID = "extra.memuId";
	
	String EXTRA_TEXT = "extra.text";
	String EXTRA_MEMBER = "extra.member";
	
	
	String EXTRA_SUCCESS = "extra.success";
	String EXTRA_DATA = "extra.data";
	String EXTRA_SOURCE_ACTION = "extra.source";
	String EXTRA_LOGINAME = "extra.loginame";
	String EXTRA_PASSWORD = "extra.password";
	
	String EXTRA_EID = "extra.eid";
	String EXTRA_UID = "extra.uid";
	String EXTRA_ID = "extra.id";
	
	String EXTRA_STATUS= "extra.status";
	String EXTRA_CLASS= "extra.class";
	String EXTRA_METHOD= "extra.method";
	String EXTRA_ARGS= "extra.args";
	String EXTRA_SINGE= "extra.singe";
	
	
	  public static final String EXTRA_MSG_ID = "com.imbos.extra.msg.id";
	    public static final String EXTRA_MSG_FROMS = "com.imbos.extra.msg.froms";
	    public static final String EXTRA_MSG_TOS = "com.imbos.extra.msg.tos";
	    public static final String EXTRA_MSG_DATE = "com.imbos.extra.msg.date";
	    public static final String EXTRA_MSG_CONTENT = "com.imbos.extra.msg.content";
}
