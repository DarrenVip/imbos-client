package com.imbos.chat.sync;

/**
 * 同步处理的责任链
 * @author xianze
 *
 */
public abstract class SyncHandler {
	/**
     * 持有后继的责任对象
     */
    protected SyncHandler successor;
   
    public abstract boolean handle(SyncTask task);
    /**
     * 取值方法
     */
    public SyncHandler getSuccessor() {
        return successor;
    }
    /**
     * 赋值方法，设置后继的责任对象
     */
    public void setSuccessor(SyncHandler successor) {
        this.successor = successor;
    }
}
