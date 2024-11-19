package com.imbos.chat.member;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.imbos.chat.ChatActivity;
import com.imbos.chat.EditTextActivity;
import com.imbos.chat.MainActivity;
import com.imbos.chat.R;
import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.image.AsyncImageLoader.ImageCallback;
import com.imbos.chat.manage.LogicManager;
import com.imbos.chat.model.Member;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTableAnalysis;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.FileUtil;
import com.imbos.chat.util.StringUtil;
import com.imbos.chat.view.urlimagehelper.UrlImageViewHelper;

public class MemberDetailActivity extends SkinActivity {
	
	
	private static final int REQUEST_TEXT = 1;
	private String uid;
	private Member member;
	private ImageView imgFace;
	private TextView txtName;
	private TextView txtDoing;
	private TextView txtCode;
	private TextView txtArea;
	private TextView txtAlias;
	private TextView txtAge;
	private Button btnGreen;
	private ImageButton btnStar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_detail);
		
		uid = getIntent().getStringExtra(Intents.EXTRA_UID);
		
		if(getIntent().hasExtra(Intents.EXTRA_DATA)){
			String data = getIntent().getStringExtra(Intents.EXTRA_DATA);
			if(StringUtil.toString(data).contains(Constants.QRCODE_PREFIX_MB)){
				String[] arry = data.split(Constants.QRCODE_PREFIX_MB);
				if(arry.length>1){
					uid = arry[1];
				}else
					return;
			}else
			{
				return;
			}
		}
		
		imgFace =  (ImageView) findViewById(R.id.img_face);
		txtName = (TextView) findViewById(R.id.txt_name);
		txtDoing = (TextView) findViewById(R.id.txt_doing);
		txtCode = (TextView) findViewById(R.id.txt_code);
		txtArea = (TextView) findViewById(R.id.txt_addr);
		txtAge = (TextView) findViewById(R.id.txt_age);
		txtAlias = (TextView) findViewById(R.id.txt_alias);
		btnGreen = (Button) findViewById(R.id.btn_green);
		btnStar = (ImageButton) findViewById(R.id.btn_star);
		btnStar.setOnClickListener(this);
		txtAlias.setOnClickListener(this);
		btnGreen.setOnClickListener(this);
		
		setTitle(R.string.TITLE_MEMBER);
		setButtonIcon(btnNext, R.drawable.mm_title_btn_menu_normal);
		
		findViewById(R.id.div_album).setOnClickListener(this);
		
		bindMemberDetail(uid);
		syncMemberDetail();
		syncAlbum();
	}
	@Override
	protected void onStart() {
		super.onStart();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_btn_next:
			startActivityForResult(new Intent(this,MemberDialog.class)
					.putExtra(Intents.EXTRA_MEMBER,member),R.id.title_btn_next);
			break;
		case R.id.txt_alias:
			onMenuClick(R.id.menu_alias);
			break;
		case R.id.btn_green:
			if(member.isfriend){
				startActivity(new Intent(this, ChatActivity.class)
				.putExtra(Intents.EXTRA_MSG_TOS, member.uid)
				.putExtra(Intents.EXTRA_TITLE,StringUtil.isEmpty(member.alias)?member.name:member.alias));
			}else{
				startActivityForResult(new Intent(this,EditTextActivity.class)
				.putExtra(Intents.EXTRA_TITLE,R.string.TITLE_SAYHI)
				.putExtra(Intents.EXTRA_TEXT,""),R.id.btn_green);
			}
			break;
		case R.id.btn_star:
			if(member.star>0){
				delStar(uid);
			}else
				addStar(uid);
			break;
		case R.id.head_img3:
			startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT)
			.setType("image/*"),R.id.head_img3);
			break;
		case R.id.div_album:
			startActivity(new Intent(this, AlbumActivity.class)
			.putExtra(Intents.EXTRA_UID, member.uid)
			.putExtra(Intents.EXTRA_TITLE,StringUtil.isEmpty(member.alias)?member.name:member.alias));
			break;
		default:
			super.onClick(view);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED)
			return;

		switch (requestCode) {
			case R.id.title_btn_next:
				int menuId = data.getIntExtra(Intents.EXTRA_MENU_ID, 0);
				onMenuClick(menuId);
				break;
			case R.id.menu_alias:
				if (data.hasExtra(Intents.EXTRA_TEXT)) {
					String alias = data.getStringExtra(Intents.EXTRA_TEXT);
					member.alias = alias;
					setFriendAlias();
				} else {
					
				}
	
				break;
			case R.id.btn_green:
				if (data.hasExtra(Intents.EXTRA_TEXT)) {
					String note = data.getStringExtra(Intents.EXTRA_TEXT);
					addFriend(member.uid, note);
				}
				break;
				
			case R.id.head_img3:
				Uri uri = data.getData(); 
				uploadFile(FileUtil.queryFile(this, uri));
				
				break;
			default:
				break;
		}

	}
	
	public void onMenuClick(int menuId){
		switch (menuId) {
		case R.id.menu_alias:
			startActivityForResult(new Intent(this, EditTextActivity.class)
					.putExtra(Intents.EXTRA_TITLE, R.string.TITLE_ALIAS)
					.putExtra(Intents.EXTRA_TEXT, member.alias),
					R.id.menu_alias);
			break;
		case R.id.menu_star:
			if(member.star>0)
				delStar(uid);
			else
				addStar(uid);
			break;
		case R.id.menu_del:
			delFriend();
			break;
		case R.id.menu_black:
			blackFriend();
			break;
		
		default:
			break;
		}
	}
	
	public void bindMemberDetail(String uid){
		//[UID AS _id, NAME ,HEAD,STAR ,REAL ,DOING ,ALIAS ,SEX ,ENTERPRISE ,ISFRIEND ,
		// BIRTH ,GRADUATE ,INDUSTRY ,PROFESSION ,POST ,INTEREST ,AREA ,DISTANCE ,UNUM]
		SQLiteCursor cursor =  DbManager.queryMemberDetail(uid);
		member = new Member();
		if(cursor.moveToNext()){
			
			member.uid = StringUtil.toString(cursor.getString(0));
			member.name = StringUtil.toString(cursor.getString(1));
			member.head = StringUtil.toString(cursor.getString(2));
			member.star = cursor.getInt(3);
			member.real = cursor.getInt(4);
			member.doing = StringUtil.toString(cursor.getString(5));
			member.alias = StringUtil.toString(cursor.getString(6));
			member.sex = cursor.getInt(7);
			member.enterprise = StringUtil.toString(cursor.getString(8));
			member.isfriend = cursor.getInt(9)==1;
			member.birth = StringUtil.toString(cursor.getString(10));
			member.graduate = StringUtil.toString(cursor.getString(11));
			member.industry = StringUtil.toString(cursor.getString(12));
			member.profession = StringUtil.toString(cursor.getString(13));
			member.post = StringUtil.toString(cursor.getString(14));
			member.interest = StringUtil.toString(cursor.getString(15));
			member.area = StringUtil.toString(cursor.getString(16));
			member.distance = StringUtil.toString(cursor.getString(17));
			member.unum = StringUtil.toString(cursor.getString(18));
		}
		cursor.close();
		
		txtName.setText(member.name);
		txtDoing.setText(member.doing);
		txtAlias.setText(getString(R.string.MEMBER_ALIAS,member.alias));
		txtCode.setText(member.unum);
		txtArea.setText(member.area);
		btnStar.setImageResource(member.star>0?R.drawable.ic_star_pressed:R.drawable.ic_star_normal);
		if(!member.isfriend){//非好友无备注
			txtAlias.setVisibility(View.GONE);
			btnGreen.setText("加好友");
		}else{
			btnGreen.setText("开始聊天");
		}
		
		//性别
		int sexResId = Constants.SEX.BOY.equals(member.sex)?R.drawable.ico_boy:R.drawable.ico_girl;
		txtName.setCompoundDrawablesWithIntrinsicBounds(sexResId, 0, 0, 0);
		
		//生日
		String age = LogicManager.birth2Age(member.birth);
		txtAge.setText(age);
		
		UrlImageViewHelper.setUrlDrawable(imgFace,AsyncImageLoader.builderUrl(member.head));

	}
	
	public void syncMemberDetail() {

		HashMap<String, String> args = new HashMap<String, String>();
		args.put("uid", uid);

		SyncService
				.start(new SyncTask()
						.setSyncListener(new SimpleSyncListener(this,true) {
							@Override
							public void onSuccess() {
								super.onSuccess();
								bindMemberDetail(uid);
							}
						}).setMethodName(Constants.MENTHOD.MEMBER_DETAIL)
						.setArgs(args));

	}
	public void addFriend(String uid,String note){
		
		HashMap<String,String> args = new HashMap<String, String>();
		args.put("uid",uid);
		args.put("note", note);
		
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this,true){
					@Override
					public void onFailed() {
						super.onFailed();
						if(StringUtil.isEmpty(tips)){
							showToast(R.string.SYNC_MSG_FAILED);
						}else{
							showToast(this.tips);
						}
						
					}
					
					@Override
					public void onSuccess() {
						super.onSuccess();
						if(StringUtil.isEmpty(tips)){
							showToast(R.string.SYNC_MSG_SUCCESS);
						}else{
							showToast(this.tips);
						}
						finish();
					}
				}).setMethodName(Constants.MENTHOD.FRIEND_ADD)
			.setArgs(args));
	
	}
	public void addStar(String uid){
		
		HashMap<String,String> args = new HashMap<String, String>();
		args.put("uid",uid);
		
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this,false){
					@Override
					public void onFailed() {
						super.onFailed();
						if(StringUtil.isEmpty(tips)){
							showToast(R.string.MEMBER_MSG_STAR_FAILED);
						}else{
							showToast(this.tips);
						}
						
					}
					
					@Override
					public void onSuccess() {
						super.onSuccess();
						DbManager.setFriendStar(MemberDetailActivity.this.uid,1);
						bindMemberDetail(MemberDetailActivity.this.uid);
						if(StringUtil.isEmpty(tips)){
							showToast(R.string.MEMBER_MSG_STAR_FINISH);
						}else{
							showToast(this.tips);
						}
					}
				}).setMethodName(Constants.MENTHOD.FRIEND_ADD_STAR)
			.setArgs(args));
	
	}
	public void delStar(String uid){
		
		HashMap<String,String> args = new HashMap<String, String>();
		args.put("uid",uid);
		
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this,false){
					@Override
					public void onFailed() {
						super.onFailed();
						if(StringUtil.isEmpty(tips)){
							showToast(R.string.MEMBER_MSG_UNSTAR_FAILED);
						}else{
							showToast(this.tips);
						}
						
					}
					
					@Override
					public void onSuccess() {
						super.onSuccess();
						DbManager.setFriendStar(MemberDetailActivity.this.uid,0);
						bindMemberDetail(MemberDetailActivity.this.uid);
						if(StringUtil.isEmpty(tips)){
							showToast(R.string.MEMBER_MSG_UNSTAR_FINISH);
						}else{
							showToast(this.tips);
						}
					}
				}).setMethodName(Constants.MENTHOD.FRIEND_DEL_STAR)
			.setArgs(args));
	
	}
	/**
	 * 删除好友
	 * @param uid
	 */
	 public void delFriend(){
			
			HashMap<String,String> args = new HashMap<String, String>();
			args.put("uid",uid);
			
			SyncService.start(new SyncTask()
					.setSyncListener(new SimpleSyncListener(this,true){
						@Override
						public void onFailed() {
							super.onFailed();
							if(StringUtil.isEmpty(tips)){
								showToast(R.string.MEMBER_MSG_DEL_FAILED);
							}else{
								showToast(tips);
							}
						}
						
						@Override
						public void onSuccess() {
							super.onSuccess();
							LogicManager.delFriend(uid);
							if(StringUtil.isEmpty(tips)){
								showToast(R.string.MEMBER_MSG_DEL_FINISH);
							}else{
								showToast(tips);
							}
							startActivity(new Intent(MemberDetailActivity.this,MainActivity.class));
							finish();
						}
					}).setMethodName(Constants.MENTHOD.FRIEND_DEL)
				.setArgs(args));
		
	   }
	 /**
	 * 拉黑好友
	 * @param uid
	 */
	 public void blackFriend(){
			
			HashMap<String,String> args = new HashMap<String, String>();
			args.put("uid",uid);
			
			SyncService.start(new SyncTask()
					.setSyncListener(new SimpleSyncListener(this,true){
						@Override
						public void onFailed() {
							super.onFailed();
							if(StringUtil.isEmpty(tips)){
								showToast(R.string.MEMBER_MSG_BLACK_FAILED);
							}else{
								showToast(tips);
							}
						}
						
						@Override
						public void onSuccess() {
							super.onSuccess();
							LogicManager.delFriend(uid);
							if(StringUtil.isEmpty(tips)){
								showToast(R.string.MEMBER_MSG_BLACK_FINISH);
							}else{
								showToast(tips);
							}
							startActivity(new Intent(MemberDetailActivity.this,MainActivity.class));
							finish();
						}
					}).setMethodName(Constants.MENTHOD.FRIEND_BLACK)
				.setArgs(args));
		
	   }
	   /**
	    * 修改备注
	    * @param uid
	    * @param alias
	    */
	   public void setFriendAlias(){
		   HashMap<String,String> args = new HashMap<String, String>();
			args.put("uid",member.uid);
			args.put("alias",member.alias);
			
			SyncService.start(new SyncTask()
					.setSyncListener(new SimpleSyncListener(this,true){
						@Override
						public void onFailed() {
							super.onFailed();
							if(StringUtil.isEmpty(tips)){
								showToast(R.string.MEMBER_MSG_ALIAS_FAILED);
							}else{
								showToast(tips);
							}
						}
						
						@Override
						public void onSuccess() {
							super.onSuccess();
							DbManager.setFriendAlias(member.uid,member.alias);
							bindMemberDetail(uid);
							showToast(R.string.MEMBER_MSG_ALIAS_FINISH);
						}
					}).setMethodName(Constants.MENTHOD.FRIEND_ALIAS)
				.setArgs(args));
	   }
	   
	   
	   public void uploadFile(File file){
			
			HashMap<String,String> args = new HashMap<String, String>();
			args.put("idtype",Constants.PHOTO_TYPE.MEMBER);
			
			SyncService.start(new SyncTask()
			.setSyncListener(new SimpleSyncListener(this){
				@Override
				public void onFailed() {
					super.onFailed();
					if(StringUtil.isEmpty(tips)){
						showToast(R.string.GENERAL_DATA_SAVE_FAIL);
					}else{
						showToast(tips);
					}
					
				}
				
				@Override
				public void onSuccess() {
					super.onSuccess();
					showToast(R.string.GENERAL_DATA_SAVE_SUCCESS);
					
				}
			}).setArgs(args).setFile(file));
		}
	   
	   public void setPhoto(int imgId,String file){
		  final ImageView imageView = (ImageView) findViewById(imgId);
		  String miniFile = AsyncImageLoader.imageMiniFileName(file);
		  UrlImageViewHelper.setUrlDrawable(imageView,AsyncImageLoader.builderUrl(miniFile));
	   }
	   /**
	    * 照片
	    * @param uid
	    * @param alias
	    */
	   public void syncAlbum(){
		   HashMap<String,String> args = new HashMap<String, String>();
		   args.put("id",member.uid);
		   args.put("idtype",Constants.PHOTO_TYPE.MEMBER);
		   
			SyncService.start(new SyncTask()
					.setSyncListener(new SimpleSyncListener(this,true){
						@Override
						public void onFailed() {
							super.onFailed();
							if(StringUtil.isEmpty(tips)){
								showToast(R.string.SYNC_MSG_FAILED);
							}else{
								showToast(tips);
							}
						}
						
						@Override
						public void onSuccess() {
							super.onSuccess();
							
							try{
								SyncTableAnalysis analysis = new SyncTableAnalysis((Map<String, ?>) this.tag);
								List<String> row = (List<String>) analysis.tableRow(null,1);
								if(row!=null){
									setPhoto(R.id.head_img1,row.get(1));
								}
								row = (List<String>) analysis.tableRow(null, 2);
								if(row!=null){
									setPhoto(R.id.head_img2,row.get(1));
								}
								row = (List<String>) analysis.tableRow(null, 3);
								if(row!=null){
									setPhoto(R.id.head_img3,row.get(1));
								}
							}catch (Exception e) {
								e.printStackTrace();
							}
							
							
						}
					}).setMethodName(Constants.MENTHOD.COMMON_PHOTO)
					.setSyncHandler(null)
					.setArgs(args));
	   }
	   
}
