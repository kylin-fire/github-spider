package com.only.github.common.model;

import com.only.github.common.helper.JsonHelper;
import com.only.github.common.object.Status;

/**
 * 结果对象
 *
 * @param <T> 模板
 * @author only 2013-7-2
 */
public class Result<T> implements ResultSupport<T> {

	/** Define long serialVersionUID */
	private static final long serialVersionUID = 3928402875296079225L;
	/** 状态 */
	protected boolean success = true;
	/** 状态码 例如:0-正常；-1-缺少参数；-3-接口异常 */
	protected int status;
	/** 错误码 */
	protected String errorCode;
	/** 状态消息 */
	protected String message;
	/** 淘宝/tmall商品或店铺的返利信息;外网B2C商品或活动的返利信息 */
	private T module;

	/** 构造函数 */
	public Result() {
		super();
	}

	/**
	 * 设置状态码
	 *
	 * @param status 状态码
	 */
	public void setStatus(Status status) {
		this.setStatus(status.isSuccess(), status.getStatus(), status.getErrorCode(), status.getMessage());
	}

	/**
	 * 设置状态码
	 *
	 * @param status 状态码
	 */
	public void setStatus(boolean success, int status, String errorCode, String message) {
		this.setSuccess(success);
		this.setStatus(status);
		this.setErrorCode(errorCode);
		this.setMessage(message);
	}

	/**
	 * 设置状态码
	 *
	 * @param status   状态码
	 * @param messages 替换文本
	 */
	public void setStatus(Status status, String... messages) {
		this.setStatus(status.isSuccess(), status.getStatus(), status.getErrorCode(), status.getMessage(), messages);
	}

	/**
	 * 设置状态码
	 *
	 * @param status   状态码
	 * @param messages 替换文本
	 */
	public void setStatus(boolean success, int status, String errorCode, String message, String... messages) {
		this.setSuccess(success);
		this.setStatus(status);
		this.setErrorCode(errorCode);
		if (message != null && message.contains("%s")) {
			if (messages != null) {
				message = String.format(message, (Object[]) messages);
			}
			// 无替换文本
			else {
				message = message.replaceAll("%s", "");
			}
		}
		this.setMessage(message);
	}

	/**
	 * 是否成功
	 *
	 * @return 成功返回true 反则返回false
	 */
	@Override
	public boolean isSuccess() {
		return success;
	}

	/** @return the status */
	@Override
	public int getStatus() {
		return status;
	}

	/** @param status the status to set */
	@Override
	public void setStatus(int status) {
		this.status = status;
	}

	/** @return the errorCode */
	@Override
	public String getErrorCode() {
		return errorCode;
	}

	/** @param errorCode the errorCode to set */
	@Override
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/** @return the message */
	@Override
	public String getMessage() {
		return message;
	}

	/** @param message the message to set */
	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	/** @return the module */
	@Override
	public T getModule() {
		return module;
	}

	/** @param module the module to set */
	@Override
	public void setModule(T module) {
		this.module = module;
	}

	/** @param success the success to set */
	@Override
	public void setSuccess(boolean success) {
		this.success = success;
	}

	@Override
	public String toString() {
		return JsonHelper.toJson(this);
	}

}