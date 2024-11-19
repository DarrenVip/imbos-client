package com.imbos.chat.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

/**
 * 
* 手机联系人
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
public class TelBookActivity extends SkinActivity{
	
	private ListView listView;
	private ArrayAdapter<PhoneContact> arrayAdapter;
	private List<PhoneContact> members = new ArrayList<TelBookActivity.PhoneContact>();
	private Map<String,String> map = new HashMap<String, String>();
	private SimpleSyncListener syncListener;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_telbook);
		
		btnNext.setVisibility(View.INVISIBLE);
		
		setTitle(R.string.TITLE_TELBOOK);
		listView = (ListView) findViewById(android.R.id.list);
	    listView.setOnItemClickListener(itemClickListener);
	    
	    
	    arrayAdapter = new ArrayAdapter<PhoneContact>(this,R.layout.telbook_item,R.id.txt_name,members){
	    	public View getView(int position, View convertView, ViewGroup parent) {
	    		convertView = super.getView(position, convertView, parent);
	    		PhoneContact contact = getItem(position);
	    		
	    		((TextView)convertView.findViewById(R.id.txt_name)).setText(contact.name);
	    		((TextView)convertView.findViewById(R.id.txt_remark)).setText(contact.phone);
	    		Button txtOpreate  = ((Button)convertView.findViewById(R.id.btn_operate));
	    		txtOpreate.setTextColor(Color.GREEN);
	    		txtOpreate.setVisibility(contact.isFriend?View.VISIBLE:View.INVISIBLE);
	    		txtOpreate.setText(contact.isFriend?getString(R.string.GENERAL_VIEW):"");
	    		return convertView;
	    	};
	    };
        
        listView.setAdapter(arrayAdapter);
        
        syncListener = new SimpleSyncListener(this){
        	@Override
        	public void onBefore() {
        		dismissDialog(DIALOG_PROGRESS);
        		super.onBefore();
        	}
    		@Override
    		public void onFailed() {
    			super.onFailed();
    			showToast(R.string.SYNC_MSG_FAILED);
    		}
    		
    		@Override
    		public void onSuccess() {
    			super.onSuccess();
    			loadMembers();
    		}
    	};
        
        //loadMembers();
        try {
			showDialog(DIALOG_PROGRESS);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						queryContact();
						syncTelBook();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}).start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void handleMessage(Message msg) {
		if(msg.what==MSG_SUCCESSS)
		{}
		else
			super.handleMessage(msg);
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
			PhoneContact contact = arrayAdapter.getItem(postion);
			if(contact.isFriend)
				startActivity(new Intent(adapterView.getContext(),MemberDetailActivity.class)
						.putExtra(Intents.EXTRA_UID,contact.uid));
			
		};
	};
	
	public void loadMembers(){
		members.clear();
		SQLiteCursor cursor = DbManager.queryPhoneContact();
		while (cursor.moveToNext()){
			String eid = cursor.getString(0);
			String phone = cursor.getString(1);
			String name =  cursor.getString(2);
			if(!StringUtil.isEmpty(phone)){
				PhoneContact contact = new PhoneContact();
				contact.uid = eid;
				contact.phone = phone;
				contact.name =  StringUtil.isEmpty(name)?map.get(phone):name;
				contact.isFriend = !StringUtil.isEmpty(eid);
				members.add(contact);
			}
				
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
	public void syncTelBook(){
		
		List<String> phoneNumbers = new ArrayList<String>();
		if(map==null||map.isEmpty())
			return;
		String sql = " REPLACE INTO MEMBER_CONTRAST (PHONE,LOCAL_NAME) VALUES (?,?)";
		SQLiteDatabase db = ChatApp.getDbHelper().getWritableDatabase();
		try{
			db.beginTransaction();
			db.delete(Constants.MENTHOD.MEMBER_CONTRAST,null, null);
			
			Set<Entry<String, String>> entries = map.entrySet();
			
			for (Entry<String, String> entry: entries) {
				if(!StringUtil.isEmpty(entry.getKey())){
					phoneNumbers.add(entry.getKey());
					db.execSQL(sql, new Object[]{entry.getKey(),entry.getValue()});
				}
			}
			db.setTransactionSuccessful();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			db.endTransaction();
		}
		HashMap<String,Object> args = new HashMap<String, Object>();
		args.put("phone",phoneNumbers);
//		
		SyncService.start(new SyncTask()
				.setSyncListener(syncListener)
				.setMethodName(Constants.MENTHOD.MEMBER_CONTRAST)
			.setArgs(args));
	
	}
	
	//查询所有联系人的姓名，电话，邮箱
    public void queryContact() throws Exception{        
    	map.clear();
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
       
        while (cursor.moveToNext()) {
        	PhoneContact contact = new PhoneContact();
            int contractID = cursor.getInt(0);
            StringBuilder sb = new StringBuilder("contractID=");
            sb.append(contractID);
            uri = Uri.parse("content://com.android.contacts/contacts/" + contractID + "/data");
            Cursor cursor1 = resolver.query(uri, new String[]{"mimetype", "data1", "data2"}, null, null, null);
            while (cursor1.moveToNext()) {
                String data1 = cursor1.getString(cursor1.getColumnIndex("data1"));
                String mimeType = cursor1.getString(cursor1.getColumnIndex("mimetype"));
                if ("vnd.android.cursor.item/name".equals(mimeType)) { //是姓名
                	contact.name = data1;
                    sb.append(",name=" + data1);
                } else if ("vnd.android.cursor.item/email_v2".equals(mimeType)) { //邮箱
                    sb.append(",email=" + data1);
                    
                } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { //手机
                    sb.append(",phone=" + data1);
                    contact.phone = StringUtil.toString(data1);
                } 
                
            }
            cursor1.close();
            Log.i(TAG, sb.toString());
            if(!StringUtil.isEmpty(contact.phone)){
	            contact.phone = contact.phone.replaceAll("\\D","");
	            map.put(contact.phone, contact.name);
            }
        }
        cursor.close();
        
    
    }
    
    public class PhoneContact{
    	public String uid;
    	public String name;
    	public String phone;
    	public boolean isFriend;
    	
    	@Override
    	public boolean equals(Object o) {
    		if(o instanceof PhoneContact)
    			return StringUtil.toString(uid).equals(((PhoneContact)o).uid);
    		else 
    			return super.equals(o);
    	}
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
