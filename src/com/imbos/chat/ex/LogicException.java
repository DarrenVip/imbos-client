package com.imbos.chat.ex;
/**
 * @author wanxianze@gmail.com 2012-6-1
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class LogicException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public LogicException() {
		
	}
	public LogicException(String detailMessage,Throwable throwable) {
		super(detailMessage,throwable);
	}
}
