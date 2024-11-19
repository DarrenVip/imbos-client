package com.imbos.chat.setting;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.StringUtil;

public class AccountActivity extends SkinActivity{

	private String num;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);
		
		btnNext.setVisibility(View.INVISIBLE);
		//btnBack.setVisibility(View.INVISIBLE);
		
		findViewById(R.id.item_phone).setOnClickListener(this);
		findViewById(R.id.item_idcard).setOnClickListener(this);
		findViewById(R.id.item_email).setOnClickListener(this);
		
		findViewById(R.id.item_mdf_pwd).setOnClickListener(this);
		
		bindFields();
		syncUserAccount();
	}
	
	
	public void syncUserAccount(){
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this,true){
					@Override
					public void onFailed() {
						super.onFailed();
					}
					
					@Override
					public void onSuccess() {
						super.onSuccess();
						bindFields();
					}
				}).setMethodName(Constants.MENTHOD.USER_FIND_ACCOUNT)
				.setArgs(null));
	}
	
	private void bindFields(){
		//[UID as _id,UNUM,EMAIL,EMAILCHECK,IDCARD,IDCARDCHECK,MOBILE,MOBILECHECK]
		Map<String,?> map = DbManager.queryUserAccount(ChatApp.getConfig().saasUid);
		
		
		num = StringUtil.toString(map.get("UNUM"));
		
		setFieldValue(R.id.item_num,map.get("UNUM"));
		
		TextView txtPhone= setFieldValue(R.id.item_phone,map.get("MOBILE"));
		if("1".equals(map.get("MOBILECHECK")))
			txtPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_v,0);
		else
			txtPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_uv,0);
		
		TextView txtIdcard = setFieldValue(R.id.item_idcard,map.get("IDCARD"));
		if("1".equals(map.get("IDCARDCHECK")))
			txtIdcard.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_v,0);
		else
			txtIdcard.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_uv,0);
		
		TextView txtEmail = setFieldValue(R.id.item_email,map.get("EMAIL"));
		if("1".equals(map.get("EMAILCHECK")))
			txtEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_v,0);
		else
			txtEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_uv,0);
	}
	
	private TextView setFieldValue(int id,Object value){
		TextView textView = ((TextView)findViewById(id)
				.findViewById(R.id.txt_value));
		textView.setText(StringUtil.toString(value));
		return textView;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.item_phone:
			
			break;
		case R.id.item_idcard:
			break;
		case R.id.item_email:
			break;
		case R.id.item_mdf_pwd:
			if(!StringUtil.isEmpty(num))
			{
				startActivity(new Intent(this,ChangePwdActivity.class)
					.putExtra(Intents.EXTRA_ID,num));
			}
			break;
		case R.id.item_com:
			break;
		default:
			super.onClick(view);
		}
	}
}
