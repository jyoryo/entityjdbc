package com.jyoryo.entityjdbc.common.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @auther: jyoryo
 * @Date: 2019.4.1 23:39
 */
public class CollectionUtil {
	
	/**
	 * 集合是否为空
	 *
	 * @param collection 集合
	 * @return 是否为空
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return null == collection || collection.isEmpty();
	}
	
	/**
	 * Map是否为空
	 *
	 * @param map 集合
	 * @return 是否为空
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return null == map || map.isEmpty();
	}
	
	/**
	 * Iterable是否为空
	 *
	 * @param iterable Iterable对象
	 * @return 是否为空
	 */
	public static boolean isEmpty(Iterable<?> iterable) {
		return null == iterable || iterable.iterator().hasNext();
	}
	
	/**
	 * Iterator是否为空
	 *
	 * @param Iterator Iterator对象
	 * @return 是否为空
	 * @see Iterators#isEmpty(Iterator)
	 */
	public static boolean isEmpty(Iterator<?> Iterator) {
		return Iterators.isEmpty(Iterator);
	}
	
	/**
	 * Enumeration是否为空
	 *
	 * @param enumeration {@link Enumeration}
	 * @return 是否为空
	 */
	public static boolean isEmpty(Enumeration<?> enumeration) {
		return null == enumeration || false == enumeration.hasMoreElements();
	}
	
	/**
	 * 如果list为null，返回空的非null list；否则返回原list
	 * @param list
	 * @return
	 */
	public static <E> List<E> emptyListIfNull(List<E> list) {
	    return (null == list) ? Collections.emptyList() : list;
	} 
}
