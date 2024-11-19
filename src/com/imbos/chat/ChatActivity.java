
package com.imbos.chat;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteCursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.imbos.chat.adapter.ChatMessageAdapter;
import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.image.AsyncImageLoader.ImageCallback;
import com.imbos.chat.image.ImageActivity;
import com.imbos.chat.member.MemberDetailActivity;
import com.imbos.chat.model.ChatMessage;
import com.imbos.chat.model.ChatMessage.Type;
import com.imbos.chat.sync.MessageTask;
import com.imbos.chat.util.BitmapUtil;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.util.FileUtil;
import com.imbos.chat.util.MediaPlayUtil;
import com.imbos.chat.util.SharedUtils;
import com.imbos.chat.util.MediaPlayUtil.OnChangeListener;
import com.imbos.chat.util.MediaRecordUtil;
import com.imbos.chat.util.StringUtil;
import com.imbos.chat.util.VibratorUtil;
import com.imbos.chat.view.ChatInputView;
import com.imbos.chat.view.EmojiGetter;
import com.imbos.chat.view.ResizeLayout;
import com.imbos.chat.view.urlimagehelper.UrlImageViewHelper;
import com.imbos.client.MessageService;

/**
 * 聊天界面
 * @author xianze
 *
 */
public class ChatActivity extends SkinActivity {

	protected static final String TAG = ChatActivity.class.getSimpleName();
	private ListView listView;
	private TextView headerView;
	private ChatInputView inputView;
	 
	private List<ChatMessage> data=new ArrayList<ChatMessage>();
	private ChatMessageAdapter adapter;
	private MediaRecordUtil mediaRecorder;
	private MediaPlayUtil mediaPlayer;
	private String tos;
	private String froms;
	private InputMethodManager imm;
	private AsyncImageLoader asyncImageLoader;
	private AsyncImageLoader asyncTempLoader;
	private String fromsHead,tosHead;
    @Override
    public void onCreate(Bundle savedInstanceState) { 
       
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        
        this.getWindow()
		.setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setButtonIcon(btnNext,R.drawable.mm_title_btn_contact_normal);
        
        mediaRecorder = new MediaRecordUtil(this);
        mediaPlayer = new MediaPlayUtil(this);
        mediaPlayer.setOnChangeListener(changeListener);
        
        asyncImageLoader = ((ChatApp)getApplication()).getAsyncImageLoader();
        asyncTempLoader = ((ChatApp)getApplication()).getAsyncTempLoader();
        
        imm= (InputMethodManager)(this.getSystemService(Context.INPUT_METHOD_SERVICE));
        
        if(getIntent().hasExtra(Constants.EXTRA_MSG_FROMS))
        	froms = getIntent().getStringExtra(Intents.EXTRA_MSG_FROMS);
        else
        	froms = ChatApp.getConfig().saasUid;
        
        if(getIntent().hasExtra(Intents.EXTRA_MSG_TOS))
        	tos = getIntent().getStringExtra(Intents.EXTRA_MSG_TOS);
        
        setTitle(getIntent().getStringExtra(Intents.EXTRA_TITLE));
        
        
        queryHeader();
        
        bindListView();

        inputView = (ChatInputView) findViewById(R.id.div_input);
        inputView.setTos(tos);
        inputView.setFroms(froms);
        inputView.setOnTypeItemClickListener(this);
        
        ResizeLayout resizeLayout = (ResizeLayout) findViewById(R.id.div_main);
        resizeLayout.setOnResizeListener(onResizeListener);
        
        
        
    }
    
    
	public void bindListView() {
		listView = (ListView) findViewById(android.R.id.list);
		headerView = new TextView(this);
//		headerView.setText("历史记录");
//		listView.addHeaderView(headerView);
		
		adapter = new ChatMessageAdapter(this,R.layout.chat_item,R.id.txt_msg, data);
		adapter.setFromsHead(fromsHead);
		adapter.setTosHead(tosHead);
		adapter.setAsyncImageLoader(asyncImageLoader);
		
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	}
	public void queryHeader() {
		SQLiteCursor cursor = DbManager.queryFriend(tos);
        if(cursor.moveToNext()){
        	tosHead = cursor.getString(2);
        }
        cursor.close();
        
        Map<String,?> map = DbManager.queryUserCard(ChatApp.getConfig().saasUid);
        fromsHead = StringUtil.toString(map.get("HEAD"));
        
	}
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	IntentFilter intentFilter = new IntentFilter();
    	intentFilter.addAction(Constants.ACTION_MESSAGE_SEND);
    	intentFilter.addAction(Constants.ACTION_MESSAGE_RECEVER);
    	this.registerReceiver(messageReceiver,intentFilter);
    	
    	
    	
    	MessageService.bindNotifiyOnClickEvent(this);
    	
    	loadMessages();
    }

    @Override
    protected void onStop() {
    	this.unregisterReceiver(messageReceiver);
    	super.onStop();
    }
    private void loadMessages(){
    	//[_id,FROMS,TOS,CONTENT,CREATE_DATE,DIRECT]
    	SQLiteCursor cursor = DbManager.queryMessages(tos);
    	data.clear();
    	Date lastTime = new Date();
    	while (cursor.moveToNext()) {
			ChatMessage item = new ChatMessage();
			
			item.id = cursor.getString(0);
			item.froms = cursor.getString(1);
			item.tos = cursor.getString(2);
			item.content = cursor.getString(3);
			item.createDate = cursor.getString(4);
			item.recevieDate =cursor.getString(4);
			item.direction = cursor.getInt(5);
			item.date = DateUtil.formatStr2Date(item.recevieDate);
			if(lastTime.after(item.date) || (item.date.getTime()-lastTime.getTime())>1000*60*10){
				item.showDate = DateUtil.toDateString(DateUtil.formatStr2Date(item.recevieDate));
				lastTime = item.date;
			}
			item.showDate = DateUtil.toDateString(lastTime);
			
			data.add(0,item);
		}
    	cursor.close();
    	DbManager.updateChatMessage(tos);
    	adapter.notifyDataSetChanged();
    }
    
   
    
	
	private ImageView playView = null;
    private AdapterView.OnItemClickListener  itemClickListener = new AdapterView.OnItemClickListener() {
    	@Override
    	public void onItemClick(AdapterView<?> adapterView, View view, int position,
    			long id) {
    		playView = (ImageView) view.findViewById(R.id.img_voice);
    		ChatMessage msg = (ChatMessage) adapterView.getItemAtPosition(position);
    		if(msg.getType()==Type.SOUND){
    			mediaPlayer.start(msg.content.replace(Constants.PREFIX_FILE,""));
//    			AnimationDrawable drawable = (AnimationDrawable) playView.getDrawable();
//    			drawable.start();
    		}else if(msg.getType()==Type.IMAGE){
    			startActivity(new Intent(view.getContext(),ImageActivity.class)
    					.putExtra(Intents.EXTRA_DATA,msg.getFilePath()));
    		}
    		
    	}
	};
    
	private ResizeLayout.OnResizeListener  onResizeListener = new ResizeLayout.OnResizeListener() {
		
		private int lastHeight;
		private int maxHeight;
		private int keybordHeight;
		private int inputHeight;
		
		@Override
		public void onResize(boolean changed, int l, int t, int r, int b) {
//			int currHeight = b-t;
//			Log.d(TAG, String.format("onResize  changed %1$s,  l %2$s, t %3$s, r %4$s,  b %5$s",changed,l,t,r,b));
//			if(changed && maxHeight>0){
//				
//				if(currHeight<maxHeight){
//					if(inputHeight==0 || keybordHeight==0){
//						if(inputView.getBottomVisibility()==View.VISIBLE){
//							inputHeight = maxHeight-currHeight;
//						}else{
//							keybordHeight = maxHeight-currHeight;
//						}
//					}
//				}
//				
//				if((currHeight)<maxHeight){
//					if(inputView.getBottomVisibility()==View.VISIBLE)
//						inputView.hideBottomDiv();
//				}
//			}
//			lastHeight = currHeight;
//			maxHeight = lastHeight>maxHeight?lastHeight:maxHeight;
		}
	};
	
	
	
	private OnChangeListener changeListener = new OnChangeListener() {
		
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
//			AnimationDrawable drawable = (AnimationDrawable) playView.getDrawable();
//			drawable.stop();
			mp.release();
			return true;
		}
		
		@Override
		public void onCompletion(MediaPlayer mp) {
//			AnimationDrawable drawable = (AnimationDrawable) playView.getDrawable();
//			drawable.stop();
		}
	};
    
    public void onClick(View view) {
    	switch (view.getId()) {
		case R.id.title_btn_next:
			startActivity(new Intent(this, MemberDetailActivity.class)
					.putExtra(Intents.EXTRA_UID, String.valueOf(tos)));
			break;
		case R.id.img_btn:
			onTypeClick((Integer)view.getTag());
			break;
		default :
			super.onClick(view);
		}
    }
    
    private void onTypeClick(int key){
    	switch (key) {
	    	case R.string.CHAT_MSGTYPE_EMOJI:
				inputView.showEmoji();
				break;
			case R.string.CHAT_MSGTYPE_PIC:
				startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT)
				.setType("image/*"),R.string.CHAT_MSGTYPE_PIC);
				break;
			case R.string.CHAT_MSGTYPE_CAMERA:
				//Uri output = Uri.fromFile(new File(FileUtil.getFileDir(this),"camra.jpg"));
				startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
					//.putExtra(MediaStore.EXTRA_OUTPUT,output)
					,R.string.CHAT_MSGTYPE_CAMERA);
				break;
			case R.string.CHAT_MSGTYPE_FILE:
//				Intent intent = FileUtil.getFileBrowserActivity(this);
//				if(intent!=null)
//					startActivityForResult(intent,R.string.CHAT_MSGTYPE_FILE);
				showToast(R.string.CIRCLE_NEW_TIP);
				
				break;
			default:
				break;
		}
	    
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode==RESULT_CANCELED)
    		return;
    	switch (requestCode) {
			case R.string.CHAT_MSGTYPE_PIC:
			case R.string.CHAT_MSGTYPE_CAMERA:{
				
				if(data==null)
					return;
				
				Uri uri = data.getData(); 
				File srcFile = FileUtil.queryFile(this, uri);
				String  fileDir = FileUtil.getFileDir(this);
				File targetFile = new File(fileDir,srcFile.getName());
				try {
					new BitmapUtil().decodeFile(srcFile,targetFile,800);
					inputView.sendMessage(targetFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;}
			case R.string.CHAT_MSGTYPE_FILE:{
				Uri uri = data.getData(); 
				String filePath = FileUtil.getRealPath(this, uri);
				File srcFile = new File(filePath);
				String fileDir = FileUtil.getFileDir(this);
				File targetFile = new File(fileDir,srcFile.getName());
				srcFile.renameTo(targetFile);
				inputView.sendMessage(targetFile);
				break;
			}
			default:
				break;
		}
    }
    @Override
    public void onBackPressed() {
    	if(inputView.getBottomVisibility()==View.VISIBLE){
    		inputView.hideBottomDiv();
    	}
    	super.onBackPressed();
    }
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent==null)
				return;
			Log.d(TAG, "onReceive:"+intent.getAction());
			if(Constants.ACTION_MESSAGE_RECEVER.equals(intent.getAction())){
				abortBroadcast();
				SharedUtils sharedUtils = SharedUtils.instance(context);
		        boolean needVibrator = sharedUtils.getInt(Constants.SHARED_SET_NOTIFY_VIBRATOR,0) ==1;
		        if(needVibrator)
		        	VibratorUtil.vibrate(context, 500);
			}else{
			}
			
			loadMessages();
		}
	};
	
}