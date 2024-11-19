package com.imbos.chat.setting;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

import com.imbos.chat.R;
import com.imbos.chat.app.ChatApp;
import com.imbos.chat.util.SharedUtils;

/**
 * 
 * @author Kingly
 * 2012-1-17
 */
public class SettingActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	private SharedPreferences appSetting;
	private SharedUtils sharedUtils;
	private Config config;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.setting);
		
		this.bindSetting();
	}
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if("saasPath".equals(key)||"saasPort".equals(key) || "saasHost".equals(key))
		{
			this.findPreference(key).setSummary(
					sharedPreferences.getString(key,""));
		}
	}
	public void bindSetting(){
		
		this.sharedUtils = SharedUtils.instance(this);
		this.appSetting = this.sharedUtils.getSharedPreferences();
		this.config = ChatApp.getConfig();
		
		appSetting.registerOnSharedPreferenceChangeListener(this);
		this.findPreference("saasHost").setSummary( appSetting.getString("saasHost",config.saasHost));
		this.findPreference("saasPort").setSummary( appSetting.getString("saasPort",config.saasPort));
		this.findPreference("saasPath").setSummary( appSetting.getString("saasPath",config.saasPath));
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,1,1,R.string.GENERAL_DEFAULT);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==1){
			try {
				sharedUtils.clear();
				ChatApp.resetConfig();
				this.bindSetting();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public void finish() {
		try {
			ChatApp.resetConfig();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.finish();
	}
}
