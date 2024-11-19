package com.imbos.chat.sync;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.ex.NetException;
import com.imbos.chat.net.NetClient;
import com.imbos.chat.setting.Config;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.JSONUtil;
import com.imbos.chat.util.StringUtil;

public class SimpleSyncTask implements Runnable, Serializable {
	private static final long serialVersionUID = 1L;

	public static final int STATUS_READY = 0;
	public static final int STATUS_START = 1;
	public static final int STATUS_FINISH = 2;
	public static final int STATUS_FAILED = 3;

	private String methodName;
	private Map<String, ?> args;

	private int status;
	private OnSyncListener syncListener;

	private String errorMessage;
	private HashMap<String, ?> response;

	@SuppressWarnings("unchecked")
	@Override
	public void run() {

		try {
			if (status != STATUS_READY)
				return;

			notify(STATUS_START);

			HashMap<String, Object> request = new HashMap<String, Object>();
			request.put("f", methodName);
			if (args != null)
				request.put("args", args);

			String requestStr = JSONUtil.encode(request);
			Config config = ChatApp.getConfig();

			String responseStr = null;
			if (config.debug) {
				responseStr = debugResponse();
			} else {
				responseStr = new NetClient(config.saasHost, config.saasPort, config.saasPath).setToken(config.saasToken)
						.request(requestStr);
			}

			response = (HashMap<String, ?>) JSONUtil.decode(responseStr);

			String resultCode = StringUtil.toString(response.get("s"));

			if (StringUtil.isINTEGER_NEGATIVE(resultCode)) {
				notify(STATUS_FINISH);
			} else {
				errorMessage = response.get("i") + "";
				notify(STATUS_FAILED);
			}

		} catch (NetException ex) {
			ex.printStackTrace();
			notify(STATUS_FAILED);
		} catch (Exception ex) {
			ex.printStackTrace();
			notify(STATUS_FAILED);
		}

	}

	private String debugResponse() {

		if (Constants.MENTHOD.FRIENDS.equals(methodName)) {
			return "{\"dt\":[\"friend\"],\"s\":1,\"ds\":[[[\"ID\",\"USER\"],[\"1\",\"wxz\"],[\"2\",\"lx\"]],[[\"ID\",\"NAME\"],[\"1\",\"truedev\"],[\"2\",\"ebestmobile\"]]],\"i\":\"成功\"}";

		} else if ("".equals(methodName)) {

		}
		return null;
	}

	private void notify(int status) {

		if (syncListener != null) {
			syncListener.onChange(this, status);
		} else {

		}
		this.status = status;
	}

	public SimpleSyncTask setSyncListener(OnSyncListener syncListener) {
		this.syncListener = syncListener;
		return this;
	}

	public String getMethodName() {
		return methodName;
	}

	public SimpleSyncTask setMethodName(String methodName) {
		this.methodName = methodName;
		return this;
	}

	public Map<String, ?> getArgs() {
		return args;

	}

	public SimpleSyncTask setArgs(Map<String, ?> args) {
		this.args = args;
		return this;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public HashMap<String, ?> getResponse() {
		return response;
	}

	public int getStatus() {
		return status;
	}

	public static interface OnSyncListener {
		void onChange(SimpleSyncTask task, int status);

		void onProgress(int cur, int max, String msg);
	}
}
