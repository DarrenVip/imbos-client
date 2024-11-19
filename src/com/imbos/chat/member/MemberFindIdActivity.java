package com.imbos.chat.member;

import java.util.HashMap;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.adapter.MemberArrayAdapter;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.model.Member;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.StringUtil;
import com.imbos.chat.view.ToggleButton;

/**
 * 
* 通过id或昵称查找
* 类修改者	修改日期
* 修改说明
* <p>Title: MemberNameActivity.java</p>
* <p>Description:SFA</p>
* <p>Copyright: Copyright (c) 2012</p>
* <p>Company:ebestmobile</p>
* @author wanxianze@gmail.com
* @date 2013-4-10 下午4:40:03
* @version V1.0
 */
public class MemberFindIdActivity extends SkinActivity{
	
	private ToggleButton btnType;
	private EditText editSearch;
	private ListView listView;
	private MemberArrayAdapter arrayAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_member_id);
		
		btnType = (ToggleButton) findViewById(R.id.btn_type);
		editSearch = (EditText) findViewById(R.id.edit_search);
		String code = getIntent().getStringExtra(Intents.ACTION_DATA);
		
		btnNext.setVisibility(View.INVISIBLE);
		
		setTitle(R.string.CIRCLE_FIND_BYID);
		findViewById(R.id.btn_confirm).setOnClickListener(this);
		
		arrayAdapter = new MemberArrayAdapter(this);
		
		listView = (ListView) findViewById(android.R.id.list);
	    listView.setOnItemClickListener(itemClickListener);
	    listView.setAdapter(arrayAdapter);
	    
	    
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_confirm:
			if(ToggleButton.LEFT_CHOOSED == btnType.getChoose())
				queryMembers();
			else
				queryEPMembers();
			break;
		case R.id.title_btn_back:
			finish();
			break;
		default:
			super.onClick(view);
		}
		
	}
	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapterView, View converView, int position, long id) {
			
			String name = ((TextView)converView.findViewById(R.id.txt_name)).getText().toString();
			long uid = adapterView.getAdapter().getItemId(position);
			if("6".equals(arrayAdapter.getItem(position).unum)){
				
				startActivity(new Intent(adapterView.getContext(),EPMemberDetailViewPagerActivity.class)
				.putExtra("eid", String.valueOf(uid)));
			}else{
				startActivity(new Intent(adapterView.getContext(),MemberDetailActivity.class)
				.putExtra(Intents.EXTRA_TITLE, String.valueOf(name))
				.putExtra(Intents.EXTRA_UID, String.valueOf(uid)));
			}
		};
	};
	
	
	public void queryMembers(){
		HashMap<String,String> args = new HashMap<String, String>();
		args.put("key", editSearch.getText().toString());
		args.put("page","1");
		DbManager.delMembers(Constants.MENTHOD.MEMBER_FINDID);
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this){
					@Override
					public void onBefore() {
						super.onBefore();
					}
					@Override
					public void onFailed() {
						super.onFailed();
					}
					@Override
					public void onSuccess() {
						super.onSuccess();
						loadMembers();
						
					}
				}).setMethodName(Constants.MENTHOD.MEMBER_FINDID)
			.setArgs(args));
	}
	public void queryEPMembers(){
		HashMap<String,String> args = new HashMap<String, String>();
		args.put("key", editSearch.getText().toString());
		args.put("page","1");
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this){
					@Override
					public void onBefore() {
						super.onBefore();
						DbManager.delTable(Constants.MENTHOD.EMEMBER_FINDID,null);
					}
					@Override
					public void onSuccess() {
						super.onSuccess();
						loadEPMembers();
						
					}
				}).setMethodName(Constants.MENTHOD.EMEMBER_FINDID)
			.setArgs(args));
	}
	 public void loadEPMembers(){
		 	arrayAdapter.clear();
	    	SQLiteCursor cursor = DbManager.searchEPMember();
	    	//[EID,NAME,LOGO,VIP,REAL,INDUSTRY]
	    	Member member = null;
	    	while (cursor.moveToNext()){
				member = new Member();
				
				member.uid = StringUtil.toString(cursor.getString(0));
				member.name = StringUtil.toString(cursor.getString(1));
				member.head = cursor.getString(2);
				member.star = cursor.getInt(3);
				member.real = cursor.getInt(4);
				member.doing = StringUtil.toString(cursor.getString(5));
				member.unum = "6";
				arrayAdapter.add(member);
			}
	    	cursor.close();
	    	arrayAdapter.notifyDataSetChanged();
	  }
	 
	 	public void loadMembers(){
	 		arrayAdapter.clear();
	    	SQLiteCursor cursor = DbManager.queryMembers(Constants.MENTHOD.MEMBER_FINDID);
	    	//[_id,NAME,HEAD,STAR,REAL,DOING]
	    	Member member = null;
	    	while (cursor.moveToNext()){
				member = new Member();
				member.uid = StringUtil.toString(cursor.getString(0));
				member.name = StringUtil.toString(cursor.getString(1));
				member.head = cursor.getString(2);
				member.star = cursor.getInt(3);
				member.real = cursor.getInt(4);
				member.doing = StringUtil.toString(cursor.getString(5));
				
				arrayAdapter.add(member);
			}
	    	cursor.close();
	    	arrayAdapter.notifyDataSetChanged();
	    }
	 
	 @Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
