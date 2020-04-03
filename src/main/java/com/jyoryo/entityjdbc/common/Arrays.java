package com.jyoryo.entityjdbc.common;

import java.lang.reflect.Array;
import java.util.Iterator;

import org.apache.commons.lang3.ArrayUtils;

import com.jyoryo.entityjdbc.common.collection.Iterators;
import com.jyoryo.entityjdbc.common.exception.UtilException;

/**
 * Array数组工具类
 * <li>基于Apache ArrayUtils</li>
 * @author jyoryo
 *
 */
public class Arrays extends ArrayUtils {

	/**
	 * 对象是否为数组对象
	 * 
	 * @param obj 对象
	 * @return 是否为数组对象，如果为{@code null} 返回false
	 */
	public static boolean isArray(Object obj) {
		if(null == obj) {
			return false;
		}
		return obj.getClass().isArray();
	}

	/**
	 * 是否包含{@code null}元素
	 * 
	 * @param array   被检查的数组
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean hasNull(T... array) {
		if(isNotEmpty(array)) {
			for(T element : array) {
				if(null == element) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 将一个Object转换成Array
	 * <li>传入参数object需为数组类型</li>
	 * @param object
	 * @return
	 */
	public static Object[] objectToArray(Object object) {
		if(null == object) {
			return new Object[0];
		}
		if(!isArray(object)) {
			throw new IllegalArgumentException("传入参数必须是数组类型！");
		}
		int length = Array.getLength(object);
		if(0 == length) {
			return new Object[0];
		}
		Object[] array = (Object[])Array.newInstance(Array.get(object, 0).getClass(), length);
		for(int i = 0; i < length; i ++) {
			array[i] = Array.get(object, i);
		}
		return array;
	}
	
	/**
	 * 将原始类型数组包装为包装类型
	 *
	 * @param values 原始类型数组
	 * @return 包装类型数组
	 */
	public static Integer[] wrap(int... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new Integer[0];
		}
		
		final Integer[] array = new Integer[length];
		for (int i = 0; i < length; i++) {
			array[i] = Integer.valueOf(values[i]);
		}
		return array;
	}
	
	/**
	 * 包装类数组转为原始类型数组
	 *
	 * @param values 包装类型数组
	 * @return 原始类型数组
	 */
	public static int[] unWrap(Integer... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new int[0];
		}
		
		final int[] array = new int[length];
		for (int i = 0; i < length; i++) {
			array[i] = values[i].intValue();
		}
		return array;
	}
	
	/**
	 * 将原始类型数组包装为包装类型
	 *
	 * @param values 原始类型数组
	 * @return 包装类型数组
	 */
	public static Long[] wrap(long... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new Long[0];
		}
		
		final Long[] array = new Long[length];
		for (int i = 0; i < length; i++) {
			array[i] = Long.valueOf(values[i]);
		}
		return array;
	}
	
	/**
	 * 包装类数组转为原始类型数组
	 *
	 * @param values 包装类型数组
	 * @return 原始类型数组
	 */
	public static long[] unWrap(Long... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new long[0];
		}
		
		final long[] array = new long[length];
		for (int i = 0; i < length; i++) {
			array[i] = values[i].longValue();
		}
		return array;
	}
	
	/**
	 * 将原始类型数组包装为包装类型
	 *
	 * @param values 原始类型数组
	 * @return 包装类型数组
	 */
	public static Character[] wrap(char... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new Character[0];
		}
		
		final Character[] array = new Character[length];
		for (int i = 0; i < length; i++) {
			array[i] = Character.valueOf(values[i]);
		}
		return array;
	}
	
	/**
	 * 包装类数组转为原始类型数组
	 *
	 * @param values 包装类型数组
	 * @return 原始类型数组
	 */
	public static char[] unWrap(Character... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new char[0];
		}
		
		char[] array = new char[length];
		for (int i = 0; i < length; i++) {
			array[i] = values[i].charValue();
		}
		return array;
	}
	
	/**
	 * 将原始类型数组包装为包装类型
	 *
	 * @param values 原始类型数组
	 * @return 包装类型数组
	 */
	public static Byte[] wrap(byte... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new Byte[0];
		}
		
		final Byte[] array = new Byte[length];
		for (int i = 0; i < length; i++) {
			array[i] = Byte.valueOf(values[i]);
		}
		return array;
	}
	
	/**
	 * 包装类数组转为原始类型数组
	 *
	 * @param values 包装类型数组
	 * @return 原始类型数组
	 */
	public static byte[] unWrap(Byte... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new byte[0];
		}
		
		final byte[] array = new byte[length];
		for (int i = 0; i < length; i++) {
			array[i] = values[i].byteValue();
		}
		return array;
	}
	
	/**
	 * 将原始类型数组包装为包装类型
	 *
	 * @param values 原始类型数组
	 * @return 包装类型数组
	 */
	public static Short[] wrap(short... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new Short[0];
		}
		
		final Short[] array = new Short[length];
		for (int i = 0; i < length; i++) {
			array[i] = Short.valueOf(values[i]);
		}
		return array;
	}
	
	/**
	 * 包装类数组转为原始类型数组
	 *
	 * @param values 包装类型数组
	 * @return 原始类型数组
	 */
	public static short[] unWrap(Short... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new short[0];
		}
		
		final short[] array = new short[length];
		for (int i = 0; i < length; i++) {
			array[i] = values[i].shortValue();
		}
		return array;
	}
	
	/**
	 * 将原始类型数组包装为包装类型
	 *
	 * @param values 原始类型数组
	 * @return 包装类型数组
	 */
	public static Float[] wrap(float... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new Float[0];
		}
		
		final Float[] array = new Float[length];
		for (int i = 0; i < length; i++) {
			array[i] = Float.valueOf(values[i]);
		}
		return array;
	}
	
	/**
	 * 包装类数组转为原始类型数组
	 *
	 * @param values 包装类型数组
	 * @return 原始类型数组
	 */
	public static float[] unWrap(Float... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new float[0];
		}
		
		final float[] array = new float[length];
		for (int i = 0; i < length; i++) {
			array[i] = values[i].floatValue();
		}
		return array;
	}
	
	/**
	 * 将原始类型数组包装为包装类型
	 *
	 * @param values 原始类型数组
	 * @return 包装类型数组
	 */
	public static Double[] wrap(double... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new Double[0];
		}
		
		final Double[] array = new Double[length];
		for (int i = 0; i < length; i++) {
			array[i] = Double.valueOf(values[i]);
		}
		return array;
	}
	
	/**
	 * 包装类数组转为原始类型数组
	 *
	 * @param values 包装类型数组
	 * @return 原始类型数组
	 */
	public static double[] unWrap(Double... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new double[0];
		}
		
		final double[] array = new double[length];
		for (int i = 0; i < length; i++) {
			array[i] = values[i].doubleValue();
		}
		return array;
	}
	
	/**
	 * 将原始类型数组包装为包装类型
	 *
	 * @param values 原始类型数组
	 * @return 包装类型数组
	 */
	public static Boolean[] wrap(boolean... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new Boolean[0];
		}
		
		final Boolean[] array = new Boolean[length];
		for (int i = 0; i < length; i++) {
			array[i] = Boolean.valueOf(values[i]);
		}
		return array;
	}
	
	/**
	 * 包装类数组转为原始类型数组
	 *
	 * @param values 包装类型数组
	 * @return 原始类型数组
	 */
	public static boolean[] unWrap(Boolean... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new boolean[0];
		}
		
		final boolean[] array = new boolean[length];
		for (int i = 0; i < length; i++) {
			array[i] = values[i].booleanValue();
		}
		return array;
	}
	
	/**
	 * 包装数组对象
	 *
	 * @param obj 对象，可以是对象数组或者基本类型数组
	 * @return 包装类型数组或对象数组
	 * @throws UtilException 对象为非数组
	 */
	public static Object[] wrap(Object obj) {
		if (null == obj) {
			return null;
		}
		if (isArray(obj)) {
			try {
				return (Object[]) obj;
			} catch (Exception e) {
				final String className = obj.getClass().getComponentType().getName();
				switch (className) {
					case "long":
						return wrap((long[]) obj);
					case "int":
						return wrap((int[]) obj);
					case "short":
						return wrap((short[]) obj);
					case "char":
						return wrap((char[]) obj);
					case "byte":
						return wrap((byte[]) obj);
					case "boolean":
						return wrap((boolean[]) obj);
					case "float":
						return wrap((float[]) obj);
					case "double":
						return wrap((double[]) obj);
					default:
						throw new UtilException(e);
				}
			}
		}
		throw new UtilException(Strings.format("[{}] is not Array!", obj.getClass()));
	}
	
	/**
	 * 以 conjunction 为分隔符将数组转换为字符串
	 *
	 * @param <T> 被处理的集合
	 * @param array 数组
	 * @param conjunction 分隔符
	 * @return 连接后的字符串
	 */
	public static <T> String join(T[] array, CharSequence conjunction) {
		return join(array, conjunction, null, null);
	}
	
	/**
	 * 以 conjunction 为分隔符将数组转换为字符串
	 *
	 * @param <T> 被处理的集合
	 * @param array 数组
	 * @param conjunction 分隔符
	 * @param prefix 每个元素添加的前缀，null表示不添加
	 * @param suffix 每个元素添加的后缀，null表示不添加
	 * @return 连接后的字符串
	 */
	public static <T> String join(T[] array, CharSequence conjunction, String prefix, String suffix) {
		if (null == array) {
			return null;
		}
		
		final StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (T item : array) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(conjunction);
			}
			if (Arrays.isArray(item)) {
				sb.append(join(Arrays.wrap(item), conjunction, prefix, suffix));
			} else if (item instanceof Iterable<?>) {
				sb.append(Iterators.join((Iterable<?>) item, conjunction, prefix, suffix));
			} else if (item instanceof Iterator<?>) {
				sb.append(Iterators.join((Iterator<?>) item, conjunction, prefix, suffix));
			} else {
				sb.append(Strings.wrap(Strings.toString(item), prefix, suffix));
			}
		}
		return sb.toString();
	}
	
	/**
	 * 以 conjunction 为分隔符将数组转换为字符串
	 *
	 * @param array 数组
	 * @param conjunction 分隔符
	 * @return 连接后的字符串
	 */
	public static String join(long[] array, CharSequence conjunction) {
		if (null == array) {
			return null;
		}
		
		final StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (long item : array) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(conjunction);
			}
			sb.append(item);
		}
		return sb.toString();
	}
	
	/**
	 * 以 conjunction 为分隔符将数组转换为字符串
	 *
	 * @param array 数组
	 * @param conjunction 分隔符
	 * @return 连接后的字符串
	 */
	public static String join(int[] array, CharSequence conjunction) {
		if (null == array) {
			return null;
		}
		
		final StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (int item : array) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(conjunction);
			}
			sb.append(item);
		}
		return sb.toString();
	}
	
	/**
	 * 以 conjunction 为分隔符将数组转换为字符串
	 *
	 * @param array 数组
	 * @param conjunction 分隔符
	 * @return 连接后的字符串
	 */
	public static String join(short[] array, CharSequence conjunction) {
		if (null == array) {
			return null;
		}
		
		final StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (short item : array) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(conjunction);
			}
			sb.append(item);
		}
		return sb.toString();
	}
	
	/**
	 * 以 conjunction 为分隔符将数组转换为字符串
	 *
	 * @param array 数组
	 * @param conjunction 分隔符
	 * @return 连接后的字符串
	 */
	public static String join(char[] array, CharSequence conjunction) {
		if (null == array) {
			return null;
		}
		
		final StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (char item : array) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(conjunction);
			}
			sb.append(item);
		}
		return sb.toString();
	}
	
	/**
	 * 以 conjunction 为分隔符将数组转换为字符串
	 *
	 * @param array 数组
	 * @param conjunction 分隔符
	 * @return 连接后的字符串
	 */
	public static String join(byte[] array, CharSequence conjunction) {
		if (null == array) {
			return null;
		}
		
		final StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (byte item : array) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(conjunction);
			}
			sb.append(item);
		}
		return sb.toString();
	}
	
	/**
	 * 以 conjunction 为分隔符将数组转换为字符串
	 *
	 * @param array 数组
	 * @param conjunction 分隔符
	 * @return 连接后的字符串
	 */
	public static String join(boolean[] array, CharSequence conjunction) {
		if (null == array) {
			return null;
		}
		
		final StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (boolean item : array) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(conjunction);
			}
			sb.append(item);
		}
		return sb.toString();
	}
	
	/**
	 * 以 conjunction 为分隔符将数组转换为字符串
	 *
	 * @param array 数组
	 * @param conjunction 分隔符
	 * @return 连接后的字符串
	 */
	public static String join(float[] array, CharSequence conjunction) {
		if (null == array) {
			return null;
		}
		
		final StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (float item : array) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(conjunction);
			}
			sb.append(item);
		}
		return sb.toString();
	}
	
	/**
	 * 以 conjunction 为分隔符将数组转换为字符串
	 *
	 * @param array 数组
	 * @param conjunction 分隔符
	 * @return 连接后的字符串
	 */
	public static String join(double[] array, CharSequence conjunction) {
		if (null == array) {
			return null;
		}
		
		final StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (double item : array) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(conjunction);
			}
			sb.append(item);
		}
		return sb.toString();
	}
	
	/**
	 * 以 conjunction 为分隔符将数组转换为字符串
	 *
	 * @param array 数组
	 * @param conjunction 分隔符
	 * @return 连接后的字符串
	 */
	public static String join(Object array, CharSequence conjunction) {
		if (isArray(array)) {
			final Class<?> componentType = array.getClass().getComponentType();
			if (componentType.isPrimitive()) {
				final String componentTypeName = componentType.getName();
				switch (componentTypeName) {
					case "long":
						return join((long[]) array, conjunction);
					case "int":
						return join((int[]) array, conjunction);
					case "short":
						return join((short[]) array, conjunction);
					case "char":
						return join((char[]) array, conjunction);
					case "byte":
						return join((byte[]) array, conjunction);
					case "boolean":
						return join((boolean[]) array, conjunction);
					case "float":
						return join((float[]) array, conjunction);
					case "double":
						return join((double[]) array, conjunction);
					default:
						throw new UtilException("Unknown primitive type: [{}]", componentTypeName);
				}
			} else {
				return join((Object[]) array, conjunction);
			}
		}
		throw new UtilException(Strings.format("[{}] is not a Array!", array.getClass()));
	}
	
	/**
	 * 按指定分隔符将字符串分割为int数组。如果存在转换失败，则返回空数组。
	 * @param str
	 * @param separatorChars
	 * @return
	 */
	public static int [] toIntArray(String str, String separatorChars) {
	    String [] strArray = Strings.split(str, separatorChars);
	    if(null == strArray) {
	        return EMPTY_INT_ARRAY;
	    }
	    int length = strArray.length;
	    int[] array = new int[length];
	    try {
	        for(int i = 0; i < length; i ++) {
	            array[i] = Integer.parseInt(strArray[i]);
	        }
	    } catch (NumberFormatException e) {
	        return EMPTY_INT_ARRAY;
	    }
	    return array;
	}
	
	/**
     * 按指定分隔符将字符串分割为long数组。如果存在转换失败，则返回空数组。
     * @param str
     * @param separatorChars
     * @return
     */
    public static long [] toLongArray(String str, String separatorChars) {
        String [] strArray = Strings.split(str, separatorChars);
        if(null == strArray) {
            return EMPTY_LONG_ARRAY;
        }
        int length = strArray.length;
        long[] array = new long[length];
        try {
            for(int i = 0; i < length; i ++) {
                array[i] = Long.parseLong(strArray[i]);
            }
        } catch (NumberFormatException e) {
            return EMPTY_LONG_ARRAY;
        }
        return array;
    }
    
    /**
     * 按指定分隔符将字符串分割为double数组。如果存在转换失败，则返回空数组。
     * @param str
     * @param separatorChars
     * @return
     */
    public static double [] toDoubleArray(String str, String separatorChars) {
        String [] strArray = Strings.split(str, separatorChars);
        if(null == strArray) {
            return EMPTY_DOUBLE_ARRAY;
        }
        int length = strArray.length;
        double[] array = new double[length];
        try {
            for(int i = 0; i < length; i ++) {
                array[i] = Double.parseDouble(strArray[i]);
            }
        } catch (NumberFormatException e) {
            return EMPTY_DOUBLE_ARRAY;
        }
        return array;
    }
}
