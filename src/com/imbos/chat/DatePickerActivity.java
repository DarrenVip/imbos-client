package com.imbos.chat;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.util.StringUtil;

/**
 * 日期
 * @author xianze
 *
 */
public class DatePickerActivity extends SkinActivity{
	
	private DatePicker datePicker;
	private Calendar calendar;
	private String text;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.date_activity);
		calendar = Calendar.getInstance();
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		setButton(btnNext, 0, R.string.GENERAL_CONFIRM);
		
		if(getIntent().hasExtra(Intents.EXTRA_TITLE)){
			int titleId = getIntent().getIntExtra(Intents.EXTRA_TITLE, 0);
			
			if(titleId==0){
				String title = getIntent().getStringExtra(Intents.EXTRA_TITLE);
				setTitle(title);
			}
			else
				setTitle(titleId);
		} 
		
		text = getIntent().getStringExtra(Intents.EXTRA_TEXT);
		if(!StringUtil.isEmpty(text)){
			calendar.setTime(DateUtil.formatStr2Date(text, DateUtil.C_DATE_PATTON_DEFAULT));
		}
		datePicker.init(calendar.get(Calendar.YEAR), 
				calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH),
				new DatePicker.OnDateChangedListener() {
			
				@Override
				public void onDateChanged(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					calendar.set(year, monthOfYear, dayOfMonth);
				}
		});
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_btn_next:
			
			String text = DateUtil.formatDate2Str(calendar.getTime(),DateUtil.C_DATE_PATTON_DEFAULT);
			
			Intent data = new Intent()
				.putExtra(Intents.EXTRA_TEXT,text);
			setResult(RESULT_OK, data);
			finish();
			break;
		case R.id.title_btn_back:
			finish();
			break;
		default:
			super.onClick(view);
		}
		
	}
}
