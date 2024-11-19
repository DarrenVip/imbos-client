package com.imbos.chat.find;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.model.NormalDetail;
/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class NormalDetailActivity extends SkinActivity {
	private TextView txt_title;
	private TextView txt_detail;
	private TextView txt_introtitle;
	private TextView txt_intro;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.normal_detail);
		NormalDetail temp = (NormalDetail) getIntent().getSerializableExtra("detail");
		setTitle(temp.getMainTitle());
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_detail = (TextView) findViewById(R.id.txt_detail);
		txt_introtitle = (TextView) findViewById(R.id.txt_introtitle);
		txt_intro = (TextView) findViewById(R.id.txt_intro);
		
		txt_title.setText(temp.getTitle());
		txt_detail.setText(temp.getDetail());
		txt_introtitle.setText("---------------简介---------------");
		txt_intro.setText(temp.getIntro());
		
		btnNext.setVisibility(View.INVISIBLE);
	}
}
