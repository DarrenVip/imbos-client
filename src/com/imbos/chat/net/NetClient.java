package com.imbos.chat.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.util.Log;

import com.imbos.chat.ex.NetException;

public class NetClient {
	private String TAG= getClass().getSimpleName(); 
	public static final String CHARSET="UTF-8";
	public String url="http://localhost:8080/api.do";
	public static HttpClient client;
	private String token;
	private Map<String,?> header;
	private OnNetListener onNetListener;
	
	public static String createUrl(String host,String port,String dir){
		return "http://"+host+":"+port+dir;
	}
	
	public NetClient(String host,String port,String dir) {
		url = createUrl(host, port, dir);
		Log.d(TAG, url);
		synchronized (url) {
			if(client==null){
				client = createHttpClient();
			}
		}
	}
	public NetClient(String url){
		this.url  = url;
		synchronized (url) {
			if(client==null){
				client = createHttpClient();
			}
		}
	}
	
	public NetClient setOnNetListener(OnNetListener onNetListener) {
		this.onNetListener = onNetListener;
		return this;
	}
	
	public String request(String content) {
		StringBuffer result = new StringBuffer();
		
		HttpPost post = new HttpPost(url.toString());
		post.addHeader("token", token);
		final String requestStr= content;
		ContentProducer cp = new ContentProducer() {
			public void writeTo(OutputStream outstream) throws IOException {
				Writer writer = new OutputStreamWriter(outstream, "UTF-8");
				writer.write(requestStr);
				writer.flush();
				writer.close();
			}
		};

		
		
		try {
			Log.d(TAG,"request:"+content);
			HttpEntity query = new EntityTemplate(cp);
			if(content!=null)
				post.setEntity(query);
			/** 发出POST数据并获取返回数据 */
		
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(entity.getContent()));
			
			String line = null;
			while ((line = buffReader.readLine()) != null)
				result.append(line);
			Log.d(TAG,"response:"+result);
		}catch (Exception ex) {
			ex.printStackTrace();
			throw new NetException(ex);
		} finally {
			post.abort();
		}
		return result.toString();
	}
	public String request(File file) {
		StringBuffer result = new StringBuffer();
		
		HttpPost post = new HttpPost(url.toString());
		post.addHeader("token", token);
		final File sendFile= file;
		ContentProducer cp = new ContentProducer() {
			public void writeTo(OutputStream outstream) throws IOException {
				long max = sendFile.length();
				long cur = 0; 
				FileInputStream fis = new FileInputStream(sendFile);
				 byte[] buffer = new byte[1024];
		    	 int readLen=0;
		    	 
		    	 while ((readLen=fis.read(buffer))!=-1){
		    		 outstream.write(buffer,0,readLen);
		    		 cur+=readLen;
		    		 if(onNetListener!=null)
		    			 onNetListener.onProgress((int)cur,(int) max,"");
		    		 
				 }
				outstream.flush();
				outstream.close();
			}
		};

		
		
		try {
			HttpEntity query = new EntityTemplate(cp);
			post.setEntity(query);
			post.addHeader("fileName", file.getName());
			if(header!=null){
				Set<String> keySet = header.keySet();
				for (String key : keySet) {
					String value = header.get(key)==null?
							null:header.get(key).toString();
					post.addHeader(key,value);
				}
				
				
			}
			/** 发出POST数据并获取返回数据 */
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(entity.getContent()));
			
			String line = null;
			while ((line = buffReader.readLine()) != null)
				result.append(line);
			Log.d(TAG,"response:"+result);
		}catch (Exception ex) {
			ex.printStackTrace();
			throw new NetException(ex);
		} finally {
			post.abort();
		}
		return result.toString();
	}
	
	public File download(String dir) {
		File file =null;
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
		
			String fileName = response.getFirstHeader("Content-Disposition").getValue();
			Log.d(TAG, fileName);
			fileName = fileName.split(";")[1].split("=")[1];
			Log.d(TAG, fileName);
			
			InputStream inputStream =entity.getContent();
			file = new File(dir,fileName);
			FileOutputStream fos = new FileOutputStream(file);
			
			long max = entity.getContentLength();
			long cur = 0; 
			
			byte[] buffer = new byte[1024];
			int readLen=0;
			
			while ((readLen = inputStream.read(buffer)) != -1){
				fos.write(buffer,0,readLen);
				cur+=readLen;
				if(onNetListener!=null)
	    			 onNetListener.onProgress((int)cur,(int) max,"");
			}
			fos.flush();
		}catch (Exception ex) {
			ex.printStackTrace();
			file = null;
			throw new NetException(ex);
		} finally {
			get.abort();
		}
		return file;
	}
	public NetClient setHeader(Map<String,?> header) {
		this.header = header;
		return this;
	}
	public NetClient setToken(String token) {
		this.token = token;
		return this;
	}
	
	
	
	
	
	private HttpClient createHttpClient(){
		HttpParams params = new BasicHttpParams();
        // 设置一些基本参数
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params,
                CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpProtocolParams
                .setUserAgent(
                        params,
                        "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                                + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
        // 超时设置
        /* 从连接池中取连接的超时时间 */
        //ConnManagerParams.setTimeout(params, 6000);
        /* 连接超时 */
        HttpConnectionParams.setConnectionTimeout(params,30*1000);
        /* 请求超时 */
        HttpConnectionParams.setSoTimeout(params,30*1000);
        
        // 设置我们的HttpClient支持HTTP和HTTPS两种模式
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory
                .getSocketFactory(), 443));

        // 使用线程安全的连接管理来创建HttpClient
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                params, schReg);
        return  new DefaultHttpClient(conMgr, params);
	}
	
	
	public interface OnNetListener{
		void onProgress(int cur,int max,String msg);
	}
}
