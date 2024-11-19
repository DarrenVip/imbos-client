package com.imbos.chat;

import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.setting.AboutActivity;
import com.imbos.chat.setting.AccountActivity;
import com.imbos.chat.setting.SetComActivity;
import com.imbos.chat.setting.UserCardActivity;
import com.imbos.chat.util.StringUtil;

public class MoreActivity extends SkinActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
		
		btnNext.setVisibility(View.INVISIBLE);
		btnBack.setVisibility(View.INVISIBLE);
		
		setTitle(R.string.MENU_MORE);
		
		findViewById(R.id.item_account).setOnClickListener(this);
		findViewById(R.id.item_card).setOnClickListener(this);
		findViewById(R.id.item_ui).setOnClickListener(this);
		findViewById(R.id.item_safe).setOnClickListener(this);
		findViewById(R.id.item_com).setOnClickListener(this);
		findViewById(R.id.item_clear).setOnClickListener(this);
		findViewById(R.id.item_about).setOnClickListener(this);

		findViewById(R.id.btn_green).setOnClickListener(this);
		
		
		
	}
	
	@Override
	protected void onStart() {
		bindNum();
		super.onStart();
	}

	private void bindNum() {
		Map<String,?> map = DbManager.queryUserAccount(ChatApp.getConfig().saasUid);
		String num = StringUtil.toString(map.get("UNUM"));
		((TextView)findViewById(R.id.item_account)
				.findViewById(R.id.txt_value))
				.setText(num);
	}
	

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.item_account:
			goActivity(AccountActivity.class);
			break;
		case R.id.item_card:
			goActivity(UserCardActivity.class);
			break;
		case R.id.item_ui:
			break;
		case R.id.item_safe:
			break;
		case R.id.item_com:
			goActivity(SetComActivity.class);
			break;
		case R.id.item_clear:
			showDialog(R.id.item_clear);
			break;
		case R.id.item_about:
			goActivity(AboutActivity.class);
			break;
		case R.id.btn_green:
			((ChatApp)this.getApplication()).logout();
			finish();
			break;
		default:
			super.onClick(view);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this)
		.setTitle(R.string.GENERAL_TIP)
		.setMessage(R.string.SET_MSG_CLEAR)
		.setPositiveButton(R.string.GENERAL_CONFIRM,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DbManager.clearAllMessages();
			}
		})
		.setNegativeButton(R.string.GENERAL_CANCEL,null).create();
	}
}
