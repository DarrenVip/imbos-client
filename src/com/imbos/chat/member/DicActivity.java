package com.imbos.chat.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.util.StringUtil;

/**
 * 字典表通用数据
* <p>Title: DicActivity.java</p>
* <p>Description:SFA</p>
* <p>Copyright: Copyright (c) 2012</p>
* <p>Company:ebestmobile</p>
* @author wanxianze@gmail.com
* @date 2013-4-24 上午11:12:25
* @version V1.0
 */
public class DicActivity extends SkinActivity{
	
	public static Class<?> srcActivity;
	private ListView listView ;
	private SimpleAdapter adapter;
	private List<Map<String,?>> data=new ArrayList<Map<String,?>>();
	private String parentId;
	private String text;
	private boolean isCheck;
	private int menuId;
	private int step;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_area);
		
		listView = (ListView) findViewById(android.R.id.list);
		
		parentId = getIntent().getStringExtra(Intents.EXTRA_ID);
		menuId = getIntent().getIntExtra(Intents.EXTRA_MENU_ID,0);
		String title = getIntent().getStringExtra(Intents.EXTRA_TITLE);
		text = getIntent().getStringExtra(Intents.EXTRA_TEXT);
		text = StringUtil.toString(text);
		step = getIntent().getIntExtra("step",0);
		step++;
		if(StringUtil.isEmpty(title)){
			
		}else{
			setTitle(title);
		}
		
		switch (menuId) {
		case R.id.item_area:
			data.addAll(DbManager.queryDicItems("area", parentId));
			break;
		case R.id.item_age:
			data.addAll(DbManager.queryDicItems("age", parentId));
			break;
		case R.id.item_sex:
			data.addAll(DbManager.queryDicItems("sex", parentId));
			break;
		case R.id.item_profession:
			data.addAll(DbManager.queryDicCategoryItems("profession"));
			isCheck = true;
			break;
		case R.id.item_industry:
			isCheck = true;
			data.addAll(DbManager.queryDicItems("industry", parentId));
			break;
		case R.id.menu_funds_investment:
			isCheck = true;
			data.addAll(DbManager.queryDicItems("investmentway", parentId));
			break;
		case R.id.menu_funds_strength:
			data.addAll(DbManager.queryDicItems("financialstrength", parentId));
			break;
		case R.id.menu_funds_field:
			isCheck = true;
			data.addAll(DbManager.queryDicItems("industry", parentId));
			break;
		case R.id.menu_subject_financing:
			isCheck = true;
			data.addAll(DbManager.queryDicItems("financingway", parentId));
			break;
		case R.id.menu_subject_industry:
			isCheck = true;
			data.addAll(DbManager.queryDicItems("industry", parentId));
			break;
		case R.id.menu_subject_assets:
			data.addAll(DbManager.queryDicItems("financialstrength", parentId));
			break;
		case R.id.menu_subject_amount:
		case R.id.menu_funds_cost:
			data.addAll(DbManager.queryDicItems("financialstrength", parentId));
			break;
		case R.id.menu_emember_industry:
		case R.id.menu_gs_industry:
			isCheck = true;
			data.addAll(DbManager.queryDicItems("industry", parentId));
			break;
		case R.id.menu_gs_classification:
			data.addAll(DbManager.queryDicItems("industryinvolved", parentId));
			break;
		case R.id.menu_emember_area:
		case R.id.menu_gs_area:
		case R.id.menu_ep_area:
			data.addAll(DbManager.queryDicItems("area", parentId));
			break;
		case R.id.menu_emember_range:
		case R.id.menu_ep_operate_type:
			isCheck = true;
			data.addAll(DbManager.queryDicItems("servicefield", parentId));
			break;
		case R.id.menu_ep_industry:
			data.addAll(DbManager.queryDicItems("industry", parentId));
			break;
		default:
			break;
		}
		
		adapter = new SimpleAdapter(this,data,R.layout.list_text_item,new String[]{"NAME"},new int[]{R.id.txt1}){
			@Override
			public long getItemId(int position) {
				Map<String,?> item = (Map<String, ?>) getItem(position);
				return Long.parseLong(StringUtil.toString(item.get("ID")));
			}
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = super.getView(position, convertView, parent);
				Map<String,?> item = (Map<String, ?>) getItem(position);
				
				View category = convertView.findViewById(R.id.list_category);
				
				CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
				checkBox.setTag(item);
				checkBox.setOnCheckedChangeListener(checkedChangeListener);
				checkBox.setChecked("1".equals(item.get("CHECKED")));
				
				View subMenu = convertView.findViewById(R.id.subMenu);
				if(item.get("SUB")==null || "1".equals(item.get("SUB"))){
					subMenu.setVisibility(View.INVISIBLE);
					checkBox.setVisibility(isCheck?View.VISIBLE:View.GONE);
				}else{
					subMenu.setVisibility(View.VISIBLE);
				}
				if(item.containsKey("CATEGORY")){
					((TextView)convertView.findViewById(R.id.txt1)).setText(StringUtil.toString(item.get("NAME")));
					Map<String,?> last = position>0?(Map<String, ?>) getItem(position-1):null;
					
					if(last==null||!last.get("CATEGORY").equals(item.get("CATEGORY"))){
						category.setVisibility(View.VISIBLE);
						((TextView)category.findViewById(R.id.txt_category)).setText(StringUtil.toString(item.get("CATEGORY")));
					}else{
						category.setVisibility(View.GONE);
					}

					subMenu.setVisibility(View.INVISIBLE);
				}else{
					category.setVisibility(View.GONE);
				}
				
				
				return convertView;
			}
		};
		if(isCheck){
			btnNext.setText(R.string.GENERAL_CONFIRM);
			btnNext.setVisibility(View.VISIBLE);
		}else{
			btnNext.setVisibility(View.INVISIBLE);
		}
		
		listView.setOnItemClickListener(itemClickListener);
		listView.setAdapter(adapter);
	}
	
	@Override
	public void onClick(View view) {
		if(view.getId()== R.id.title_btn_next){
			ArrayList<String> ids = new ArrayList<String>();
			for (Map<String,?> item:data) {
				if("1".equals(item.get("CHECKED"))){
					text+=item.get("NAME")+",";
					ids.add(StringUtil.toString(item.get("ID")));
				}
			}
			startActivity(new Intent(this,srcActivity)
				.putExtra(Intents.EXTRA_ID,ids)
				.putExtra(Intents.EXTRA_TEXT,text)
				.putExtra(Intents.EXTRA_MENU_ID,menuId)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
		}else
			super.onClick(view);
	}
	
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Map<String,String> item = (Map<String, String>) buttonView.getTag();
			item.put("CHECKED", isChecked?"1":"0");
		}
	};
	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View arg1, int position,
				long id) {
			Map<String,?> item = (Map<String, ?>) adapter.getItem(position);
			String name = StringUtil.toString(item.get("NAME"));
			if(step==0)
				text="";	
			text = StringUtil.toString(text)+" "+name;
			if("1".equals(item.get("SUB"))){
				startActivity(new Intent(adapterView.getContext(), srcActivity)
				.putExtra(Intents.EXTRA_ID, StringUtil.toString(id))
				.putExtra(Intents.EXTRA_TEXT,text.trim())
				.putExtra(Intents.EXTRA_MENU_ID,menuId)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
			}else{
				
				
				
				startActivity(new Intent(adapterView.getContext(), DicActivity.class)
						.putExtra(Intents.EXTRA_ID, StringUtil.toString(id))
						.putExtra(Intents.EXTRA_MENU_ID,menuId)
						.putExtra(Intents.EXTRA_TITLE, StringUtil.toString(name))
						.putExtra(Intents.EXTRA_TEXT,text.trim())
						.putExtra("step",step));
			}
		}
	};
	
}
