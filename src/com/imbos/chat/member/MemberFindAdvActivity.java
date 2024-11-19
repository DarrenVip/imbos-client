
package com.imbos.chat.member;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;

/**
 * 
 * @author 
 */
public class MemberFindAdvActivity extends SkinActivity {

	private ViewGroup headerView;
    private EditText editSearch;
    private HashMap<String,Object> searchCond = new HashMap<String,Object>();
 
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		setContentView(R.layout.find_member_adv);
        
		setTitle(R.string.CIRCLE_FIND_ADV);
        btnNext.setVisibility(View.INVISIBLE);
        
        headerView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.search_bar, null);
        editSearch = (EditText)findViewById(R.id.edit_search);
        
        findViewById(R.id.item_age).setOnClickListener(this);
        findViewById(R.id.item_area).setOnClickListener(this);
        findViewById(R.id.item_sex).setOnClickListener(this);
        findViewById(R.id.item_industry).setOnClickListener(this);
        findViewById(R.id.item_profession).setOnClickListener(this);
        findViewById(R.id.btn_green).setOnClickListener(this);
        
        
        
		searchCond.put("key", editSearch.getText().toString());
		searchCond.put("age", null);
		searchCond.put("sex", null);
		searchCond.put("industry",null);
		searchCond.put("profession",null);
		searchCond.put("page","1");
       
    }
	@Override
	protected void onNewIntent(Intent intent) {
		
		int itemId = intent.getIntExtra(Intents.EXTRA_MENU_ID, 0);
		String text = intent.getStringExtra(Intents.EXTRA_TEXT);
		String id = intent.getStringExtra(Intents.EXTRA_ID);
		ArrayList<String> ids = intent.getStringArrayListExtra(Intents.EXTRA_ID);
		
		switch (itemId) {
			case R.id.item_age:
				((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
				searchCond.put("age",id);
				
				break;
			case R.id.item_area:
				((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
				searchCond.put("area",text);
				
				break;
			case R.id.item_sex:
				((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
				searchCond.put("sex",id);
				break;
			case R.id.item_profession:
				((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
				searchCond.put("industry",ids);
				
				break;
			case R.id.item_industry:
				((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
				searchCond.put("profession",ids);
				break;
			default:
				break;
		}
		super.onNewIntent(intent);
	}
	
    @Override
    public void onClick(View view) {
    	switch (view.getId()) {
		case R.id.title_btn_next:
			//queryMembers(editSearch.getText().toString());
			//queryMembers("114.393580555941","30.5083586259591");
			break;
		case R.id.item_area:
			startActivity(new Intent(view.getContext(), RegionActivity.class)
			.putExtra(Intents.EXTRA_MENU_ID, view.getId()));
			break;
		case R.id.item_age:
		case R.id.item_sex:
		case R.id.item_profession:
		case R.id.item_industry:
			DicActivity.srcActivity = this.getClass();
			startActivity(new Intent(view.getContext(), DicActivity.class)
			.putExtra(Intents.EXTRA_MENU_ID, view.getId()));
			break;
		case R.id.btn_green:
			queryMembers();
			break;
		default:
			break;
		}
    	super.onClick(view);
    }
    @Override
    protected void onStart() {
    	super.onStart();
    }
    @Override
    protected void onResume() {
    	///listView.setAdapter(cursorAdapter);
    	super.onResume();
    }
    
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
    	@Override
    	public void onItemClick(AdapterView<?> adapterView, View arg1, int position,
    			long id) {	
			startActivity(new Intent(MemberFindAdvActivity.this,
					MemberDetailActivity.class).putExtra(Intents.EXTRA_UID,
					String.valueOf(id)));
    	}
	};
	
	
	
	public void queryMembers(){
		searchCond.put("key", editSearch.getText().toString());
		searchCond.put("page","1");
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this){
					@Override
					public void onBefore() {
						super.onBefore();
						DbManager.delMembers(Constants.MENTHOD.MEMBER_FINDADV);
					}
					@Override
					public void onFailed() {
						super.onFailed();
					}
					
					@Override
					public void onSuccess() {
						super.onSuccess();
						startActivity(new Intent(MemberFindAdvActivity.this,
								MemberFindResultActivity.class)
								.putExtra(Intents.EXTRA_DATA,(Serializable)searchCond)
								);
						
					}
				}).setMethodName(Constants.MENTHOD.MEMBER_FINDADV)
			.setArgs(searchCond));
	}
	
}