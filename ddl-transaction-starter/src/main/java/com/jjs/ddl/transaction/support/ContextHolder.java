package com.jjs.ddl.transaction.support;

/**
 * 上下文
 * @author zhangrenhua
 * @date 2022/1/14
 */
public final class ContextHolder {

	/**
	 * 事务ID标识
	 */
	private static final ThreadLocal<String> TX_ID = new ThreadLocal<>();

	private ContextHolder() {
	}

	public static void putTxId(String key) {
		TX_ID.set(key);
	}

	public static String getTxId() {
		return TX_ID.get();
	}

	public static void clearTxId() {
		TX_ID.remove();
	}

}