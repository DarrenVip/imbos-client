package com.imbos.chat.ex;
/**
 * @author wanxianze@gmail.com 2012-6-1
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class NetException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public NetException(Throwable throwable) {
		super(throwable);
	}
	public NetException(String detailMessage,Throwable throwable) {
		super(detailMessage,throwable);
	}
}
