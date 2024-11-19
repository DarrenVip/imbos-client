package com.imbos.chat.member;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.imbos.chat.R;
import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.sync.DataSyncListener;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SimpleSyncTask;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTableAnalysis;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.StringUtil;
import com.imbos.chat.view.EmojiGetter;
import com.imbos.chat.view.urlimagehelper.UrlImageViewHelper;


/**
 * 
 * @author
 */
public class EPMemberDetailAboutUsView implements OnClickListener {
	private View headerView;
	private View mview;
	private Context mcontext;
	private ListView list_comments;
	
	private ImageView logo;
	private TextView shortname;
	private TextView appid;
	private TextView name;
	private ImageView vip;
	private ImageView real;
	private TextView area;
	private TextView txt_legal_person;
	private TextView txt_scale;
	private TextView txt_range;
	private TextView txt_industry;
	private TextView txt_dynamic;
	private TextView txt_address;
	private TextView txt_zip;
	private TextView txt_summary;
	private Button btn_comments;
	private TextView txt_praise;
	private TextView txt_attention;
//	private LinearLayout layout_comments;
	private LinearLayout layout_div_album;
	
	private boolean isloaded = false;
	private String eid;
	private HashMap<String, Object> searchCond = new HashMap<String, Object>();
	private DataSyncListener syncListener;
	
	private DataSyncListener commentListListener;
	private DataSyncListener addCommentListener;
	private DataSyncListener praiseListener;
	private DataSyncListener attentionListener;
	
	private ArrayList<ArrayList<String>> datalist;
	private CommentsAdapter commentsAdapter;
	
	public EPMemberDetailAboutUsView(Context mcontext, String eid) {
		this.mcontext = mcontext;
		this.eid = eid;
		initView();
	}
	
	private void initView() {
		mview = LayoutInflater.from(mcontext).inflate(R.layout.normal_comments_list, null);
		list_comments = (ListView) mview.findViewById(R.id.list_comments);
		datalist = new ArrayList<ArrayList<String>>();
		list_comments.setDivider(new ColorDrawable(0x00ffffff));
		list_comments.setDividerHeight(1);
		
		headerView = LayoutInflater.from(mcontext).inflate(R.layout.emember_aboutus, null);
		initChildView(headerView);
		initSyncListener();
	}
	
	private void initSyncListener() {
		syncListener = new DataSyncListener(this.mcontext) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			public void onBefore() {
				super.onBefore();
			}

			@Override
			public void onSuccess(HashMap<String, ?> response) {
				super.onSuccess(response);
				isloaded = true;
				setChildViewValue(response);
				list_comments.addHeaderView(headerView);
				getCommentsList();
			}
		};
		
		commentListListener = new DataSyncListener(this.mcontext) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			public void onBefore() {
				super.onBefore();
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(HashMap<String, ?> response) {
				super.onSuccess(response);
				datalist.clear();
				Object obj = ((ArrayList<?>) response.get("ds")).get(0);
				datalist.addAll((ArrayList<ArrayList<String>>) obj);
				if(datalist.size() > 0) {
					datalist.remove(0);
				}
				commentsAdapter = new CommentsAdapter(datalist, mcontext);
				list_comments.setAdapter(commentsAdapter);
			}
		};
		
		addCommentListener = new DataSyncListener(this.mcontext) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			public void onBefore() {
				super.onBefore();
			}

			@Override
			public void onSuccess(HashMap<String, ?> response) {
				super.onSuccess(response);
				getCommentsList();
			}
		};
		
		praiseListener = new DataSyncListener(this.mcontext) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			public void onBefore() {
				super.onBefore();
			}

			@Override
			public void onSuccess(HashMap<String, ?> response) {
				super.onSuccess(response);
				int now = Integer.parseInt(txt_praise.getText().toString());
				txt_praise.setText((now + 1) + "");
			}
		};
		
		attentionListener = new DataSyncListener(this.mcontext) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			public void onBefore() {
				super.onBefore();
			}

			@Override
			public void onSuccess(HashMap<String, ?> response) {
				super.onSuccess(response);
				int now = Integer.parseInt(txt_attention.getText().toString());
				txt_attention.setText((now + 1) + "");
			}
		};
	}
	
	private void initChildView(View mview) {
		logo = (ImageView) mview.findViewById(R.id.logo);
		shortname = (TextView) mview.findViewById(R.id.shortname);
		appid = (TextView) mview.findViewById(R.id.appid);
		name = (TextView) mview.findViewById(R.id.name);
		vip = (ImageView) mview.findViewById(R.id.vip);
		real = (ImageView) mview.findViewById(R.id.real);
		area = (TextView) mview.findViewById(R.id.area);
		txt_legal_person = (TextView) mview.findViewById(R.id.txt_legal_person);
		txt_scale = (TextView) mview.findViewById(R.id.txt_scale);
		txt_range = (TextView) mview.findViewById(R.id.txt_range);
		txt_industry = (TextView) mview.findViewById(R.id.txt_industry);
		txt_dynamic = (TextView) mview.findViewById(R.id.txt_dynamic);
		txt_address = (TextView) mview.findViewById(R.id.txt_address);
		txt_zip = (TextView) mview.findViewById(R.id.txt_zip);
		txt_summary = (TextView) mview.findViewById(R.id.txt_summary);
		btn_comments = (Button) mview.findViewById(R.id.btn_comments);
		txt_praise = (TextView) mview.findViewById(R.id.txt_praise);
		txt_attention = (TextView) mview.findViewById(R.id.txt_attention);
//		layout_comments = (LinearLayout) mview.findViewById(R.id.layout_comments);
//		if(eid.equalsIgnoreCase(ChatApp.getConfig().saasUid)) {
//			layout_comments.setVisibility(View.GONE);
//		}
		layout_div_album = (LinearLayout) mview.findViewById(R.id.div_album);
		
		layout_div_album.setOnClickListener(this);
		btn_comments.setOnClickListener(this);
		txt_praise.setOnClickListener(this);
		txt_attention.setOnClickListener(this);
	}
	
	@SuppressWarnings("unchecked")
	private void setChildViewValue(HashMap<String, ?> response) {
		List<?> ds = (List<?>) response.get("ds");
		List<?> list = (List<?>) ds.get(0);
		ArrayList<String> data = (ArrayList<String>) list.get(1);
		
		UrlImageViewHelper.setUrlDrawable(logo, AsyncImageLoader.builderUrl(data.get(19)));
		shortname.setText(data.get(1));
		name.setText(data.get(2));
		appid.setText("ID:" + (data.get(3) == null ? "" : data.get(3)));
		vip.setBackgroundResource(R.drawable.ico_vip);
		real.setBackgroundResource(R.drawable.ico_cp_verify);
		area.setText(data.get(6));
		String source = mcontext.getString(R.string.EM_DETAIL_DYNAMIC) + ":" + (data.get(7) == null ? "" : data.get(7));
		txt_dynamic.setText(Html.fromHtml(source, new EmojiGetter(mcontext, txt_dynamic), null));
		txt_scale.setText(mcontext.getString(R.string.EM_DETAIL_SCALE) + ":" + (data.get(8) == null ? "" : data.get(8)));
		txt_industry.setText(mcontext.getString(R.string.EM_DETAIL_INDUSTRY) + ":" 
				+ DbManager.getDicName((data.get(9) == null ? "" : data.get(9))));
		txt_summary.setText(mcontext.getString(R.string.EM_DETAIL_SUMMARY) + ":" + (data.get(10) == null ? "" : data.get(10)));
		btn_comments.setText((data.get(11) == null ? "" : data.get(11)));
		txt_praise.setText((data.get(12) == null ? "" : data.get(12)));
		txt_attention.setText((data.get(13) == null ? "" : data.get(13)));
		txt_range.setText(mcontext.getString(R.string.EM_DETAIL_SERVICEFIELD) + ":" 
				+ DbManager.getDicName((data.get(14) == null ? "" : data.get(14))));
		txt_legal_person.setText(mcontext.getString(R.string.EP_LEGAL_PERSON) + ":" + (data.get(16) == null ? "" : data.get(16)));
		txt_address.setText(mcontext.getString(R.string.EP_ADDRESS) + ":" + (data.get(17) == null ? "" : data.get(17)));
		txt_zip.setText(mcontext.getString(R.string.EP_ZIP) + ":" + (data.get(18) == null ? "" : data.get(18)));
	}
	
	public void queryEMemberDetailAboutUs() {
		searchCond.put("eid", eid);
		SimpleSyncTask task = new SimpleSyncTask().setSyncListener(syncListener)
		.setMethodName(Constants.MENTHOD.EMEMBER_ABOUTUS).setArgs(searchCond);
		new Thread(task).start();
	}
	
	public void initData() {
		if(isloaded == false) {
			queryEMemberDetailAboutUs();
			syncAlbum();
		}
	}

	public View getMview() {
		return mview;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_comments:
			if(eid.equalsIgnoreCase(ChatApp.getConfig().saasUid)) {
				Toast.makeText(mcontext, "自己不能评论自己的企业！", Toast.LENGTH_SHORT).show();
			} else {
				toComments();
			}
			break;
		case R.id.txt_praise:
			if(eid.equalsIgnoreCase(ChatApp.getConfig().saasUid)) {
				Toast.makeText(mcontext, "自己不能赞自己的企业！", Toast.LENGTH_SHORT).show();
			} else {
				addPraise();
			}
			break;
		case R.id.txt_attention:
			if(eid.equalsIgnoreCase(ChatApp.getConfig().saasUid)) {
				Toast.makeText(mcontext, "自己不能关注自己的企业！", Toast.LENGTH_SHORT).show();
			} else {				
				addAttention();
			}
			break;
		case R.id.div_album:
			mcontext.startActivity(new Intent(mcontext, AlbumActivity.class).putExtra(Intents.EXTRA_UID, eid).putExtra(
					Intents.EXTRA_TITLE, name.getText().toString()).putExtra("idtype", Constants.PHOTO_TYPE.ENTERPRISE));
			break;
		default:
			break;
		}
	}
	
	public void addPraise() {
		HashMap<String, Object> searchArgs = new HashMap<String, Object>();
		searchArgs.put("id", eid);
		searchArgs.put("idtype", "eid");
		SimpleSyncTask task = new SimpleSyncTask().setSyncListener(praiseListener)
		.setMethodName(Constants.MENTHOD.EMEMBER_ADDPRAISE).setArgs(searchArgs);
		new Thread(task).start();
	}
	
	public void addAttention() {
		HashMap<String, Object> searchArgs = new HashMap<String, Object>();
		searchArgs.put("eid", eid);
		SimpleSyncTask task = new SimpleSyncTask().setSyncListener(attentionListener)
		.setMethodName(Constants.MENTHOD.EMEMBER_ADDATTENTION).setArgs(searchArgs);
		new Thread(task).start();
	}
	
	public void getCommentsList() {
		HashMap<String, Object> searchArgs = new HashMap<String, Object>();
		searchArgs.put("id", eid);
		searchArgs.put("idtype", "eid");
		SimpleSyncTask task = new SimpleSyncTask().setSyncListener(commentListListener)
		.setMethodName(Constants.MENTHOD.EMEMBER_FINDCOMMENT).setArgs(searchArgs);
		new Thread(task).start();
	}
	
	public void addComments(String message) {
		HashMap<String, Object> searchArgs = new HashMap<String, Object>();
		searchArgs.put("uid", ChatApp.getConfig().saasUid);
		searchArgs.put("id", eid);
		searchArgs.put("idtype", "eid");
		searchArgs.put("message", message);
		SimpleSyncTask task = new SimpleSyncTask().setSyncListener(addCommentListener)
		.setMethodName(Constants.MENTHOD.EMEMBER_ADDCOMMENT).setArgs(searchArgs);
		new Thread(task).start();
	}
	
	class CommentsAdapter extends BaseAdapter {
		private ArrayList<ArrayList<String>> mdataList;
		private Context mcontext;

		public CommentsAdapter(ArrayList<ArrayList<String>> dataList, Context context) {
			this.mdataList = dataList;
			this.mcontext = context;
		}

		@Override
		public int getCount() {
			return mdataList.size();
		}

		@Override
		public ArrayList<String> getItem(int position) {
			return mdataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater lai = LayoutInflater.from(mcontext);
			ViewHolder vh = null;
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = lai.inflate(R.layout.comments_item, null);
				vh.tv_author = ((TextView) convertView.findViewById(R.id.tv_author));
				vh.tv_dateline = ((TextView) convertView.findViewById(R.id.tv_dateline));
				vh.tv_message = ((TextView) convertView.findViewById(R.id.tv_message));
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			ArrayList<String> item = getItem(position);
			vh.tv_author.setText(item.get(4));
			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(Long.valueOf(item.get(2)) * 1000));
			vh.tv_dateline.setText(date);
			vh.tv_message.setText(item.get(1));
			return convertView;
		}

		class ViewHolder {
			TextView tv_author; // 名称
			TextView tv_dateline; // 投资方式
			TextView tv_message; // 简介
		}
	}
	
	private void toComments() {
		View view = LayoutInflater.from(mcontext).inflate(R.layout.comments_result_list, null);
		final EditText tv_message = (EditText) view.findViewById(R.id.tv_message);
		AlertDialog dialog = new AlertDialog.Builder(mcontext)
		.setView(view)
		.setTitle("添加评论")
		.setPositiveButton(R.string.GENERAL_CONFIRM, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				String message = tv_message.getText().toString();
				if(StringUtil.isEmpty(message)) {
					Toast.makeText(mcontext, "请填写评论内容后再发表", Toast.LENGTH_SHORT).show();
				} else {					
					addComments(message);
				}
			}
		}).setNegativeButton(R.string.GENERAL_CANCEL, null).create();
		dialog.show();
	}
	
	/**
	 * 照片
	 * 
	 * @param uid
	 * @param alias
	 */
	public void syncAlbum() {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("id", eid);
		args.put("idtype", Constants.PHOTO_TYPE.ENTERPRISE);

		SyncService.start(new SyncTask().setSyncListener(new SimpleSyncListener(mcontext, true) {
			@Override
			public void onFailed() {
				super.onFailed();
				if (StringUtil.isEmpty(tips)) {
					showToast(R.string.SYNC_MSG_FAILED);
				} else {
					showToast(tips);
				}
			}

			@Override
			public void onSuccess() {
				super.onSuccess();
				try {
					SyncTableAnalysis analysis = new SyncTableAnalysis((Map<String, ?>) this.tag);
					List<String> row = (List<String>) analysis.tableRow(null, 1);
					if (row != null) {
						setPhoto(R.id.head_img1, row.get(1));
					}
					row = (List<String>) analysis.tableRow(null, 2);
					if (row != null) {
						setPhoto(R.id.head_img2, row.get(1));
					}
					row = (List<String>) analysis.tableRow(null, 3);
					if (row != null) {
						setPhoto(R.id.head_img3, row.get(1));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).setMethodName(Constants.MENTHOD.COMMON_PHOTO).setSyncHandler(null).setArgs(args));
	}
	
	protected void showToast(String res) {
		Toast.makeText(mcontext, res, Toast.LENGTH_SHORT).show();
	}
	
	protected void showToast(int resId) {
		Toast.makeText(mcontext, resId, Toast.LENGTH_SHORT).show();
	}
	
	public void setPhoto(int imgId, String file) {
		final ImageView imageView = (ImageView) headerView.findViewById(imgId);
		String miniFile = AsyncImageLoader.imageMiniFileName(file);
		UrlImageViewHelper.setUrlDrawable(imageView, AsyncImageLoader.builderUrl(miniFile));
	}
}