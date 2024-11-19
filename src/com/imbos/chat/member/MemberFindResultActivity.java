package com.imbos.chat.member;

import java.util.HashMap;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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
public class MemberFindResultActivity extends SkinActivity{
	
	private EditText editSearch;
	private ListView listView;
	private MemberArrayAdapter arrayAdapter;
	private HashMap<String,Object> searchCond = new HashMap<String,Object>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_member_result);
		
		searchCond = (HashMap<String, Object>) getIntent().getSerializableExtra(Intents.EXTRA_DATA);
		
		btnNext.setVisibility(View.INVISIBLE);
		
		setTitle(R.string.M_FIND_RESULT);
		
		listView = (ListView) findViewById(android.R.id.list);
		View vEmpty = LayoutInflater.from(this).inflate(R.layout.empty, null);
		listView.setEmptyView(vEmpty);
		
		arrayAdapter = new MemberArrayAdapter(this);
	    listView.setOnItemClickListener(itemClickListener);
	    listView.setAdapter(arrayAdapter);
	    
	    loadMembers();
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_confirm:
			queryMembers();
			break;
		case R.id.title_btn_back:
			finish();
			break;
		default:
			super.onClick(view);
		}
		
	}
	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapterView, View converView, int postion, long id) {
			
			String name = ((TextView)converView.findViewById(R.id.txt_name)).getText().toString();
			
			startActivity(new Intent(adapterView.getContext(),MemberDetailActivity.class)
					.putExtra(Intents.EXTRA_TITLE, String.valueOf(name))
					.putExtra(Intents.EXTRA_UID, String.valueOf(id)));
		};
	};
	public void queryMembers(){
		HashMap<String,String> args = new HashMap<String, String>();
		args.put("key", editSearch.getText().toString());
		args.put("page","1");
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this){
					@Override
					public void onBefore() {
						super.onBefore();
						DbManager.delMembers(Constants.MENTHOD.MEMBER_FINDADV);
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
				}).setMethodName(Constants.MENTHOD.MEMBER_FINDADV)
			.setArgs(args));
	}
	public void loadMembers(){
    	SQLiteCursor cursor = DbManager.queryMembers(Constants.MENTHOD.MEMBER_FINDADV);
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
	
}
