
package com.imbos.chat;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.image.AsyncImageLoader.ImageCallback;
import com.imbos.chat.member.EPMemberActivity;
import com.imbos.chat.member.MemberDetailActivity;
import com.imbos.chat.member.MemberFindAdvActivity;
import com.imbos.chat.member.MemberRecommendActivity;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.StringUtil;
import com.imbos.chat.view.SideBar;
import com.imbos.chat.view.urlimagehelper.UrlImageViewHelper;

/**
 * 联系人界面
 * @author xianze
 *
 */
public class ContactActivity extends SkinActivity {

	private ListView listView;
	private List<Contact> data=new ArrayList<Contact>();
	private ArrayAdapter<Contact> adapter;
	private SideBar indexBar;
	private TextView mDialogText;
	private WindowManager mWindowManager;
	protected AsyncImageLoader asyncImageLoader;
	private ViewGroup headerView;
    private EditText editSearch;
    private boolean isCheck;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);
       
        this. mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
       
        asyncImageLoader = ((ChatApp)getApplication()).getAsyncImageLoader();
        
        btnBack.setVisibility(View.INVISIBLE);
        setButtonIcon(btnNext, R.drawable.mm_title_btn_add_contact_normal);
        setTitle(R.string.MENU_CONTACT);
        
        listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(itemClickListener);
        
        mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.list_position, null);
        mDialogText.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager.addView(mDialogText, lp);
        
        adapter = new ContactAdapter(this,R.layout.contact_item,R.id.txt_name, data);
        
        indexBar = (SideBar) findViewById(R.id.sideBar);
        indexBar.setTextView(mDialogText);
        indexBar.setListView(listView); 
        
        headerView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.search_bar, null);
        editSearch = (EditText) headerView.findViewById(R.id.edit_search);
        
        findViewById(R.id.div_bottom).setVisibility(View.INVISIBLE);
        
        listView.addHeaderView(headerView);
        listView.setAdapter(adapter);
        loadFriends();
        
    }
    @Override
    public void onClick(View view) {
    	switch (view.getId()) {
		case R.id.title_btn_next:
			goActivity(MemberFindAdvActivity.class);
			break;

		default:
			break;
		}
    	super.onClick(view);
    }
    @Override
    protected void onStart() {
    	syncFriends();
    	super.onStart();
    }
    @Override
    protected void handleMessage(Message msg) {
    	if(msg.what==MESSAGE_FINISH){
    		loadFriends();
    	}
    }
    @Override
    protected void onResume() {
    	listView.setAdapter(adapter);
    	super.onResume();
    }
    
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
    	@Override
    	public void onItemClick(AdapterView<?> adapterView, View arg1, int position,
    			long arg3) {
    		
    		Contact item = (Contact) adapterView.getItemAtPosition(position);
    		
    		if("000".equals(item.uid)){
    			goActivity(MemberRecommendActivity.class);
    			return;
    		}
    		if("001".equals(item.uid)){
    			goActivity(EPMemberActivity.class);
    			return;
    		}
    		startActivity(new Intent(ContactActivity.this, ChatActivity.class)
    				.putExtra(Intents.EXTRA_MSG_TOS,item.uid)
    				.putExtra(Intents.EXTRA_TITLE,item.name));
    		
    		
    		
    	}
	};
	
	
	
	
	public void syncFriends(){
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this,true){
					@Override
					public void onSuccess() {
						
						super.onSuccess();
						
						//int flag = DbManager.updateTable(Constants.MENTHOD.FRIENDS);
						loadFriends();
						
					}
				}).setMethodName(Constants.MENTHOD.FRIENDS)
			.setArgs(null));
		
	}
	
	public int countRecommend(){
		Cursor cursor = DbManager.queryRecommend();
		int result =cursor.getCount();
		cursor.close();
		return result;
	}
    
    public void loadFriends(){
    	data.clear();
    	Contact contact = new Contact();
    	contact.uid="000";
    	contact.name = getString(R.string.TITLE_FRIEND_RECOMMEND);
    	contact.pingy = "$";
    	contact.notice = countRecommend();
    	data.add(contact);
    	
    	
    	contact = new Contact();
    	contact.uid="001";
    	contact.name =  getString(R.string.TITLE_FRIEND_COMPANY);
    	contact.pingy = "$";
    	data.add(contact);
    	
    	//SELECT UID,NAME,HEAD,STAR,REAL,DOING,PINGYIN,ALIAS,ALIAS_PINGYIN
    	SQLiteCursor cursor = DbManager.queryFriends();
    	
    	while (cursor.moveToNext()) {
    		contact = new Contact();
    		contact.uid =cursor.getString(0);
    		contact.name =cursor.getString(1);
    		contact.head =cursor.getString(2);
    		contact.start =cursor.getInt(3);
    		contact.real =cursor.getInt(4);
    		contact.doing =StringUtil.toString(cursor.getString(5));
    		contact.pingy =StringUtil.toString(cursor.getString(6));
    		if(contact.start>0){
    			contact.pingy = getString(R.string.MEMBER_STAR);
    		}
    		data.add(contact);
		}
    	cursor.close();
    	adapter.notifyDataSetChanged();
    }
    public class Contact{
    	
    	public String uid;
    	public String name;
    	public String head;
    	public int start;
    	public int real;
    	public String doing;
    	public String pingy;
    	public int notice;
    }
    class ContactAdapter extends ArrayAdapter<Contact> implements SectionIndexer{
	   
	    protected AsyncImageLoader asyncImageLoader = new AsyncImageLoader(this.getContext());
	   
    	public ContactAdapter(Context context,int resource, int textViewResourceId,List<Contact> data) {
			super(context,resource,textViewResourceId,data);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = super.getView(position, convertView, parent);
    		
			Contact item = getItem(position);

			TextView txtName = ((TextView)convertView.findViewById(R.id.txt_name));
			txtName.setText(item.name);
			if(item.real>0){
				txtName.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ico_verify,0);
			}else{
				txtName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,0);
			}
			((TextView)convertView.findViewById(R.id.txt_remark)).setText(item.doing);
    		
    		final ImageView imgFace = (ImageView) convertView.findViewById(R.id.img_face);
    		imgFace.setTag(item);
    		imgFace.setOnClickListener(faceClickListener);
    		
    		TextView txtCategory = (TextView)convertView.findViewById(R.id.txt_category);
    		
    		TextView txtNotice = (TextView)convertView.findViewById(R.id.txt_notice);
    		if(item.notice>0){
    			txtNotice.setVisibility(View.VISIBLE);
    			txtNotice.setText(StringUtil.toString(item.notice));
        	}else
        		txtNotice.setVisibility(View.INVISIBLE);
    			
			String lastPingy = position==0?"":getItem(position-1).pingy;
			String lastCategory = lastPingy.length()>0?lastPingy.substring(0,1):"";
			String category = item.pingy.length()>0?item.pingy.substring(0,1):"";
			if(lastCategory.equalsIgnoreCase(category) || "$".equals(category)){
				txtCategory.setVisibility(View.GONE);
			}else{
				txtCategory.setText(category);
				txtCategory.setVisibility(View.VISIBLE);
			}
			
			
//			asyncImageLoader.loadDrawable(AsyncImageLoader.builderUrl(item.head), new ImageCallback() {
//    	            public void imageLoaded(Drawable drawable, String imageUrl) {
//    	            	if(drawable==null && imgFace!=null){
//    	            		imgFace.setImageResource(R.drawable.default_face);
//    	            	}else if (imgFace != null) {
//    	            		imgFace.setImageDrawable(drawable);
//    	                }
//    	            	
//    	            }
//	    	});
			
			
			if("000".equals(item.uid))
				imgFace.setImageResource(R.drawable.ic_recommend);
			else if("001".equals(item.uid))
				imgFace.setImageResource(R.drawable.ic_company);
			else{
				UrlImageViewHelper.setUrlDrawable(imgFace, AsyncImageLoader.builderUrl(item.head),R.drawable.default_face);
			}
			
			return convertView;
		}
		
		@Override
		public int getPositionForSection(int section) {
			int len = getCount();
			for (int i = 0; i < len; i++) {
				String pingy = getItem(i).pingy;
				if(pingy.length()<1)
					break;
				char firstChar = pingy.toUpperCase().charAt(0); 
				if(firstChar == section)
					return i;
			}
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object[] getSections() {
			// TODO Auto-generated method stub
			return null;
		}
    	OnClickListener faceClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Contact contact = (Contact) v.getTag();
				
				if("000".equals(contact.uid))
					return;
				if("001".equals(contact.uid))
					return;
				
				Context ctx = v.getContext();
				ctx.startActivity(new Intent(ctx,MemberDetailActivity.class)
				.putExtra(Intents.EXTRA_UID,contact.uid));
			}
		};
    }
   
  
   
   
}