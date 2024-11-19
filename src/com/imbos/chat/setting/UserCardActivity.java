package com.imbos.chat.setting;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.imbos.chat.EditTextActivity;
import com.imbos.chat.QRCardActivity;
import com.imbos.chat.R;
import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.image.AsyncImageLoader.ImageCallback;
import com.imbos.chat.image.ImageActivity;
import com.imbos.chat.member.AlbumActivity;
import com.imbos.chat.member.DicActivity;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.BitmapUtil;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.util.FileUtil;
import com.imbos.chat.util.StringUtil;
import com.imbos.chat.view.urlimagehelper.UrlImageViewHelper;
/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class UserCardActivity extends SkinActivity{

	private static final int REQUEST_CROP_ICON = 123;
	private ImageView imgFace;
	private String birthday;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_card);
		
		btnNext.setVisibility(View.INVISIBLE);
		
		findViewById(R.id.item_face).setOnClickListener(this);
		findViewById(R.id.item_nickname).setOnClickListener(this);
		findViewById(R.id.item_name).setOnClickListener(this);
		findViewById(R.id.item_sex).setOnClickListener(this);
		findViewById(R.id.item_birthday).setOnClickListener(this);
		findViewById(R.id.item_area).setOnClickListener(this);
		findViewById(R.id.item_addr).setOnClickListener(this);
		findViewById(R.id.item_doing).setOnClickListener(this);
		findViewById(R.id.item_score).setOnClickListener(this);
		findViewById(R.id.item_cmp).setOnClickListener(this);
		findViewById(R.id.item_school).setOnClickListener(this);
		findViewById(R.id.item_alrum).setOnClickListener(this);
		findViewById(R.id.item_qrcode).setOnClickListener(this);
		
		imgFace = (ImageView) findViewById(R.id.img_face);
		imgFace.setOnClickListener(this);
		bindFields();
		syncSetting();
	}
	
	private void bindFields(){
		//[id,USERNAME,NAME,HEAD,BIRTH,AREA,C_DOING,CREDIT,C_ENTERPRISE,C_INDUSTRY , C_GRADUATE,C_PROFESSION,C_INTEREST,C_PROFESSION,C_POST,C_INTEREST,C_TYPE]
		Map<String,?> map = DbManager.queryUserCard(ChatApp.getConfig().saasUid);
		
		findViewById(R.id.item_nickname).setTag("USERNAME");
		setFieldValue(R.id.item_nickname,map.get("USERNAME"));
		
		findViewById(R.id.item_name).setTag("NAME");
		setFieldValue(R.id.item_name,map.get("NAME"));
		
		findViewById(R.id.item_sex).setTag("SEX");
		setFieldValue(R.id.item_sex,Constants.SEX.BOY.equals(map.get("SEX"))?
				getString(R.string.GENERAL_SEX_BOY)
				:getString(R.string.GENERAL_SEX_GIRL));
		
		birthday = StringUtil.toString(map.get("BIRTH"));
		findViewById(R.id.item_birthday).setTag("BIRTH");
		setFieldValue(R.id.item_birthday,map.get("BIRTH"));
		
		findViewById(R.id.item_area).setTag("AREA");
		setFieldValue(R.id.item_area,map.get("AREA"));
		
		findViewById(R.id.item_addr).setTag("USERNAME");
		setFieldValue(R.id.item_addr,map.get("USERNAME"));
		
		findViewById(R.id.item_doing).setTag("C_DOING");
		setFieldValue(R.id.item_doing,map.get("C_DOING"));
		
		findViewById(R.id.item_score).setTag("CREDIT");
		setFieldValue(R.id.item_score,map.get("CREDIT"));
		
		findViewById(R.id.item_cmp).setTag("C_INTEREST");
		setFieldValue(R.id.item_cmp,map.get("C_INTEREST"));
		
		findViewById(R.id.item_school).setTag("C_GRADUATE");
		setFieldValue(R.id.item_school,map.get("C_GRADUATE"));
		
		//setFieldValue(R.id.item_alrum,map.get("C_GRADUATE"));
		//setFieldValue(R.id.item_qrcode,map.get("C_GRADUATE"));
		String user_head = StringUtil.toString(map.get("HEAD"));
		String head_src = AsyncImageLoader.builderUrl(user_head);
		imgFace.setTag(head_src);
		
		UrlImageViewHelper.setUrlDrawable(imgFace, head_src);
	}
	
	private void setFieldValue(int id,Object value){
		((TextView)findViewById(id)
				.findViewById(R.id.txt_value))
				.setText(StringUtil.toString(value));
	}
	private String getFieldValue(int id){
		return ((TextView)findViewById(id)
				.findViewById(R.id.txt_value))
				.getText().toString();
	}
	private String getFieldText(int id){
		return ((TextView)findViewById(id)
				.findViewById(R.id.txt_name))
				.getText().toString();
	}
	
	public void syncSetting(){
		
		
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this,true){
					@Override
					public void onFailed() {
						super.onFailed();
					}
					
					@Override
					public void onSuccess() {
						super.onSuccess();
						bindFields();
					}
				}).setMethodName(Constants.MENTHOD.USER_SET)
				.setArgs(null));
	}
	
	public void syncFiledValue(String filedName,String filedValue){
		
		HashMap<String,String> args = new HashMap<String, String>();
		args.put("fieldname", filedName);
		args.put("value", filedValue);
		
		SyncService.start(new SyncTask()
		.setSyncListener(new SimpleSyncListener(this){
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			@Override
			public void onSuccess() {
				super.onSuccess();
				showToast(R.string.GENERAL_DATA_SAVE_SUCCESS);
				syncSetting();
			}
		}).setMethodName(Constants.MENTHOD.USER_EDIT_FILED)
		.setArgs(args));
	}
	
	public void uploadFile(File file){
		
		HashMap<String,String> args = new HashMap<String, String>();
		args.put("idtype",Constants.PHOTO_TYPE.HEAD);
		
		SyncService.start(new SyncTask()
		.setSyncListener(new SimpleSyncListener(this){
			@Override
			public void onFailed() {
				super.onFailed();
				showToast(R.string.GENERAL_DATA_SAVE_FAIL);
			}
			
			@Override
			public void onSuccess() {
				super.onSuccess();
				syncSetting();
				showToast(R.string.GENERAL_DATA_SAVE_SUCCESS);
				
			}
		}).setArgs(args).setFile(file));
	}
	
	private void showDatePicker(){
		final String format = "yyyy-M-d";
		Calendar calendar = Calendar.getInstance();
		Date date = DateUtil.formatStr2Date(birthday,format);
		if(date!=null)
			calendar.setTime(date);
		new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener(){
			@Override
			public void onDateSet(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, monthOfYear);
				c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
				
				syncFiledValue("BIRTH",DateUtil.formatDate2Str(c.getTime(),format));
			}
		},calendar.get(Calendar.YEAR)
		,calendar.get(Calendar.MONTH)
		,calendar.get(Calendar.DAY_OF_MONTH))
		.show();
	}
	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.item_name:
			case R.id.item_cmp:
			case R.id.item_school:
				startActivityForResult(new Intent(this,EditTextActivity.class)
					.putExtra(Intents.EXTRA_TITLE,getFieldText(id))
					.putExtra(Intents.EXTRA_TEXT,getFieldValue(id)),id);
				break;
			case R.id.item_doing:
				startActivityForResult(new Intent(this,EditTextActivity.class)
				.putExtra(Intents.EXTRA_TITLE,getFieldText(id))
				.putExtra(Intents.EXTRA_TEXT,getFieldValue(id))
				.putExtra(Intents.EXTRA_SINGE,false),id);
			case R.id.item_birthday:
//				startActivityForResult(new Intent(this,DatePickerActivity.class)
//					.putExtra(Intents.EXTRA_TITLE,getFieldText(id))
//					.putExtra(Intents.EXTRA_TEXT,getFieldValue(id)),id);
				showDatePicker();
				break;
			case R.id.img_face:
				if(view.getTag()==null)
					return;
				String img_src = view.getTag().toString();
				startActivity(new Intent(this, ImageActivity.class)
					.putExtra(Intents.EXTRA_DATA,img_src));
				
				break;
			case R.id.item_area:
			case R.id.item_sex:
				DicActivity.srcActivity = this.getClass();
				startActivityForResult(new Intent(this,DicActivity.class)
					.putExtra(Intents.EXTRA_MENU_ID,id)
					.putExtra(Intents.EXTRA_TITLE,getFieldText(id)),id);
				break;
			case R.id.item_qrcode:
				startActivity(new Intent(this, QRCardActivity.class)
				.putExtra(Intents.EXTRA_ID,ChatApp.getConfig().saasUid));
				break;
			case R.id.item_face:
				startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT)
					.setType("image/*"),id);
	             break;
			case R.id.item_alrum:
				startActivity(new Intent(this, AlbumActivity.class)
				.putExtra(Intents.EXTRA_UID,ChatApp.getConfig().saasUid));
			default:
				super.onClick(view);
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		int itemId = intent.getIntExtra(Intents.EXTRA_MENU_ID, 0);
		String text = intent.getStringExtra(Intents.EXTRA_TEXT);
		String id = intent.getStringExtra(Intents.EXTRA_ID);
		
		switch (itemId) {
		case R.id.item_sex:
			text = id;
		case R.id.item_area:
			String filedValue = text;	
			String filedName = findViewById(itemId).getTag().toString();	
			syncFiledValue(filedName, filedValue);
			break;
		default:
		}// TODO Auto-generated method stub
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode!=RESULT_OK)
			return;
		switch (requestCode) {
			case R.id.item_name:
			case R.id.item_birthday:
			case R.id.item_doing:
			case R.id.item_cmp:
			case R.id.item_school:
				String filedValue = data.getStringExtra(Intents.EXTRA_TEXT);	
				String filedName = findViewById(requestCode).getTag().toString();	
				syncFiledValue(filedName, filedValue);
			break;
			case R.id.item_face:
				try {
					Uri uri = data.getData(); 
					//cropImage(uri);
					File srcFile = FileUtil.queryFile(this, uri);
					String  fileDir = FileUtil.getFileDir(this);
					File targetFile = new File(fileDir,srcFile.getName());
					new BitmapUtil().decodeFile(srcFile,targetFile,800);
					uploadFile(targetFile);
				} catch (Exception e) {
					e.printStackTrace();
					showToast("读取文件失败！");
				}
			break;
			case REQUEST_CROP_ICON:
				Bundle extras = data.getExtras();
				if(extras !=null){
					Bitmap photo = extras.getParcelable("data");
					
				}
			   
				//uploadFile(FileUtil.queryFile(this, uri));	
			break;
		}
	}
	
	public void cropImage(Uri uri){
		
		Intent intent =new Intent("com.android.camera.action.CROP");
		intent.setClassName("com.android.camera","com.android.camera.CropImage");
		intent.setData(uri);
		intent.putExtra("crop","true");
		intent.putExtra("aspectX",1);
		intent.putExtra("aspectY",1);
		intent.putExtra("outputX",96);
		intent.putExtra("outputY",96);
		intent.putExtra("noFaceDetection",true);
		intent.putExtra("return-data",true);
		startActivityForResult(intent, REQUEST_CROP_ICON);
	}
}
