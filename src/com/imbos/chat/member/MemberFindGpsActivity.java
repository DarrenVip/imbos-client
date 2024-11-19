package com.imbos.chat.member;

import java.util.HashMap;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.adapter.MemberArrayAdapter;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.gps.LocationHelper;
import com.imbos.chat.gps.LocationHelper.BdLocationListener;
import com.imbos.chat.model.Member;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.StringUtil;

/**
 * 
* 通过GPS查找
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
public class MemberFindGpsActivity extends SkinActivity{
	
	private ListView listView;
	private MemberArrayAdapter arrayAdapter;
	private Button btnConfirm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_member_gps);
		
		btnNext.setVisibility(View.INVISIBLE);
		
		setTitle(R.string.CIRCLE_FIND_NEARBY);
		btnConfirm =(Button) findViewById(R.id.btn_confirm);
		btnConfirm.setOnClickListener(this);
		btnConfirm.setText("未获取到地理位置信息，点击获取");
		listView = (ListView) findViewById(android.R.id.list);
	    listView.setOnItemClickListener(itemClickListener);
	    
	    arrayAdapter =new MemberArrayAdapter(this){
	    	@Override
	    	public View getView(int position, View convertView, ViewGroup parent) {
	    		convertView =  super.getView(position, convertView, parent);
	    		Member member =getItem(position);
	    		((TextView)convertView.findViewById(R.id.txt_date)).setText(getString(R.string.MEMBER_DISTANCE,member.distance));
	    		return convertView;
	    	}
	    };
		listView.setAdapter(arrayAdapter);
        
		loadMembers();
		
        new LocationHelper(this, locationListener).start();
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_confirm:
			 new LocationHelper(this, locationListener).start();
			//queryMembers();
			break;
		case R.id.title_btn_back:
			finish();
			break;
		default:
			super.onClick(view);
		}
		
	}
	private BdLocationListener locationListener = new BdLocationListener() {
		
		@Override
		public void needTakePicture(boolean take) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void locationListener(double longitude, double latitude,String addr) {
			if(Double.isNaN(longitude) || Double.isNaN(latitude) ){
				
			}else{
				btnConfirm.setText(addr);
				btnConfirm.setTag(longitude+","+latitude);
				queryMembers(longitude, latitude);
			}
		}
	};
	
	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapterView, View converView, int position, long id) {
			
			String name = ((TextView)converView.findViewById(R.id.txt_name)).getText().toString();
			long uid = adapterView.getItemIdAtPosition(position);
			startActivity(new Intent(adapterView.getContext(),MemberDetailActivity.class)
					.putExtra(Intents.EXTRA_TITLE, String.valueOf(name))
					.putExtra(Intents.EXTRA_UID, String.valueOf(uid)));
		};
	};
	public void queryMembers(double longitude,double latitude){
		HashMap<String,Object> args = new HashMap<String, Object>();
		args.put("sex","");
		args.put("industry",null);
		args.put("profession",null);
		args.put("longitude", longitude);
		args.put("latitude",latitude);
		args.put("scope","1");
		args.put("page","1");
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this){
					@Override
					public void onBefore() {
						DbManager.delMembers(Constants.MENTHOD.MEMBER_NEARBY);
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
				}).setMethodName(Constants.MENTHOD.MEMBER_NEARBY)
			.setArgs(args));
	}
	public void loadMembers(){
			arrayAdapter.clear();
	    	SQLiteCursor cursor = DbManager.queryMembers(Constants.MENTHOD.MEMBER_NEARBY);
	    	//[_id,NAME,HEAD,STAR,REAL,DOING,ALIAS ,SEX ,ENTERPRISE ,ISFRIEND , BIRTH ,GRADUATE ,INDUSTRY ,PROFESSION ,POST ,INTEREST ,AREA ,DISTANCE ,UNUM]
	    	Member member = null;
	    	while (cursor.moveToNext()){
				member = new Member();
				member.uid = StringUtil.toString(cursor.getString(0));
				member.name = StringUtil.toString(cursor.getString(1));
				member.head = cursor.getString(2);
				member.star = cursor.getInt(3);
				member.real = cursor.getInt(4);
				member.sex = cursor.getInt(7);
				member.doing = StringUtil.toString(cursor.getString(5));
				member.distance = String .format("%.2f",cursor.getFloat(17)/1000);
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
