package com.imbos.chat.setting;

import android.content.Context;

import com.imbos.chat.util.FileUtil;
import com.imbos.chat.util.SharedUtils;

public class Config {

	public String apiKey = "123456789";
	public String version = "1";

	// public String serverHost="61.183.8.11";
	// public String serverPort="80";

	public String serverHost = "182.18.23.218";
	public String serverPort = "8081";

	//
	// public String xmppHost="61.183.8.11";
	// public String xmppPort="5222";

	// public String serverHost="192.168.1.101";
	// public String serverPort="8081";
	// public String serverPath="/imbos-server/api.do?action=send";
	// whchuding.eicp.net:8084/
	public String xmppHost = "192.168.2.52";
	public String xmppPort = "5222";

	// public String saasHost="61.183.8.10";
	// public String saasPort="80";
	// public String saasPath="/m.action";

//	public String saasHost = "whchuding.eicp.net";
	public String saasHost = "58.48.141.63";
	public String saasPort = "8084";
	public String saasPath = "/m.action";

	public String saasToken;
	public String saasLoginame;
	public String saasUid;

	public String fileDir;

	public boolean debug = false;

	private Config() {
	}

	private static Config config;

	public static Config instance(Context ctx) {

		synchronized (Config.class) {
			if (config == null) {

				SharedUtils sharedUtils = SharedUtils.instance(ctx);
				config = new Config();
				config.apiKey = sharedUtils.getString("xmppHost", config.apiKey);

				config.serverHost = sharedUtils.getString("xmppHost", config.serverHost);
				config.serverPort = sharedUtils.getString("xmppHost", config.serverPort);

				config.xmppHost = sharedUtils.getString("xmppHost", config.xmppHost);
				config.xmppPort = sharedUtils.getString("xmppHost", config.xmppPort);

				config.saasHost = sharedUtils.getString("saasHost", config.saasHost);
				config.saasPort = sharedUtils.getString("saasPort", config.saasPort);
				config.saasPath = sharedUtils.getString("saasPath", config.saasPath);
				config.saasToken = sharedUtils.getString("saasToken", config.saasToken);
				config.saasUid = sharedUtils.getString("saasUid", config.saasUid);

				config.saasLoginame = sharedUtils.getString("saasLoginame", config.saasLoginame);

				config.fileDir = FileUtil.getFileDir(ctx);

				if (!sharedUtils.contains("saasHost"))
					save(ctx, config);
			}
		}

		return config;
	}

	public void reset() {
		config = null;
	}

	public void save(Context ctx) {
		SharedUtils.instance(ctx).putString("saasHost", this.saasHost).putString("saasPort", this.saasPort)
				.putString("saasPath", this.saasPath).putString("saasToken", this.saasToken).putString("saasUid", this.saasUid)
				.putString("saasLoginame", this.saasLoginame);
	}

	public static void save(Context ctx, Config config) {
		if (config != null)
			config.save(ctx);
	}
}
