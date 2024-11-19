package com.imbos.chat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.imbos.chat.app.SkinActivity;

public class WebActivity extends SkinActivity {
	public static final String EXTRA_LINK="web.link";
	private static final String TAG = ChromeClient.class.getSimpleName();
	private static final int DIALOG_LADING = 0x123;
	private static final int DIALOG_EXIT = 0x124;
	private static final int REQUEST_EDIT = 0x125;
	private WebView webview;
	private ProgressBar progressBar;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		
		btnNext.setVisibility(View.INVISIBLE);
		
		progressBar = (ProgressBar) findViewById(R.id.progress);
		webview = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = webview.getSettings();
		webSettings.setAppCacheEnabled(false);
		webSettings.setJavaScriptEnabled(true);
		webview.setWebViewClient(new BaseWebViewClient ());
		webview.setWebChromeClient(new ChromeClient());
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		
		String url = "http://www.baidu.com";
		
		Intent data = getIntent();
		if(data.hasExtra(EXTRA_LINK)){
			url = data.getStringExtra(EXTRA_LINK);
		}
		
		webview.loadUrl(url);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_CANCELED)
			return;
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_EXIT:
			return new AlertDialog.Builder(this)
				.setTitle(R.string.GENERAL_TIP)
				.setMessage(R.string.GENERAL_CONFIRM_EXIT)
				.setPositiveButton(R.string.GENERAL_EXIT,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				})
				.setNegativeButton(R.string.GENERAL_CANCEL,null)
				.create();
		case DIALOG_LADING:
			return new AlertDialog.Builder(this)
				.setTitle(R.string.GENERAL_CONFIRM)
				.setMessage(R.string.GENERAL_NET_PROCESS)
				.create();
		default:
			return super.onCreateDialog(id);
		}

	}
	@Override
	public void onBackPressed() {
		if(webview.canGoBack())
			webview.goBack();
		else{
			//showDialog(DIALOG_EXIT);
			finish();
		}
	}
	class ChromeClient extends WebChromeClient {
		

		// 处理Alert事件
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			// 构建一个Builder来显示网页中的alert对话框
			new AlertDialog.Builder(WebActivity.this).setTitle("提示")
					.setMessage(message).setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									result.confirm();
								}

							}).setCancelable(false).create().show();

			return true;

		}

		@Override
		public void onReceivedTitle(WebView view, String title) {

			//WebActivity.this.setTitle("可以用onReceivedTitle()方法修改网页标题");
			//Toast.makeText(WebActivity.this, title, Toast.LENGTH_SHORT).show();
			setTitle(title);
			super.onReceivedTitle(view, title);

		}
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			progressBar.setProgress(newProgress);
			Log.d(TAG, "onProgressChanged:"+newProgress);
			if(progressBar.getProgress()==progressBar.getMax())
				progressBar.setVisibility(View.GONE);
			else
				progressBar.setVisibility(View.VISIBLE);
			//super.onProgressChanged(view, newProgress);
		}
		// 处理Confirm事件

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			new AlertDialog.Builder(WebActivity.this).setTitle("确认")
					.setMessage(message).setPositiveButton("确定",
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									result.confirm();
								}
							}).setNeutralButton(android.R.string.cancel,
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									result.cancel();
								}
							}).setCancelable(false).create().show();
			return true;
		}

		// 处理提示事件

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue,

				JsPromptResult result) {
			// 看看默认的效果
			return super.onJsPrompt(view, url, message, defaultValue, result);
		}
		
	}
	
	
	class BaseWebViewClient extends WebViewClient {
		public BaseWebViewClient() {
			super();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub

			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Toast.makeText(
					WebActivity.this,
					"加载" + failingUrl + "失败 [code=" + errorCode + "]"
							+ description, Toast.LENGTH_LONG).show();
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}
}