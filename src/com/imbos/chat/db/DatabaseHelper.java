package com.imbos.chat.db;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imbos.chat.R;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class DatabaseHelper extends SQLiteOpenHelper{
	private static final String TAG = DatabaseHelper.class.getSimpleName();
	private Context ctx;
	private String name;
	private DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.ctx = context;
		this.name = name;
	}
	public DatabaseHelper(Context context,int version) {
		this(context,"imbos",null,version);
	}
	public DatabaseHelper(Context context,String name,int version) {
		this(context,name,null,version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] sqls =  this.ctx.getString(R.string.create_sql).split(";");
		execMultipleSQL(db,sqls);
	}
	private void execMultipleSQL(SQLiteDatabase db,String[] sqls) {
		try {
			db.beginTransaction();
			for (int i = 0; i < sqls.length; i++) {
				db.execSQL(sqls[i]);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG,"create db exception",e);
		}finally{
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		try {
			String[] sqls =  this.ctx.getString(R.string.update_sql).split(";");
			this.execMultipleSQL(db,sqls);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public void execSQL(String sql){
		Log.d(TAG, sql);
		getWritableDatabase().execSQL(sql);
	}
	public void execSQL(String sql,Object[] bindArgs){
		Log.d(TAG, sql);
		getWritableDatabase().execSQL(sql, bindArgs);
	}
	

	public SQLiteCursor query(String sql){
		return  (SQLiteCursor) getReadableDatabase().rawQuery(sql,null);
	}
	
	public SQLiteCursor query(String sql,String[] selectionArgs){
		return (SQLiteCursor) getReadableDatabase().rawQuery(sql, selectionArgs);
	}
	
	
    public List<Map<String,?>> rawQuery(String sql,String[] args){
    	Log.d(this.getClass().toString(), sql);
		List<Map<String,?>> result = new ArrayList<Map<String,?>>();
		SQLiteDatabase database = getReadableDatabase();
		Cursor cursor=null;
		try {
			cursor= getWritableDatabase().rawQuery(sql, args);
			int columnCount = cursor.getColumnCount();
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String,String>(); 
				for (int i = 0; i < columnCount; i++) {
					map.put(cursor.getColumnName(i),cursor.getString(i));
				}
				result.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed())
				cursor.close();
			if(database!=null && database.isOpen())
				database.close();
		}
		return result;
	}
    public Map<String,?> rawQueryTop(String sql,String[] args){
    	Log.d(this.getClass().toString(), sql);
    	Map<String, String> result = new HashMap<String,String>(); 
		Cursor cursor = null;
		SQLiteDatabase database =getWritableDatabase();
		try{	
			cursor = database.rawQuery(sql, args);
			int columnCount = cursor.getColumnCount();
			if(cursor.moveToNext()) {
				for (int i = 0; i < columnCount; i++) {
					result.put(cursor.getColumnName(i),cursor.getString(i));
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor!=null && !cursor.isClosed())
				cursor.close();
			if(database!=null && database.isOpen())
				database.close();
		}
		return result;
	}
    public String executeScalar(String sql,String[] args){
    	String result = null;
    	Cursor cursor = null;
		SQLiteDatabase database =getWritableDatabase();
		try{	
			cursor = database.rawQuery(sql, args);
			if(cursor.moveToNext()) {
				result=cursor.getString(0);
			}
		}catch (Exception e) {
			//Log.e(TAG,e.getLocalizedMessage());
		}finally{
			if(cursor!=null && !cursor.isClosed())
				cursor.close();
		}
		return result;
    }
    public List<String> queryList(String sql,String[] args){
    	List<String> result = null;
    	Cursor cursor = null;
		SQLiteDatabase database =getWritableDatabase();
		try{	
			cursor = database.rawQuery(sql, args);
			result = new ArrayList<String>(cursor.getCount());
			while (cursor.moveToNext()) {
				result.add(cursor.getString(0));
			}
		}catch (Exception e) {
			//Log.e(TAG,e.getLocalizedMessage());
		}finally{
			if(cursor!=null && !cursor.isClosed())
				cursor.close();
		}
		return result;
    }
//    public boolean saveMapEntity(Map<String, ?> map){
//    	
//    }
   
    public ArrayList<ArrayList<String>> queryData(String sql, String[] args) {
		Log.d(TAG, sql);
		ArrayList<ArrayList<String>> result = null;
		Cursor cursor = null;
		SQLiteDatabase database = getWritableDatabase();
		try {
			cursor = database.rawQuery(sql, args);
			result = new ArrayList<ArrayList<String>>(cursor.getCount());
			int columnCount = cursor.getColumnCount();
			while (cursor.moveToNext()) {
				ArrayList<String> row = new ArrayList<String>();
				for (int i = 0; i < columnCount; i++) {
					row.add(cursor.getString(i));
				}
				result.add(row);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage());
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
		}
		return result;
	}
}
