package com.eshop.event.listenner;

import com.eshop.event.EventEnum;

/**
 * 事件监听器接口
 * @author Yang
 * 配合EventManager进行使用
 * @see EventManager
 */
public interface EventListenner {
	/**
	 * 事件处理方法
	 * @param event 当前要处理的事件ID（用户自定义）
	 * @param param1 第一个事件参数
	 * @param param2 第二个事件参数
	 */
	public void Process(EventEnum event, Object param1);
}