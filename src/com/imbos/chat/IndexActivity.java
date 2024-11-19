
package com.imbos.chat;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.manage.LogicManager;
import com.imbos.chat.model.ChatMessage;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.util.StringUtil;
import com.imbos.chat.view.EmojiGetter;
import com.imbos.chat.view.urlimagehelper.UrlImageViewHelper;
import com.imbos.code.scanning.CaptureActivity;

/**
 *会话界面
 * @author xianze
 *
 */
public class IndexActivity extends SkinActivity {

	private ListView listView;
	private List<String[]> data=new ArrayList<String[]>();
	private ArrayAdapter<String[]> adapter;
	private ViewGroup headerView;
    private EditText editSearch;
    private AsyncImageLoader asyncImageLoader;
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	

        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        
        btnBack.setVisibility(View.INVISIBLE);
        
        setButtonIcon(btnNext, R.drawable.title_btn_function);
        
        listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(itemClickListener);
        listView.setOnItemLongClickListener(itemLongClickListener);
        
        
        asyncImageLoader = ((ChatApp)getApplication()).getAsyncImageLoader();
        
        adapter = new ArrayAdapter<String[]>(this,R.layout.session_item,R.id.txt_name, data){
        	public View getView(int position, View convertView, ViewGroup parent) {
        		convertView = super.getView(position, convertView, parent);
        		
        		String[] item = getItem(position);
        		String unread = item[5];
        		
        		
        		((TextView)convertView.findViewById(R.id.txt_name)).setText(item[2]);
        		((TextView)convertView.findViewById(R.id.txt_date)).setText(item[4]);
        		TextView txtNotice = ((TextView)convertView.findViewById(R.id.txt_notice));
        		
        		txtNotice.setText(unread);
        		if("0".equals(unread))
        			txtNotice.setVisibility(View.INVISIBLE);
        		else
        			txtNotice.setVisibility(View.VISIBLE);
        		
        		TextView txtMsg = (TextView)convertView.findViewById(R.id.txt_remark);
        		txtMsg.setText(Html.fromHtml(item[3],new EmojiGetter(getContext(), txtMsg), null));
        		
        		
        		final ImageView imgFace = (ImageView) convertView.findViewById(R.id.img_face);
//        		asyncImageLoader.loadDrawable(AsyncImageLoader.builderUrl(item[1]), new AsyncImageLoader.ImageCallback() {
//					
//					@Override
//					public void imageLoaded(Drawable drawable, String imageUrl) {
//						if(drawable!=null)
//							imgFace.setImageDrawable(drawable);
//					}
//				});
        		UrlImageViewHelper.setUrlDrawable(imgFace, AsyncImageLoader.builderUrl(item[1]),R.drawable.default_face);
        		return convertView;
        	};
        };
        
        headerView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.search_bar, null);
        editSearch = (EditText) headerView.findViewById(R.id.edit_search);
        
        listView.addHeaderView(headerView);
        listView.setAdapter(adapter);
       
    }
    @Override
    public void onClick(View view) {
    	switch (view.getId()) {
		case R.id.title_btn_next:
			startActivityForResult(new Intent(this,MainDialog.class),R.id.title_btn_next);
			break;

		default:
			super.onClick(view);
		}
    	
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode==RESULT_CANCELED)
    		return;
    	if(requestCode == R.id.title_btn_next){
	    	int menuId = data.getIntExtra(Intents.EXTRA_MENU_ID, 0);
	    	onMenuClick(menuId);
    	}else
    		super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void onMenuClick(int menuId){
    	switch (menuId) {
		case R.id.menu_chat:
			goActivity(ContactActivity.class);
			break;
		case R.id.menu_qrcode:  
			goActivity(CaptureActivity.class);
			break;
		default:
			break;
		}
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    }
    @Override
    protected void onResume() {
    	loadSessions();
    	listView.setAdapter(adapter);
    	super.onResume();
    }
    
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
    	@Override
    	public void onItemClick(AdapterView<?> adapterView, View arg1, int position,
    			long arg3) {
    		
    		String[] item = (String[]) adapterView.getAdapter().getItem(position);
    		
    		startActivity(new Intent(IndexActivity.this, ChatActivity.class)
    				.putExtra(Intents.EXTRA_TITLE,item[2])
    				.putExtra(Intents.EXTRA_MSG_TOS,item[0]));
    	}
	};
	private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
		
		@Override
		public boolean onItemLongClick(AdapterView<?> adapterView, View arg1,
				int position, long arg3) {
			String[] item = (String[]) adapterView.getAdapter().getItem(position);
			final String uid = item[0];
			new AlertDialog.Builder(adapterView.getContext())
				.setTitle(R.string.GENERAL_TIP)
				.setItems(R.array.SESSION_OPTIONS,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which==0){
							DbManager.topSession(uid);
							loadSessions();
						}else if(which==1){
							DbManager.delSession(uid);
							loadSessions();
						}
					}
				}).create().show();
			return false;
		}
    	
	};
	
    
    public void loadSessions(){
    	data.clear();
    	//[UID,HEAD,NAME,LAST_MESSAGE,LAST_DATE,UNREAER]
    	SQLiteCursor cursor = DbManager.querySessions();
    	while (cursor.moveToNext()) {
    		
    		String content = cursor.getString(3);
    		String fromsName = LogicManager.findUserName(cursor.getString(0));
    		fromsName = StringUtil.isEmpty(fromsName)?this.getString(R.string.CHAT_TEMP):fromsName;
    		ChatMessage msg = new ChatMessage();
    		msg.content = StringUtil.toString(content);
    		String[] item = new String[]{
				cursor.getString(0),
				cursor.getString(1),
				fromsName,
				msg.toString(true),
				DateUtil.toDateString(DateUtil.formatStr2Date(cursor.getString(4))),
				cursor.getString(5)
			};
			data.add(item);
		}
    	cursor.close();
    	adapter.notifyDataSetChanged();
    }
    
}