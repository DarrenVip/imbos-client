package com.imbos.chat;


import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.base.BaseActivityGroup;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.sync.UpdateService;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.SharedUtils;
import com.imbos.chat.util.StringUtil;
import com.imbos.client.MessageService;

public class MainActivity extends  BaseActivityGroup{
	
	
	private ViewGroup container;
	private Window subActivity=null;
	private List<MenuView>  menus= new ArrayList<MenuView>();
	int mCurTab = 0;
	private long flagTimeMillis=System.currentTimeMillis();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		container = (ViewGroup) findViewById(R.id.container);
		
		createMenu();
		
		//new ServiceManager(this).startService();
		
		MessageService.startForeground(this);
		
		mHandler.sendEmptyMessageDelayed(1111, 100);
		
		if(!SharedUtils.instance(this).contains(Constants.XMPP_PASSWORD)){
			startActivity(new Intent(this, LoginActivity.class)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			this.finish();
		}
		
		
		if(!StringUtil.isEmpty(ChatApp.updateAppVersion)) {
			if(!ChatApp.updateAppVersion.equals(ChatApp.appVersion)) {
				checkVersion(AsyncImageLoader.builderUrl(ChatApp.updateAppUrl));
			}
		}
		
		syncBaseData();
	}
	@Override
	protected void onStart() {
		super.onStart();
	}
	@Override
	protected void onNewIntent(Intent intent) {
		if(intent.hasExtra(Intents.EXTRA_MSG_TOS)){
			startActivity(new Intent(this,ChatActivity.class)
					.putExtras(intent));
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		default:
			super.onClick(v);;
		}
		super.onClick(v);
	}
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1111:
				switchActivity(0);
				break;
			}
		}

	};
	
	
	
	private void switchActivity(MenuView menu) {
		container.removeAllViews();
		Class<?> clazz = menu.clazz;
		if(clazz==null)
			return;
		
		for (MenuView item : menus) {
			item.setSelected(item.equals(menu));
		}
		
		Intent intent = new Intent(this,clazz);
	
		String name = clazz.getSimpleName() + " subactivity";
		// Activity 转为 View
		subActivity = getLocalActivityManager().startActivity(name,
				intent);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT, 1);
		// 容器添加View
		container.addView(subActivity.getDecorView(),layoutParams);
	}
	
	private void switchActivity(int index) {
		container.removeAllViews();
		Class<?> clazz = menus.get(index).clazz;
		if(clazz==null)
			return;
		Intent intent = new Intent(this,clazz);
	
		String name = index + " subactivity";
		// Activity 转为 View
		subActivity = getLocalActivityManager().startActivity(name,
				intent);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT, 1);
		// 容器添加View
		container.addView(subActivity.getDecorView(),layoutParams);
	}
	
	
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		
	};
	
	@Override
	public void onBackPressed() {
		long currTimeMillis = System.currentTimeMillis();
		// 3秒退出
		if ((currTimeMillis - flagTimeMillis) < 3 * 1000) {
			super.onBackPressed();
		} else {
			showToast("再按一次退出");
		}
		flagTimeMillis = currTimeMillis;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void createMenu() {
		MenuView menuSession = new MenuView(R.id.menu_session);
		menuSession.backgroundNormal = R.drawable.menu_session;
		menuSession.backgroundPressed = R.drawable.menu_session_pressed;
		menuSession.imageNormal = R.drawable.ico_session_normal;
		menuSession.imagePressed = R.drawable.ico_session_pressed;
		menuSession.clazz = IndexActivity.class;
		
		
		MenuView menuContact = new MenuView(R.id.menu_contact);
		menuContact.backgroundNormal = R.drawable.menu_contact;
		menuContact.backgroundPressed = R.drawable.menu_contact_pressed;
		menuContact.imageNormal = R.drawable.ico_contact_normal;
		menuContact.imagePressed = R.drawable.ico_contact_pressed;
		menuContact.clazz = ContactActivity.class;
		
		MenuView menuCircle= new MenuView(R.id.menu_circle);
		menuCircle.backgroundNormal = R.drawable.menu_circle;
		menuCircle.backgroundPressed = R.drawable.menu_circle_pressed;
		menuCircle.imageNormal = R.drawable.ico_circle_normal;
		menuCircle.imagePressed = R.drawable.ico_circle_pressed;
		menuCircle.clazz = CircleActivity.class;
		
		MenuView menuFind = new MenuView(R.id.menu_find);
		menuFind.backgroundNormal = R.drawable.menu_find;
		menuFind.backgroundPressed = R.drawable.menu_find_pressed;
		menuFind.imageNormal = R.drawable.ico_find_normal;
		menuFind.imagePressed = R.drawable.ico_find_pressed;
		menuFind.clazz = FindActivity.class;
		
		MenuView menuMore = new MenuView(R.id.menu_set);
		menuMore.backgroundNormal = R.drawable.menu_set;
		menuMore.backgroundPressed = R.drawable.menu_set_pressed;
		menuMore.imageNormal = R.drawable.ico_set_normal;
		menuMore.imagePressed = R.drawable.ico_set_pressed;
		menuMore.clazz = MoreActivity.class;
		
		
		menus.add(menuSession);
		menus.add(menuContact);
		menus.add(menuCircle);
		menus.add(menuFind);
		menus.add(menuMore);
	}
	
	class MenuView{
		public ViewGroup selfView;
		
		public CheckBox checkBox;
		public TextView textName;
		public ImageView imgIcon;
		
		public int backgroundNormal;
		public int backgroundPressed;
		public int imageNormal;
		public int imagePressed;
		
		public MenuView(int resId) {
			selfView = (ViewGroup) findViewById(resId);
			checkBox = (CheckBox) selfView.findViewById(R.id.checkBox);
			checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					switchActivity(MenuView.this);
				}
			});
			textName = (TextView) selfView.findViewById(R.id.txt_name);
			imgIcon = (ImageView) selfView.findViewById(R.id.img_ico);
		}
		
		public Class<?> clazz;
		
		public void setSelected(boolean isSelected){
			if(isSelected){
				checkBox.setBackgroundResource(backgroundPressed);
				imgIcon.setImageResource(imagePressed);
			}else{
				checkBox.setBackgroundResource(backgroundNormal);
				imgIcon.setImageResource(imageNormal);
			}
		}
	}
	public void syncBaseData(){
		
		
		SyncService.start(new SyncTask().setSyncListener(
				new SimpleSyncListener(this, true)).setMethodName(
				Constants.MENTHOD.FRIENDS));
		
		SyncService.start(new SyncTask().setSyncListener(
				new SimpleSyncListener(this, true)).setMethodName(
				Constants.MENTHOD.USER_SET));

		SyncService.start(new SyncTask().setSyncListener(
				new SimpleSyncListener(this, true)).setMethodName(
				Constants.MENTHOD.COMMON_DIC));
	}
	/**
	 * 检查更新版本
	 */
	private void checkVersion(final String url) {
		// 发现新版本，提示用户更新
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setCancelable(false);
		alert.setTitle("软件升级").setMessage("发现新版本，建议立即更新使用.").setPositiveButton("更新", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// 开启更新服务UpdateService
				updateVersion(url);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		if(!this.isFinishing()) {
			alert.create().show();
		}
	}
	
	private void updateVersion(String url) {
		Intent service = new Intent();
		service.setClass(this, UpdateService.class);
		service.putExtra("titleId", R.string.app_name);
		service.putExtra("updateUrl", url);
		startService(service);
	}
}
