package com.imbos.chat.sync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.setting.Config;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.util.SharedUtils;
import com.imbos.chat.util.StringUtil;
/**
 * 同步数据到数据库
 * @author xianze
 *
 */
public class SyncSqliteHandler extends SyncHandler{
	private static final String TAG = SyncSqliteHandler.class.getSimpleName();
	private String methodName;
	public SyncSqliteHandler() {
		
	}
	@Override
	public boolean handle(SyncTask task) {
		Config config = ChatApp.getConfig();
		HashMap<String,?> response = task.getResponse();
		methodName = task.getMethodName();
		
		if(null!=task.getFile())
			return true;
		else if(Constants.MENTHOD.FRIEND_ADD.equals(methodName))
			return true;
		else if(Constants.MENTHOD.LOGN.equals(methodName)){
			
			String token = StringUtil.toString(response.get("t"));
			
			config.saasToken = token;
			config.saasUid = token.substring(1);
			Context ctx = ChatApp.getContext();
			
			SharedUtils.instance(ctx)
			.putString(Constants.XMPP_USERNAME, config.saasUid)
			.putString(Constants.XMPP_PASSWORD, config.saasToken);
			
			ChatApp.getConfig().save(ctx);
			
			return true;
		}else if(Constants.MENTHOD.LOGIN_REGISTERED.equals(methodName)){
			List<?> ds = (List<?>) response.get("ds");
			
			String token = StringUtil.toString(tableValue(ds, "token"));
			String num = StringUtil.toString(tableValue(ds, "num"));
			
			config.saasLoginame = num;
			config.saasToken = token;
			config.saasUid = token.substring(1);
			Context ctx = ChatApp.getContext();
			
			SharedUtils.instance(ctx)
			.putString(Constants.XMPP_USERNAME, config.saasUid)
			.putString(Constants.XMPP_PASSWORD, config.saasToken);
			
			ChatApp.getConfig().save(ctx);
			return true;
		}else {
			List<String> dt = (List<String>) response.get("dt");
			List<?> ds = (List<?>) response.get("ds");
			int len = dt.size();
			int count=0;
			for (int i = 0; i < len; i++) {
				boolean flag = syncTable(dt.get(i).toString(),(List<List<Object>>)ds.get(i));
				count+=flag?1:0;
			}
			return count==dt.size();
		}

	}
	
	
	public static Object tableValue(List<?> ds,String filed){
		Map<String,Object> map = new HashMap<String, Object>();
		int len=ds.size();
		for (int i = 0; i < len; i++) {
			List<List<Object>> data = (List<List<Object>>) ds.get(i);
			List<Object> h=data.get(0);
			for (int j = 1; j < data.size(); j++) {
				List<Object> r=data.get(j);
				for (int f = 0; f < r.size(); f++) {
					map.put(StringUtil.toString(h.get(f)), r.get(f));
				}
			}
		}
		return map.get(filed);
		
	}
	public boolean syncTable(String table,List<List<Object>> data){
		String sql_save = "";
		StringBuilder sql_filed = new StringBuilder();
		StringBuilder sql_value = new StringBuilder();
		SQLiteDatabase db = ChatApp.getDbHelper().getWritableDatabase();
		String updateDate = DateUtil.formatDate2Str();
		try {
			 
			db.beginTransaction();
			int len = data.size();
			for (int i = 0; i < len; i++) {
				List<Object> row = data.get(i);
				if(i==0){
					
					//添加本地通用字段
					row.add("UPDATE_DATE");
					row.add("SRC_ACTION");
					
					sql_filed.append(" (");
					sql_value.append(" (");
					for (Object value : row) {
						sql_filed.append(value).append(",");
						sql_value.append("?,");
					}
					sql_filed.deleteCharAt(sql_filed.length()-1)
						.append(")");
					sql_value.deleteCharAt(sql_value.length()-1)
						.append(")");
					
				}else{
					sql_save = sql_save.length()>1?sql_save:
						" REPLACE INTO "+table+sql_filed.toString()+" VALUES "+sql_value.toString();
					
					//添加本地通用字段
					row.add(updateDate);
					row.add(methodName);
					
					db.execSQL(sql_save,row.toArray());
				}
			}
			db.setTransactionSuccessful();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}finally{
			db.endTransaction();
		}
		
	}
	
	
}
