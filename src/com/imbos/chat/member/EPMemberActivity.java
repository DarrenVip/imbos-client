package com.imbos.chat.member;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.imbos.chat.R;
import com.imbos.chat.adapter.MemberArrayAdapter;
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
* 企业好友
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
public class EPMemberActivity extends SkinActivity{
	
	private ListView listView;
	private MemberArrayAdapter arrayAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_recommend);
		
		btnNext.setVisibility(View.INVISIBLE);
		
		setTitle(R.string.TITLE_FRIEND_COMPANY);
		listView = (ListView) findViewById(android.R.id.list);
	    listView.setOnItemClickListener(itemClickListener);
	    
	    arrayAdapter = new MemberArrayAdapter(this,R.layout.member_item){
	    	public View getView(int position, View convertView, ViewGroup parent) {
	    		Member item = getItem(position);
	    		
	    		convertView = super.getView(position, convertView, parent);
	    		
//	    		TextView textView = ((TextView)convertView.findViewById(R.id.btn_operate));
//    			textView.setTag(item.uid);
//    			textView.setOnClickListener(EPMemberActivity.this);
    			
	    		return convertView;
	    		
	    	};
	    };

        listView.setAdapter(arrayAdapter);
        loadMembers();
        syncEPMember();
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_operate:
			//addFriend(StringUtil.toString(view.getTag()),"ok");
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
//			
//			String name = ((TextView)converView.findViewById(R.id.txt_name)).getText().toString();
//			EPMemberDetailViewPagerActivity
			Member item = arrayAdapter.getItem(postion);
			startActivity(new Intent(adapterView.getContext(),EPMemberDetailViewPagerActivity.class)
					.putExtra("eid",item.uid));
			
			
			
		};
	};
	public void loadMembers(){
		arrayAdapter.clear();
		//[EID,NAME,LOGO,VIP,REAL,INDUSTRY]
		SQLiteCursor cursor = DbManager.queryEPMembers();//
			Member member = null;
	    	while (cursor.moveToNext()){
				member = new Member();
				member.uid = StringUtil.toString(cursor.getString(0));
				member.name = StringUtil.toString(cursor.getString(1));
				member.head = StringUtil.toString(cursor.getString(2));
				member.star = cursor.getInt(2);
				member.real = cursor.getInt(3);
				member.doing = StringUtil.toString(cursor.getString(4));
				arrayAdapter.add(member);
			}
	    	cursor.close();
	    	arrayAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 同步企业好友
	 * 函数功能说明 
	 * 修改者名字 
	 * 修改日期 
	 * 修改内容
	 * @author wanxianze@gmail.com 2013-6-6
	 * void
	 */
	public void syncEPMember(){
		
//		HashMap<String,String> args = new HashMap<String, String>();
//		args.put("uid",uid);
//		args.put("note", note);
//		
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this){
					@Override
					public void onFailed() {
						super.onFailed();
						showToast(R.string.SYNC_MSG_FAILED);
					}
					
					@Override
					public void onSuccess() {
						super.onSuccess();
						DbManager.updateTable(Constants.MENTHOD.FIND_ATTENTION);
						loadMembers();
					}
				}).setMethodName(Constants.MENTHOD.FIND_ATTENTION)
			.setArgs(null));
	
	}
	 @Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
