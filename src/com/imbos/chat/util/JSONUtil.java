package com.imbos.chat.util;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * 
 * @author wanxianze@gmail.com
 * 2012-7-3
 */
public final class JSONUtil  {
	
	/**
	 * Map,ArrayList or Array  encode to JSON 
	 * @param object
	 * @return
	 * @throws JSONException
	 */
	public static String encode(Object object)
	{
		JSONStringer stringer = new JSONStringer();
		try {
			new JSONUtil().encode(object, stringer);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return stringer.toString();
	}
	/**
	 * JSON String decode to Map,ArrayList
	 * @param source
	 * @return
	 * @throws JSONException
	 */
	public static Object decode(String source)
	{
		Object  result = new Object();
		try {
				if('{'==source.trim().charAt(0))
					result = new JSONUtil().decodeJSONObject(new JSONObject(source));
				else
					result = new JSONUtil().decodeJSONArray(new JSONArray(source));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private  void encode(Object obj,JSONStringer stringer)throws JSONException
	{
		if(obj==null){
			stringer.value(obj);
		}else if(obj instanceof Map){
			encodeMap((Map)obj, stringer);
		}else if(obj instanceof List){
			encodeArray((List)obj, stringer);
		}else if(obj.getClass().isArray()){
			encodeArray(obj, stringer);
		}else{
			stringer.value(obj);
		}
	}
	@SuppressWarnings("unchecked")
	private  void encodeMap(Map map,JSONStringer stringer) throws JSONException
	{
		Collection collection= map.keySet();
		Iterator iterator= collection.iterator();
		stringer.object();
		String key ;
		Object value;
		while (iterator.hasNext()) {
			key = iterator.next().toString();
			stringer.key(key);
			value = map.get(key);
			encode(value, stringer);
		}
		stringer.endObject();
	}
	private  void encodeArray(Object array,JSONStringer stringer)throws JSONException
	{
		if(array==null)
			return;
		stringer.array();
			for (int i = 0; i < Array.getLength(array); i++) {
				this.encode(Array.get(array, i), stringer);	
			}
		stringer.endArray();
	}
	@SuppressWarnings("unchecked")
	private  void encodeArray(List array,JSONStringer stringer)throws JSONException
	{
		if(array==null)
			return;
		stringer.array();
		for (int i = 0; i < array.size(); i++) {
			this.encode(array.get(i), stringer);
		}
		stringer.endArray();
	}
	
	private  Object decode(Object obj) throws JSONException
	{
		if(obj instanceof JSONObject)
			return decodeJSONObject((JSONObject)obj);
		else if(obj instanceof JSONArray) 
			return decodeJSONArray((JSONArray)obj);
		else if(obj.equals(JSONObject.NULL))
			return null;
		else
			return obj;
	}
	@SuppressWarnings({ "unchecked"})
	private HashMap decodeJSONObject(JSONObject obj) throws JSONException{
		HashMap map = new HashMap();
		Iterator<String> iterator= obj.keys();
		String key;
		Object value;
		while (iterator.hasNext()) {
			key = iterator.next();
			value = this.decode(obj.get(key));
			map.put(key,value);
		}
		return map;
	}
	@SuppressWarnings("unchecked")
	private List decodeJSONArray(JSONArray array) throws JSONException{
		List list = new ArrayList();
		Object value;
		for (int i = 0; i < array.length(); i++) {
			value = this.decode(array.get(i));
			list.add(value);
		}
		return list;
	}
	
	
//	public static void main(String[] args) throws Exception {
//		Map<String, Object> map = new Hashtable<String, Object>();
//		map.put("1", 1);
//		map.put("2", "2");
//		map.put("3", true);
//		map.put("4", 1.21f);
//		Map<String, Object> submap = new Hashtable<String, Object>();
//		submap.put("submap1", 1);
//		submap.put("submap2", "2");
//		submap.put("submap3", true);
//		submap.put("submap4", 1.21f);
//		//map.put("subMap", submap);
//		List<Object> list = new ArrayList<Object>();
//		list.add("list1");
//		list.add("list2");
//		list.add("list3");
//		list.add("list4");
//		map.put("list", list);
//		List<String> list2 = new ArrayList<String>();
//		list2.add("list21");
//		list2.add("list22");
//		list2.add("list23");
//		list.add(list2);
//		String[] str = new String[]{"str1","str2","str3"};
//		List<Map> list3 = new ArrayList<Map>();
//		list3.add(map);
//		map.put("array", str);
//		//System.out.println(array.getClass());
//		String json = JSONUtil.encode(map);
//		System.out.println("json="+json);
//	    long st = System.currentTimeMillis();
//		Hashtable hashtable = (Hashtable) JSONUtil.decode(json);
//		System.out.println(System.currentTimeMillis()-st);
//		Enumeration en = hashtable.keys();
//		while (en.hasMoreElements()) {
//			String key = (String) en.nextElement();
//			Object value = hashtable.get(key);
//			System.out.println(key+"="+value+"  ----type="+value.getClass());
//		}
//	}
}
