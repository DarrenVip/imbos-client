package com.imbos.chat.member;

import java.util.HashMap;

import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.adapter.MemberArrayAdapter;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.manage.LogicManager;
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
public class MemberRecommendActivity extends SkinActivity{
	
	private ListView listView;
	private MemberArrayAdapter arrayAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_recommend);
		
		btnNext.setVisibility(View.INVISIBLE);
		
		setTitle(R.string.TITLE_FRIEND_RECOMMEND);
		listView = (ListView) findViewById(android.R.id.list);
	    listView.setOnItemClickListener(itemClickListener);
	    
	    arrayAdapter = new MemberArrayAdapter(this,R.layout.member_op_item){
	    	public View getView(int position, View convertView, ViewGroup parent) {
	    		Member item = getItem(position);
	    		
	    		convertView = super.getView(position, convertView, parent);
	    		
	    		TextView textView = ((TextView)convertView.findViewById(R.id.btn_operate));
    			textView.setTag(item.uid);
    			textView.setOnClickListener(MemberRecommendActivity.this);
    			
	    		return convertView;
	    		
	    	};
	    };
        
        listView.setAdapter(arrayAdapter);
        loadMembers();
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_operate:
			addFriend(StringUtil.toString(view.getTag()),"ok");
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
//			
//			startActivity(new Intent(adapterView.getContext(),MemberDetailActivity.class)
//					.putExtra(Intents.EXTRA_TITLE, String.valueOf(name))
//					.putExtra(Intents.EXTRA_UID, String.valueOf(id))
//					);
			
			
			
		};
	};
	
	public void loadMembers(){
		arrayAdapter.clear();
		//[UID,NAME,HEAD,STAR,REAL,DOING,PINGYIN,ALIAS,ALIAS_PINGYIN,NOTE]
		SQLiteCursor cursor = DbManager.queryRecommend();//_id,NAME,HEAD,STAR,REAL,DOING
			Member member = null;
	    	while (cursor.moveToNext()){
				member = new Member();
				member.uid = StringUtil.toString(cursor.getString(0));
				member.name = StringUtil.toString(cursor.getString(1));
				member.head = StringUtil.toString(cursor.getString(2));
				member.star = cursor.getInt(2);
				member.real = cursor.getInt(3);
				member.doing = StringUtil.toString(cursor.getString(9));
				arrayAdapter.add(member);
				
			}
	    	cursor.close();
	    	arrayAdapter.notifyDataSetChanged();
	}
	
	public void addFriend(final String uid,String note){
		
		HashMap<String,String> args = new HashMap<String, String>();
		args.put("uid",uid);
		args.put("note", note);
		
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this){
					@Override
					public void onFailed() {
						super.onFailed();
						showToast(R.string.GENERAL_MSG_SEND_FAILED);
					}
					
					@Override
					public void onSuccess() {
						super.onSuccess();
						LogicManager.delFriend(uid);
						loadMembers();
						showToast(R.string.GENERAL_MSG_SEND_FINISH);
						
					}
				}).setMethodName(Constants.MENTHOD.FRIEND_ADD)
			.setArgs(args));
	
	}
	 @Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
