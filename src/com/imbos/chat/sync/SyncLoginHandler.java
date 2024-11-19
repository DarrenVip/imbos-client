package com.imbos.chat.sync;

import java.util.Map;

import android.content.Context;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.setting.Config;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.SharedUtils;
import com.imbos.chat.util.StringUtil;

public class SyncLoginHandler extends SyncHandler{
	@Override
	public boolean handle(SyncTask task) {
		
		Map<String,?> response = task.getResponse();
		
		String token = StringUtil.toString(response.get("t"));
		String resultCode =  StringUtil.toString(response.get(SyncTask.RESULT_CODE));
		
		Config config = ChatApp.getConfig();
		config.saasToken = token;
		config.saasUid = token.substring(1);
		
		Context ctx = ChatApp.getContext();
		
		SharedUtils.instance(ctx)
		.putString(Constants.XMPP_USERNAME, config.saasUid)
		.putString(Constants.XMPP_PASSWORD, config.saasToken)
		.putString(Constants.SHARED_USER_TYPE,resultCode);
		
		ChatApp.getConfig().save(ctx);
		
		return true;
	}
}
