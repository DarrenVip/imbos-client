package com.imbos.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;

/**
 * 文本框界面
 * @author xianze
 *
 */
public class EditTextActivity extends SkinActivity{
	
	private EditText editText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_txt_activity);
		
		editText = (EditText) findViewById(R.id.editText);
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
		boolean singleLine = getIntent().getBooleanExtra(Intents.EXTRA_SINGE, true);
		editText.setSingleLine(singleLine);
		if(!singleLine){
			editText.setMinLines(5);
		}
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_btn_next:
			Intent data = new Intent().putExtra(Intents.EXTRA_TEXT,
					editText.getText().toString());
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
