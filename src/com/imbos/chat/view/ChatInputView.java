package com.imbos.chat.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.app.ChatApp;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.manage.ApiManager;
import com.imbos.chat.model.EmojiFactory;
import com.imbos.chat.model.EmojiFactory.Emoji;
import com.imbos.chat.sync.MessageTask;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.util.FileUtil;
import com.imbos.chat.util.MediaRecordUtil;
import com.imbos.chat.util.StringUtil;
import com.imbos.client.ServiceManager;

public class ChatInputView extends LinearLayout implements OnClickListener{
	
	private static final String TAG = null;
	private EditText editInput;
	private Button btnSend;
	private Button btnType;
	private Button btnVoice; 
	private Button btnMic; 
	
	private FrameLayout divBottom;
	private GridView gvType;
	private GridView gvEmoji;
	private Map<String,Emoji> emojis;
	
	private String froms,tos;
	private MediaRecordUtil mediaRecorder;
	private EmojiGetter emojiGetter;
	
	private OnClickListener onTypeItemClickListener;
	
	
	public ChatInputView(Context context) {
		super(context);
		bindViews();
	}
	public ChatInputView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        bindViews();  
	}  

	public void bindViews(){
		mediaRecorder = new MediaRecordUtil(getContext());
		
		View view = LayoutInflater.from(this.getContext()).inflate(R.layout.chat_input_inner, null);
		this.addView(view,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		divBottom = (FrameLayout) findViewById(R.id.div_bottom);
		editInput = (EditText)findViewById(R.id.txt_input);
		
		btnVoice = (Button) findViewById(R.id.btn_voice);
		btnMic = (Button) findViewById(R.id.btn_mic);
		btnSend = (Button) findViewById(R.id.btn_send);
		btnType = (Button) findViewById(R.id.btn_type);
		
		gvType = (GridView) findViewById(R.id.gv_type);
		gvEmoji = (GridView) findViewById(R.id.gv_emoji);
		
		btnSend.setOnClickListener(this);
		btnType.setOnClickListener(this);
		btnVoice.setOnClickListener(this);
		btnMic.setOnTouchListener(touchListener);
		
		emojiGetter = new EmojiGetter(this.getContext(), editInput);
		
		emojis = (HashMap<String, Emoji>) ((ChatApp)getContext().getApplicationContext()).getEmojis();
		
		//Emoji[] array = (Emoji[]) emojis.values().toArray();
		ArrayList<Emoji> array = new ArrayList<Emoji>(emojis.values());
		
		ArrayAdapter<Emoji> emojiAdapter = new ArrayAdapter<EmojiFactory.Emoji>(getContext(),R.layout.emoji_item,R.id.txt_name,array){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView =  super.getView(position, convertView, parent);
				((ImageView)convertView.findViewById(R.id.img_icon)).setImageResource(getItem(position).resId);
				return convertView;
			}
		};
		
		List<int[]> msgTypes = new ArrayList<int[]>();
		msgTypes.add(new int[]{R.drawable.app_panel_expression_icon,R.string.CHAT_MSGTYPE_EMOJI});
		msgTypes.add(new int[]{R.drawable.app_panel_pic_icon,R.string.CHAT_MSGTYPE_PIC});
		msgTypes.add(new int[]{R.drawable.app_panel_video_icon,R.string.CHAT_MSGTYPE_CAMERA});
		//msgTypes.add(new int[]{R.drawable.app_panel_wxtalk_icon,R.string.CHAT_MSGTYPE_FILE});
		
		ArrayAdapter<int[]> msgTypeAdapter = new ArrayAdapter<int[]>(getContext(),R.layout.img_item,R.id.txt_name,msgTypes){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView =  super.getView(position, convertView, parent);
				
				int[] item = getItem(position);
				
				View imgbtn = convertView.findViewById(R.id.img_btn);
				imgbtn.setTag(item[1]);
				imgbtn.setOnClickListener(onTypeItemClickListener);
				
				((ImageView)convertView.findViewById(R.id.img_icon)).setImageResource(item[0]);
				((TextView)convertView.findViewById(R.id.txt_name)).setText(item[1]);
				return convertView;
			}
		};
		
		
		Emoji emoji_back = new Emoji();
		emoji_back.resId = R.drawable.emotion_del_normal;
		emojiAdapter.add(emoji_back);
		
		gvType.setAdapter(msgTypeAdapter);
		
		
		gvEmoji.setAdapter(emojiAdapter);
		gvEmoji.setOnItemClickListener(emojiItemClickListener);
		
		editInput.setOnClickListener(this);
	}
	private OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus){
				hideBottomDiv();
			}
		}
	};
	private OnItemClickListener emojiItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View convertView, int position,
				long id) {
			if(position==adapterView.getAdapter().getCount()-1){
				hideEmoji();
			}else{
				Emoji emoji =  (Emoji) adapterView.getAdapter().getItem(position);
				appendText(emoji.code);
			}
		}
	};
	
	private OnTouchListener touchListener = new OnTouchListener() {
		long startTime;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				
				btnMic.setText(R.string.CHAT_VIOCE_TAUCH);
				String fileName =FileUtil.getFileDir(getContext()) + "/"+System.currentTimeMillis()+Constants.SUFFIX_SOUND;
				startTime=System.currentTimeMillis();
				mediaRecorder.start(fileName);
				
			}else if(event.getAction()==MotionEvent.ACTION_UP){
				String fileName = mediaRecorder.stop();
				btnMic.setText(R.string.CHAT_VIOCE_TIP);
				long time = System.currentTimeMillis()-startTime;
				
				if(time<1000){
					
				}else{
					File file = new File(fileName);
					File newFile = new File(file.getParent(),time+"-"+startTime+fileName.substring(fileName.lastIndexOf(".")));
					file.renameTo(newFile);
					sendMessage(newFile);
				}
			}return false;
		}
	};
	
	@Override
	public void onClick(View v) {
		if(v.equals(btnVoice)){
			if(btnVoice.getTag()==null){
				showMic();
				btnVoice.setTag("");
			}else{
				showEdit();
				btnVoice.setTag(null);
			}
		}else if(v.equals(btnSend)){
			if(null==btnVoice.getTag()){
				if(!StringUtil.isEmpty(getText())){
					sendMessage(getText().toString());
					setText("");
				}
			}
		}else if(v.equals(btnType)){
			if(divBottom.getVisibility()==View.GONE){
				hideKeyboard();
				hideEmoji();
				divBottom.setVisibility(View.VISIBLE);
			}else{
				hideBottomDiv();
			}
		}else if(v.equals(editInput)){
			if(divBottom.getVisibility()==View.VISIBLE){
				hideBottomDiv();
			}
		}
	}
	
	 private void showMic(){
    	btnMic.setVisibility(View.VISIBLE);
    	editInput.setVisibility(View.GONE);
    	btnSend.setVisibility(View.GONE);
    	btnVoice.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn);
	}
    private void showEdit(){
    	btnMic.setVisibility(View.GONE);
    	editInput.setVisibility(View.VISIBLE);
    	btnSend.setVisibility(View.VISIBLE);
    	btnVoice.setBackgroundResource(R.drawable.chatting_setmode_voice_btn);
    }
    
    public void hideBottomDiv(){
    	divBottom.setVisibility(View.GONE);
//    	int len = divBottom.getChildCount();
//    	for (int i = 0; i < len; i++) {
//    		divBottom.getChildAt(i).setVisibility(View.GONE);
//		}
    }
    
    public int getBottomVisibility(){
    	return divBottom.getVisibility();
    }
    
    public void hideKeyboard(){
        //隐藏软键盘
	  InputMethodManager imm = ( InputMethodManager )this.getContext().getSystemService( Context.INPUT_METHOD_SERVICE );
	  if ( imm.isActive( ) ) {
	      imm.hideSoftInputFromWindow(this.getApplicationWindowToken(), 0 );
	  }
	}
    
    public void showEmoji(){
    	gvEmoji.setVisibility(View.VISIBLE);
    	gvEmoji.bringToFront();
		
    }
    public void hideEmoji(){
    	gvEmoji.setVisibility(View.GONE);
    }
    
    public void sendMessage(String content){
    	String id = UUID.randomUUID().toString();
    	String date = DateUtil.formatDate2Str();
    	//ServiceManager.sendMessage(this.getContext(), id, froms, tos, content, date);
    
    	Map<String,String> args = new HashMap<String, String>();
    	
    	args.put("id",id);
    	args.put("from",froms);
    	args.put("to",tos);
    	args.put("content",content);
    	args.put("date",date);
    	
    	DbManager.saveMessage(id, froms, tos, content, date,MessageTask.STATUS_FINISH);
    	DbManager.saveSession(tos, content,date);
    	
    	SyncService.start(new MessageTask(id)
    		.setMethodName(Constants.MENTHOD.MSG_SEND)
    		.setArgs(args));
    	
    }
    
   
    
    
    public void sendMessage(File file){
    	String id = UUID.randomUUID().toString();
    	String date =DateUtil.formatDate2Str();
    	
    	Map<String,String> args = new HashMap<String, String>();
    	
    	args.put("id",id);
    	args.put("from",froms);
    	args.put("to",tos);
    	args.put("content",null);
    	args.put("date",date);
    	
    	DbManager.saveMessage(id, froms, tos, "file:"+file.getPath(), date, 0);
    	
    	
    	SyncService.start(new MessageTask(id)
    		.setMethodName(Constants.MENTHOD.MSG_UPLOAD)
    		.setFile(file)
    		.setArgs(args));
    	
    	//ApiManager.sendMessage(id, froms, tos, file);
    	//ServiceManager.sendMessage(this.getContext(), id, froms, tos, Constants.PREFIX_FILE+file.getPath(), date);
    }
    
    public void setTos(String tos) {
		this.tos = tos;
	}
    public void setFroms(String froms) {
		this.froms = froms;
	}
    
    public void setText(String source){
    	editInput.setText(Html.fromHtml(source, emojiGetter, null));
    }
    public void appendText(String source){
    	editInput.append(Html.fromHtml(source, emojiGetter, null));
    }
	public Editable getText(){
		return editInput.getText();
	}
	
	public void setOnTypeItemClickListener(
			OnClickListener onTypeItemClickListener) {
		this.onTypeItemClickListener = onTypeItemClickListener;
	}
}
