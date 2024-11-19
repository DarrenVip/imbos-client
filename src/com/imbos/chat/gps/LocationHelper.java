package com.imbos.chat.gps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.imbos.chat.R;


/**
 * 定位
 * 
 * 
 */
public class LocationHelper {
	private final static String TAG = "GPSModule";
	private final int SCAN_SPAN = 5000;
	// 消息提示时间30秒
	private final int MESSAGE_TIME = 5000 * 6;
	private LocationClient mLocationClient = null;
	private Context mContext;
	private int mTimes = 0;
	private ProgressDialog mProgressDialog;
	public BdLocationListener mActionListener;

	public LocationHelper(Context context, BdLocationListener listener) {
		this.mContext = context;
		this.mActionListener = listener;
		init();
	}
	
	private void init(){
		mLocationClient = new LocationClient(mContext);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型为bd09ll
		option.setPriority(LocationClientOption.GpsFirst); // 设置GPS优先
		option.setProdName("SFA"); // 设置产品线名称
		option.setScanSpan(SCAN_SPAN); // 定时定位，每隔5秒钟定位一次。
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				mTimes += SCAN_SPAN;
				System.out.println(mTimes);
				if (mTimes >= MESSAGE_TIME) {
					mTimes = 0;
					stop();
					String tip = mContext.getString(R.string.GENERAL_TIP);
					String timeOut = mContext
							.getString(R.string.ORIENTATION_TIMEOUT);
					String confirm = mContext
							.getString(R.string.GENERAL_CONFIRM);
					String cancel = mContext.getString(R.string.GENERAL_CANCEL);

					new AlertDialog.Builder(mContext).setTitle(tip).setMessage(
							timeOut).setCancelable(false).setPositiveButton(
							confirm, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									start();
								}
							}).setNegativeButton(cancel, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							if(mProgressDialog != null){
								mProgressDialog.dismiss();	
							}

							if (mActionListener != null) {
								// 无GPS需要拍照
								mActionListener.needTakePicture(true);
							}
							clean();
						}
					}).show();
				}

				if (location == null) {
					return;
				}
				if (mActionListener != null && 
						(location.getLocType() == BDLocation.TypeGpsLocation ||
						location.getLocType() == BDLocation.TypeNetWorkLocation)
						) {
					mProgressDialog.dismiss();
					Log.d(TAG,location.getLongitude()+";"+location.getLatitude()+";"+location.getAddrStr());
					mActionListener.locationListener(location.getLongitude(),
							location.getLatitude(),location.getAddrStr());
					
					clean();
				}

			}

			public void onReceivePoi(BDLocation location) {
			}
		});
	}

	private DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			clean();
		}
	};

	/**
	 * 开始定位
	 */
	public void start() {
		if(mLocationClient == null){
			init();
		}
		if(mLocationClient.isStarted()){
			mLocationClient.stop();
		}
		mLocationClient.start();
		
		if (mProgressDialog == null) {
			String loading = mContext.getString(R.string.ORIENTATION_LOADING);
			// 滚动条   
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage(loading);
			mProgressDialog.setOnDismissListener(dismissListener);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface dialog,
								int keyCode, KeyEvent event) {
							if (keyCode == KeyEvent.KEYCODE_BACK) {
								return true;
							}
							return false;
						}
					});
		}

		mProgressDialog.show();
		mProgressDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
	}

	/**
	 * 停止定位
	 */
	public void stop() {
		if(mLocationClient != null){
			mLocationClient.stop();	
		}
	}
	
	
	/***
	 * 清楚定位模块
	 */
	public void clean() {
		if (mLocationClient != null) {
			mLocationClient.stop();
			mLocationClient = null;
		}
		mContext = null;
		mProgressDialog = null;
		mActionListener = null;
	}

	/** 经纬度处理接口 */
	public abstract interface BdLocationListener {
		public abstract void locationListener(double longitude, double latitude,String addr);

		public abstract void needTakePicture(boolean take);
	}
}
