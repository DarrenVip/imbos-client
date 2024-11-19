package com.imbos.chat.member;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imbos.chat.BuildConfig;
import com.imbos.chat.R;
import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.image.ImageActivity;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTableAnalysis;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.AndroidUtils;
import com.imbos.chat.util.BitmapUtil;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.FileUtil;
import com.imbos.chat.util.StringUtil;
import com.imbos.chat.view.urlimagehelper.UrlImageViewCallback;
import com.imbos.chat.view.urlimagehelper.UrlImageViewHelper;

public class AlbumActivity extends SkinActivity {
	
	
	private String uid;
	private List<String> links = new ArrayList<String>();
	private GridView gridView;
	private ArrayAdapter<String> arrayAdapter;
	private String IDType;
	
	private int columnWidth;
	private int numColumns;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album);
		
		uid = getIntent().getStringExtra(Intents.EXTRA_UID);
		IDType = getIntent().getStringExtra("idtype");
		if(StringUtil.isEmpty(IDType)) {
			IDType = Constants.PHOTO_TYPE.MEMBER;
		}
		String title = getIntent().getStringExtra(Intents.EXTRA_TITLE);
		setTitle(StringUtil.isEmpty(title)?getString(R.string.USER_CARD_ALBUM):title);
		
		if(ChatApp.getConfig().saasUid.equals(uid))
			setButtonIcon(btnNext, R.drawable.mm_title_btn_menu_normal);
		else
			btnNext.setVisibility(View.INVISIBLE);
			
		
		gridView = (GridView) findViewById(R.id.gv_photo);
		//gridView.setColumnWidth(this.getWidth()/4);
		
		bindGridView();
		final int mImageThumbSpacing = AndroidUtils.dip2px(this,5);
		gridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (numColumns == 0) {
					numColumns = 4;//(int) Math.floor(gridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
					if (numColumns > 0) {
						if(columnWidth==0)
							columnWidth = (gridView.getWidth() / numColumns) - mImageThumbSpacing;
//						mAdapter.setNumColumns(numColumns);
//						mAdapter.setItemHeight(columnWidth);
						if (BuildConfig.DEBUG) {
							Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
						}
					}
				}
			}
		});
		
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
			startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT)
				.setType("image/*"),R.id.title_btn_next);
			break;
		case R.id.txt_alias:
			
			break;
		case R.id.btn_green:
			
			break;
		case R.id.head_img3:
			startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT)
			.setType("image/*"),R.id.head_img3);
			break;
		default:
			super.onClick(view);
		}
	}
	
	public void bindGridView(){
		arrayAdapter = new ArrayAdapter<String>(this, R.layout.photo_item, R.id.txt_name, links){
			public View getView(int position, View convertView, android.view.ViewGroup parent) {
				convertView = super.getView(position, convertView, parent);
				final ImageView imageView = (ImageView) convertView.findViewById(R.id.img_icon);
				final TextView textView = (TextView) convertView.findViewById(R.id.txt_name);
				textView.setText("");
				String miniFile = AsyncImageLoader.imageMiniFileName(getItem(position));
				
				imageView.setMinimumHeight(columnWidth);
				imageView.setMaxHeight(columnWidth);
				imageView.setMinimumWidth(columnWidth);
				imageView.setMaxWidth(columnWidth);
				imageView.setLayoutParams(new FrameLayout.LayoutParams(columnWidth,columnWidth));
				//convertView.setMinimumHeight(columnWidth);
				
				UrlImageViewHelper.setUrlDrawable(imageView,AsyncImageLoader.builderUrl(miniFile));// );
				convertView.setTag(getItem(position));
				return convertView;
			}
		};
		gridView.setAdapter(arrayAdapter);
		gridView.setOnItemClickListener(itemClickListener);
	}
	
	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View convertView, int arg2,
				long arg3) {
			String imageUrl = 	AsyncImageLoader.builderUrl(StringUtil.toString(convertView.getTag()));
			startActivity(new Intent(AlbumActivity.this,ImageActivity.class)
					.putExtra(Intents.EXTRA_DATA,imageUrl));
		}
	};
	
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
					syncAlbum();
					showToast(R.string.GENERAL_DATA_SAVE_SUCCESS);
					
				}
			}).setArgs(args).setFile(file));
		}
	   
	   public void setPhoto(int imgId,String file){
		   	links.add(file);
		}
	   @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (resultCode == RESULT_CANCELED)
				return;

			switch (requestCode) {
				case R.id.title_btn_next:
					Uri uri = data.getData(); 
					File srcFile = FileUtil.queryFile(this, uri);
					String  fileDir = FileUtil.getFileDir(this);
					File targetFile = new File(fileDir,srcFile.getName());
					try {
						new BitmapUtil().decodeFile(srcFile,targetFile,1000);
						uploadFile(targetFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
			}

		}
	   /**
	    * 照片
	    * @param uid
	    * @param alias
	    */
	   public void syncAlbum(){
		   HashMap<String,String> args = new HashMap<String, String>();
		   args.put("id",uid);
		   args.put("idtype",IDType);
		   
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
						
						@SuppressWarnings("unchecked")
						@Override
						public void onSuccess() {
							super.onSuccess();
							
							try{
								SyncTableAnalysis analysis = new SyncTableAnalysis((Map<String, ?>) this.tag);
								List<List<?>> table = analysis.table(null);
								int len =table==null?0:table.size();
								for (int i = 1; i < len; i++) {
									List<String> row = (List<String>) table.get(i);
									if(!links.contains(row.get(1)) && !StringUtil.isEmpty(row.get(1)))
										links.add(row.get(1));
								}
								arrayAdapter.notifyDataSetChanged();
							}catch (Exception e) {
								e.printStackTrace();
							}
							
							
						}
					}).setMethodName(Constants.MENTHOD.COMMON_PHOTO)
					.setSyncHandler(null)
					.setArgs(args));
	   }
	   
}
