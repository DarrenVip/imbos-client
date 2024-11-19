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
 * 解析返回的表格数据
 * @author xianze
 *
 */
public class SyncTableAnalysis {
	private static final String TAG = SyncTableAnalysis.class.getSimpleName();
	
	private Map<String,?> response;
	private List<String> tables;
	private List<?> dataset;
	
	public SyncTableAnalysis(Map<String,?> response){
		this.response = response;
		this.analysisTables();
	}
	
	@SuppressWarnings("unchecked")
	public void analysisTables(){
		tables = (List<String>)response.get("dt");
		dataset= (List<String>)response.get("ds");
	}
	
	public List<?> tableRow(String tableName,int index){
		List<List<?>> table = this.table(tableName);
		if(table!=null && table.size()>index){
			return table.get(index);
		}else{
			return null;
		}
		
	}
	@SuppressWarnings("unchecked")
	public List<List<?>> table(String tableName){
		if(tableName==null){
			return (List<List<?>>) dataset.get(0);
		}else{
			int len = tables.size();
			for (int i = 0; i < len; i++) {
				if(tableName.equals(tables.get(i))){
					return (List<List<?>>) dataset.get(i);
				}
				
			}
		}
		return null;
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
	
	public static class DataTable{
		public int rowCount;
		public int columnCount;
		public String[] columns;
		public List<List<?>> rows;
		
		public int columnIndex(String name){
			int len = columns.length;
			for (int i = 0; i < len; i++) {
				if(name.equals(columns[i]))
					return i;
			}
			return -1;
		}
		public boolean hasColumn(String name){
			return columnIndex(name)>=0;
		}
		
	}

}
