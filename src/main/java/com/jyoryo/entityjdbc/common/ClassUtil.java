package com.jyoryo.entityjdbc.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ClassUtils;

import com.jyoryo.entityjdbc.common.exception.ClassNotFoundRuntimeException;
import com.jyoryo.entityjdbc.common.log.Logs;

/**
 * Class 工具类
 * <li>基于Apache ClassUtls</li>
 * @auther: jyoryo
 * @Date: 2019.4.2 01:14
 */
public class ClassUtil extends ClassUtils {
	
	
	// 参考了 org.springframework.util.ClassUtil
	
	/**
	 * Map with primitive wrapper type as key and corresponding primitive
	 * type as value, for example: Integer.class -> int.class.
	 */
	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<>(8);
	
	/**
	 * Map with primitive type as key and corresponding wrapper
	 * type as value, for example: int.class -> Integer.class.
	 */
	private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap<>(8);
	
	/**
	 * Map with primitive type name as key and corresponding primitive
	 * type as value, for example: "int" -> "int.class".
	 */
	private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<>(32);
	
	/**
	 * Map with common "java.lang" class name as key and corresponding Class as value.
	 * Primarily for efficient deserialization of remote invocations.
	 */
	private static final Map<String, Class<?>> commonClassCache = new HashMap<>(32);
	
	
	static {
		primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
		primitiveWrapperTypeMap.put(Byte.class, byte.class);
		primitiveWrapperTypeMap.put(Character.class, char.class);
		primitiveWrapperTypeMap.put(Double.class, double.class);
		primitiveWrapperTypeMap.put(Float.class, float.class);
		primitiveWrapperTypeMap.put(Integer.class, int.class);
		primitiveWrapperTypeMap.put(Long.class, long.class);
		primitiveWrapperTypeMap.put(Short.class, short.class);
		
		for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
			primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
			registerCommonClasses(entry.getKey());
		}
		
		Set<Class<?>> primitiveTypes = new HashSet<>(32);
		primitiveTypes.addAll(primitiveWrapperTypeMap.values());
		primitiveTypes.addAll(java.util.Arrays.asList(
			boolean[].class, byte[].class, char[].class, double[].class,
			float[].class, int[].class, long[].class, short[].class));
		primitiveTypes.add(void.class);
		for (Class<?> primitiveType : primitiveTypes) {
			primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
		}
		
		registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class,
			Float[].class, Integer[].class, Long[].class, Short[].class);
		registerCommonClasses(Number.class, Number[].class, String.class, String[].class,
			Object.class, Object[].class, Class.class, Class[].class);
		registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class,
			Error.class, StackTraceElement.class, StackTraceElement[].class);
	}
	
	
	/**
	 * Register the given common classes with the ClassUtil cache.
	 */
	private static void registerCommonClasses(Class<?>... commonClasses) {
		for (Class<?> clazz : commonClasses) {
			commonClassCache.put(clazz.getName(), clazz);
		}
	}
	
	/**
	 * 判断clazz是否是type类型、type的子类或实现了type接口的类。</p>
	 *
	 * 注意：不要使用“类名.class”的方式获取clazz，可以使用“对象.getClass()”，也
	 * 可以使用Class.forName的方式。
	 *
	 * @param clazz 需要验证的类，注意不要使用“类名.class”的方式获取
	 * @param type 验证的类型
	 * @return 如果clazz是type的类型、子类或实现的接口则返回true，否则返回false
	 * @since 2.8.9
	 */
	public static boolean is(Class<?> clazz, Class<?> type) {
		return clazz == null || type == null ? false : type.isAssignableFrom(clazz);
	}
	
	/**
	 * 判断指定的类是否为Collection（或者其子类或者其子接口）。
	 *
	 * @param clazz 需要判断的类
	 * @return 如果clazz为null或非Collection类型则返回false
	 */
	public static boolean isCollection(Class<?> clazz) {
		return is(clazz, Collection.class);
	}
	
	/**
	 * 判断指定的类是否为Map（或者其子类或者其子接口)。
	 *
	 * @param clazz 需要判断的类
	 * @return 如果clazz为null或非Map类型则返回false
	 */
	public static boolean isMap(Class<?> clazz) {
		return is(clazz, Map.class);
	}
	
	/**
	 * Return the default ClassLoader to use: typically the thread context
	 * ClassLoader, if available; the ClassLoader that loaded the ClassUtil
	 * class will be used as fallback.
	 * <p>Call this method if you intend to use the thread context ClassLoader
	 * in a scenario where you absolutely need a non-null ClassLoader reference:
	 * for example, for class path resource loading (but not necessarily for
	 * <code>Class.forName</code>, which accepts a <code>null</code> ClassLoader
	 * reference as well).
	 * @return the default ClassLoader (never <code>null</code>)
	 * @see java.lang.Thread#getContextClassLoader()
	 */
	public static ClassLoader getClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ClassUtil.class.getClassLoader();
		}
		if (cl == null) {
			cl = ClassLoader.getSystemClassLoader();
		}
		return cl;
	}
	
	/**
	 * Override the thread context ClassLoader with the environment's bean ClassLoader
	 * if necessary, i.e. if the bean ClassLoader is not equivalent to the thread
	 * context ClassLoader already.
	 * @param classLoaderToUse the actual ClassLoader to use for the thread context
	 * @return the original thread context ClassLoader, or <code>null</code> if not overridden
	 */
	public static ClassLoader overrideThreadContextClassLoader(ClassLoader classLoaderToUse) {
		Thread currentThread = Thread.currentThread();
		ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
		if (classLoaderToUse != null && !classLoaderToUse.equals(threadContextClassLoader)) {
			currentThread.setContextClassLoader(classLoaderToUse);
			return threadContextClassLoader;
		}
		
		return null;
	}
	
	/**
	 * 获取指定的包名机及其子包下面所有的Class类，该方法不在CLASS PATH下的jar文件中进行查找。</p>
	 *
	 * 注意：该方法不支持直接查找JDK中的包名和类名。
	 *
	 * @param packageName 包名
	 * @return
	 */
	public static Class<?>[] getClasses(String packageName) {
		return getClasses(packageName, false);
	}
	
	/**
	 * 获取指定的包名机及其子包下面所有的Class类，可以设置是否在CLASS PATH下的jar包中查找。</p>
	 *
	 * 注意：该方法不支持直接查找JDK中的包名和类名。
	 *
	 * @param packageName 包名
	 * @param fromJarFiles 是否同时在CLASS PATH下的jar文件中进行查找
	 * @return
	 * @exception ClassNotFoundRuntimeException 当出现系统问题或类名不存在时则抛出该异常
	 */
	public static Class<?>[] getClasses(String packageName, boolean fromJarFiles) {
		String relPath = packageName.replace('.', '/');
		List<Class<?>> classes = new ArrayList<>();
		File dir = null;
		
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if(loader == null) {
				throw new ClassNotFoundRuntimeException("Can't get class loader.");
			}
			URL resource = loader.getResource(relPath);
			if(resource == null) {
				throw new ClassNotFoundRuntimeException(String.format("No resource for %s.", relPath));
			}
			dir = new File(resource.toURI());
		} catch (Throwable cause) {
			if(!fromJarFiles) {
				throw new ClassNotFoundRuntimeException(String.format(
					"%s (%s) does not appear to be a valid package ", packageName, dir), cause);
			}
		}
		
		if(dir != null && dir.exists()) {
			try {
				for(File file : FileUtils.listFiles(dir, new String[]{"class"}, true)) {
					if(file == null) {
						continue;
					}
					String fileName = file.getAbsolutePath().replace(dir.getAbsolutePath(), "");
					StringBuilder className = new StringBuilder(packageName);
					className.append(fileName.substring(0, fileName.length() - 6).replace('\\', '.').replace('/', '.'));
					Logs.debug(className);
					Class<?> clazz = Class.forName(className.toString());
					classes.add(clazz);
				} // end of FOR
			} catch (ClassNotFoundException e) {
				throw new ClassNotFoundRuntimeException("", e);
			} // end of try...catch
		}
		
		if(fromJarFiles) {
			try {
				for(JarFile jarFile : getClassPathJars()) {
					Enumeration<JarEntry> entries = jarFile.entries();
					while(entries.hasMoreElements()) {
						String entryName = entries.nextElement().getName();
						if(!entryName.startsWith(relPath) ||
							!entryName.toLowerCase().endsWith(".class")) {
							continue;
						}
						String name = entryName.substring(0, entryName.length() - 6).replace('/', '.');
						try {
							Class<?> clazz = Class.forName(name);
							classes.add(clazz);
						} catch (Error err) {
							// ignore
						}
					} // end of WHILE
				} // end of FOR
			} catch (ClassNotFoundException e) {
				throw new ClassNotFoundRuntimeException("", e);
			} // end of try...catch
		} // end of if(fromJarFiles)
		
		return classes.toArray(new Class<?>[classes.size()]);
	}
	
	/**
	 * 获取运行时CLASS PATH中的jar文件列表
	 *
	 * @return 如果不存在jar文件则返回空的数组
	 * @since 2.7.7
	 */
	public static JarFile[] getClassPathJars() {
		String classPath = Systems.JAVA_CLASS_PATH;
		String separator = File.pathSeparator;
		
		if(classPath == null || classPath.trim().length() == 0) {
			return new JarFile[] {};
		}
		
		List<JarFile> jars = new ArrayList<>();
		for(String fileName : Strings.split(classPath, separator)) {
			if(fileName == null || (fileName = fileName.trim()).length() == 0
				|| !fileName.toLowerCase().endsWith(".jar")) {
				continue;
			}
			
			try {
				JarFile jarFile = new JarFile(fileName);
				jars.add(jarFile);
			} catch (IOException e) {
				continue;
			} // end of try...catch
		} // end of FOR
		
		return jars.toArray(new JarFile[jars.size()]);
	}
	
	/**
	 * 判断当前类是否是内部类。
	 *
	 * @param clazz 待判断的类
	 * @return 返回是否是内部类，如果为null则返回false。
	 */
	public static boolean isInnerClass(Class<?> clazz) {
		// 参考：https://coderanch.com/t/545389/java/Check-class-class
		return clazz != null && clazz.getEnclosingClass() != null;
	}
	
}
