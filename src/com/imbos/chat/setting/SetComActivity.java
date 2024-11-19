package com.imbos.chat.setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.imbos.chat.R;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.SharedUtils;

/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class SetComActivity extends SkinActivity{

	private CheckBox cbNotify;
	private CheckBox cbSound;
	private CheckBox cbVibrator;
	private SharedUtils sharedUtils;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_com);
		
		btnNext.setVisibility(View.INVISIBLE);
		//btnBack.setVisibility(View.INVISIBLE);
		
		setTitle(R.string.SET_COM);
		
		sharedUtils = SharedUtils.instance(this);
		
		cbNotify = ((CheckBox)findViewById(R.id.cb_notify));
		cbNotify.setOnCheckedChangeListener(checkedChangeListener);
		
		cbSound = ((CheckBox)findViewById(R.id.cb_sound));
		cbSound.setOnCheckedChangeListener(checkedChangeListener);
		
		cbVibrator = ((CheckBox)findViewById(R.id.cb_vibrator));
		cbVibrator.setOnCheckedChangeListener(checkedChangeListener);
		
		
	}
	
	private void loadSetting(){
		cbNotify.setChecked(sharedUtils.getInt(Constants.SHARED_SET_NOTIFY_ON,0)==1);
		cbSound.setChecked(sharedUtils.getInt(Constants.SHARED_SET_NOTIFY_SOUND,0)==1);
		cbVibrator.setChecked(sharedUtils.getInt(Constants.SHARED_SET_NOTIFY_VIBRATOR,0)==1);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		loadSetting();
	}

	private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch(buttonView.getId()){
			case R.id.cb_notify:
				sharedUtils.putInt(Constants.SHARED_SET_NOTIFY_ON,isChecked?1:0);
				break;
			case R.id.cb_sound:
				sharedUtils.putInt(Constants.SHARED_SET_NOTIFY_SOUND,isChecked?1:0);
				break;
			case R.id.cb_vibrator:
				sharedUtils.putInt(Constants.SHARED_SET_NOTIFY_VIBRATOR,isChecked?1:0);
				break;
			}
		}
	};
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
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
