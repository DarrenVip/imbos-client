package com.imbos.chat.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import com.imbos.chat.ChatActivity;
import com.imbos.chat.R;
import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.sync.DataSyncListener;
import com.imbos.chat.sync.SimpleSyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.StringUtil;
import com.imbos.chat.view.EmojiGetter;
import com.imbos.chat.view.urlimagehelper.UrlImageViewHelper;


/**
 * 
 * @author
 */
public class EPMemberDetailHomepageView implements OnClickListener {
	private View headerView;
	private View mview;
	private Context mcontext;
	private ListView list_consults;
	
	private ImageView logo;
	private TextView shortname;
	private TextView appid;
	private TextView name;
	private ImageView vip;
	private ImageView real;
	private TextView area;
	private TextView txt_dynamic;
	private TextView txt_scale;
	private TextView txt_industry;
	private TextView txt_summary;
	private Button btn_comments;
	private TextView txt_praise;
	private TextView txt_attention;
//	private LinearLayout layout_comments;
	
	private boolean isloaded = false;
	private String eid;
	private HashMap<String, Object> searchCond = new HashMap<String, Object>();
	private DataSyncListener syncListener;
	
	private DataSyncListener consultListListener;
	private DataSyncListener addCommentListener;
	private DataSyncListener praiseListener;
	private DataSyncListener attentionListener;
	
	private ArrayList<ArrayList<String>> datalist;
	private ConsultsAdapter consultsAdapter;
	
	public EPMemberDetailHomepageView(Context mcontext, String eid) {
		this.mcontext = mcontext;
		this.eid = eid;
		initView();
	}
	
	private void initView() {
		mview = LayoutInflater.from(mcontext).inflate(R.layout.normal_comments_list, null);
		list_consults = (ListView) mview.findViewById(R.id.list_comments);
		datalist = new ArrayList<ArrayList<String>>();
		list_consults.setDivider(new ColorDrawable(0x00ffffff));
		list_consults.setDividerHeight(1);
		
		headerView = LayoutInflater.from(mcontext).inflate(R.layout.emember_homepage, null);
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
				list_consults.addHeaderView(headerView);
				getCommentsList();
			}
		};
		
		consultListListener = new DataSyncListener(this.mcontext) {
			@Override
			public void onFailed() {
				datalist.clear();
				consultsAdapter = new ConsultsAdapter(datalist, mcontext);
				list_consults.setAdapter(consultsAdapter);
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
				consultsAdapter = new ConsultsAdapter(datalist, mcontext);
				list_consults.setAdapter(consultsAdapter);
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
		txt_dynamic = (TextView) mview.findViewById(R.id.txt_dynamic);
		txt_scale = (TextView) mview.findViewById(R.id.txt_scale);
		txt_industry = (TextView) mview.findViewById(R.id.txt_industry);
		txt_summary = (TextView) mview.findViewById(R.id.txt_summary);
		btn_comments = (Button) mview.findViewById(R.id.btn_comments);
		txt_praise = (TextView) mview.findViewById(R.id.txt_praise);
		txt_attention = (TextView) mview.findViewById(R.id.txt_attention);
//		layout_comments = (LinearLayout) mview.findViewById(R.id.layout_comments);
//		if(eid.equalsIgnoreCase(ChatApp.getConfig().saasUid)) {
//			layout_comments.setVisibility(View.GONE);
//		}
		
//		btn_comments.setOnClickListener(this);
		txt_praise.setOnClickListener(this);
		txt_attention.setOnClickListener(this);
	}
	
	@SuppressWarnings("unchecked")
	private void setChildViewValue(HashMap<String, ?> response) {
		List<?> ds = (List<?>) response.get("ds");
		List<?> list = (List<?>) ds.get(0);
		ArrayList<String> data = (ArrayList<String>) list.get(1);
		
		UrlImageViewHelper.setUrlDrawable(logo, AsyncImageLoader.builderUrl(data.get(14)));
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
	}
	
	public void queryEMemberDetailHome() {
		searchCond.put("eid", eid);
		SimpleSyncTask task = new SimpleSyncTask().setSyncListener(syncListener)
		.setMethodName(Constants.MENTHOD.EMEMBER_FINDHOME).setArgs(searchCond);
		new Thread(task).start();
	}
	
	public void initData() {
		if(isloaded == false) {
			queryEMemberDetailHome();
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
		searchArgs.put("eid", eid);
		SimpleSyncTask task = new SimpleSyncTask().setSyncListener(consultListListener)
		.setMethodName(Constants.MENTHOD.EMEMBER_FINDCONSULT).setArgs(searchArgs);
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
	
	class ConsultsAdapter extends BaseAdapter {
		private ArrayList<ArrayList<String>> mdataList;
		private Context mcontext;

		public ConsultsAdapter(ArrayList<ArrayList<String>> dataList, Context context) {
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
				convertView = lai.inflate(R.layout.consults_item, null);
				vh.tv_name = ((TextView) convertView.findViewById(R.id.tv_name));
				vh.tv_unum = ((TextView) convertView.findViewById(R.id.tv_unum));
				vh.btn_consult = ((Button) convertView.findViewById(R.id.btn_consult));
				vh.img_face = (ImageView) convertView.findViewById(R.id.img_face);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
    		
			ArrayList<String> item = getItem(position);
			vh.tv_name.setText(item.get(1));
			vh.tv_unum.setText(item.get(4));
			vh.btn_consult.setOnClickListener(new MOnClickListener(item.get(0), item.get(1)));
			UrlImageViewHelper.setUrlDrawable(vh.img_face, AsyncImageLoader.builderUrl(item.get(2)), R.drawable.default_face);
			return convertView;
		}

		class ViewHolder {
			TextView tv_name;
			TextView tv_unum;
			Button btn_consult;
			ImageView img_face;
		}
	}
	
	class MOnClickListener implements OnClickListener {
		private String uid;
		private String name;
		public MOnClickListener(String uid, String name) {
			this.uid = uid;
			this.name = name;
		}
		@Override
		public void onClick(View v) {
			mcontext.startActivity(new Intent(mcontext, ChatActivity.class)
			.putExtra(Intents.EXTRA_MSG_TOS, uid).putExtra(Intents.EXTRA_TITLE, name));
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
}