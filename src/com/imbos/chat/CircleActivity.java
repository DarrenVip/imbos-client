package com.imbos.chat;

import java.util.Date;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.member.EPMemberFindActivity;
import com.imbos.chat.member.EPMemberFindGPSResultActivity;
import com.imbos.chat.member.MemberDetailActivity;
import com.imbos.chat.member.MemberFindAdvActivity;
import com.imbos.chat.member.MemberFindGpsActivity;
import com.imbos.chat.member.MemberFindIdActivity;
import com.imbos.chat.member.TelBookActivity;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.code.scanning.CaptureActivity;

/**
 * 圈子界面
 * @author xianze
 *
 */
public class CircleActivity extends SkinActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle);
		
		
		findViewById(R.id.item_news).setOnClickListener(this);
		findViewById(R.id.item_scan).setOnClickListener(this);
		findViewById(R.id.item_findById).setOnClickListener(this);
		findViewById(R.id.item_findAdv).setOnClickListener(this);
		findViewById(R.id.item_findNearby).setOnClickListener(this);
		findViewById(R.id.item_findContact).setOnClickListener(this);
		findViewById(R.id.item_findCpMember).setOnClickListener(this);
		findViewById(R.id.item_findCpNearby).setOnClickListener(this);
		findViewById(R.id.item_share).setOnClickListener(this);
		
		setTitle(R.string.CIRCLE_TITLE);
		
		btnNext.setVisibility(View.INVISIBLE);
		btnBack.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.item_news:
			showToast(R.string.CIRCLE_NEW_TIP);
			break;
		case R.id.item_scan:
			CaptureActivity.setNextActivity(MemberDetailActivity.class);
			startActivityForResult(new Intent(this,CaptureActivity.class),
					R.id.item_scan);
			break;
		case R.id.item_findById:
			goActivity(MemberFindIdActivity.class);
			break;
		case R.id.item_findAdv:
			goActivity(MemberFindAdvActivity.class);
			break;
		case R.id.item_findNearby:
			goActivity(MemberFindGpsActivity.class);
			break;
		case R.id.item_findCpNearby:
			goActivity(EPMemberFindGPSResultActivity.class);
			break;
		case R.id.item_findCpMember:
			goActivity(EPMemberFindActivity.class); 
			break;
		case R.id.item_findContact:
			goActivity(TelBookActivity.class);
			break;
		default:
			super.onClick(view);
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}
