package com.eshop.event;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.eshop.event.listenner.EventListenner;

/**
 * 事件处理辅助类
 * @author Yang
 * 使用方法如下：
 * 
 * 首先定义事件ID：
 * {@code
 * const int EVENT_LOGIN = 1;
 * const int EVENT_LOGOUT = 2;
 * }
 * 然后编写事件处理的代码，此处假设有一个用来对用户的登录、退出进行统计的模块LoginStatistics
 * {@code
 * class LoginStatistics implements EventListenner {
 *     public void Process(int event, Object p1, Object p2) {
 *         if (event == EVENT_LOGIN) {
 *             // 当登录事件触发时的处理逻辑
 *             processLogin((User)p1);
 *         } else if (event == EVENT_LOGOUT) {
 *             // 当退出事件触发时的处理逻辑
 *             processLogout((User)p);
 *         }
 *     }
 *     private void processLogin(User user) {
 *         ......
 *     }
 *     private void processLogout(User user) {
 *         ......
 *     }
 * }
 * }
 * 在控制器基类中注册事件和监听器
 * {@code
 * class BaseController {
 *     protected EventManager m_events;
 *     private LoginStatistics m_ls;
 *     
 *     public BaseController() {
 *         m_ls = new LoginStatistics();
 *         m_events = new EventHelper();
 *         m_events.addListenner(EVENT_LOGIN, m_ls);
 *         m_events.addListenner(EVENT_LOGOUT, m_ls);
 *     }
 * }
 * }
 * 在控制器中触发事件，相应的监听器就会被调用，这里假设缓存m_cache里的"user"存储了当前用户对象
 * {@code
 * class Controller : BaseController {
 *     public void Login() {
 *         m_events.RaiseEvent(EVENT_LOGIN, m_cache.get("user"));
 *     }
 *     public void Logout() {
 *         m_events.RaiseEvent(EVENT_LOGOUT, m_cahe.get("user"));
 *     }
 * }
 * }
 * 
 */
public class EventManager {

	/**
	 * 事件监听器集合，事件标志为一个整数
	 * @see EventListenner
	 */
	private HashMap<EventEnum, ArrayList<EventListenner>> m_listenners;// = new HashMap<EventEnum, ArrayList<EventListenner>>();
	
	/**
	 * 构造方法
	 */
	public EventManager() {
		m_listenners = new HashMap<EventEnum, ArrayList<EventListenner>>();
	}
	
	/**
	 * 为指定事件添加一个监听器
	 * @param event 事件标志，自定义的事件ID，用来区分事件
	 * @param listenner 事件监听器
	 * @see EventListenner
	 */
	public void AddListenner(EventEnum event, EventListenner listenner) {
		if (!m_listenners.containsKey(event)) {
			m_listenners.put(event, new ArrayList<EventListenner>());
		}
		ArrayList<EventListenner> ls = m_listenners.get(event);
		ls.add(listenner);
	}
	
	/**
	 * 移除指定事件的监听器
	 * @param event 事件标志，自定义的事件ID，用来区分事件
	 * @param listenner 事件监听器
	 * @see EventListenner
	 */
	public void RemoveListenner(int event, EventListenner listenner) {
		if (m_listenners.containsKey(event)) {
			ArrayList<EventListenner> ls = m_listenners.get(event);
			if (ls.contains(listenner)) {
				ls.remove(listenner);
			}
		}
	}
	
	/**
	 * 移除指定事件的所有监听器
	 * @param event 事件标志，自定义的事件ID，用来区分事件
	 */
	public void RemoveListenners(int event) {
		if (m_listenners.containsKey(event)) {
			m_listenners.remove(event);
		}
	}
	
	/**
	 * 移除所有监听器
	 */
	public void Clear() {
		m_listenners.clear();
	}
	
	/**
	 * 触发指定事件
	 * @param event 要触发的事件ID
	 * @param p1 第一个事件参数
	 * @param p2 第二个事件参数
	 * @see EventListenner
	 */
	public void RaiseEvent(EventEnum event, Object p1) {
		if (m_listenners.containsKey(event)) {
			ArrayList<EventListenner> ls = m_listenners.get(event);
			for (int i = 0; i < ls.size(); i++) {
				ls.get(i).Process(event, p1);
			}
		}
	}
	
	/**
	 * 触发指定事件
	 * @param event 要触发的事件ID，两个事件参数为null
	 * @see EventListenner
	 */
	public void RaiseEvent(EventEnum event) {
		RaiseEvent(event, null);
	}
}