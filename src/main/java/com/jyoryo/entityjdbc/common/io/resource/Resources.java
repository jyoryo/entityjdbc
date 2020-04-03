package com.jyoryo.entityjdbc.common.io.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.jyoryo.entityjdbc.common.Assert;
import com.jyoryo.entityjdbc.common.ClassUtil;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.common.io.URLs;
import com.jyoryo.entityjdbc.common.log.Logs;

/**
 * @auther: jyoryo
 * @Date: 2019.4.2 01:10
 */
public class Resources {
	/**
	 * Return whether the given resource location is a URL:
	 * either a special "classpath" pseudo URL or a standard URL.
	 * @param resourceLocation the location String to check
	 * @return whether the location qualifies as a URL
	 */
	public static boolean isUrl(String resourceLocation) {
		if (resourceLocation == null) {
			return false;
		}
		if (resourceLocation.startsWith(URLs.CLASSPATH_URL_PREFIX)) {
			return true;
		}
		try {
			new URL(resourceLocation);
			return true;
		}
		catch (MalformedURLException ex) {
			return false;
		}
	}
	
	/**
	 * 使用静默方式获取指定的资源，支持
	 * @param resourceLocation
	 * @return
	 */
	public static URL getURLQuietly(String resourceLocation) {
		try {
			return getURL(resourceLocation);
		} catch (FileNotFoundException e) {
			Logs.error("", e);
			return null;
		}
	}
	
	/**
	 * Resolve the given resource location to a <code>java.net.URL</code>.
	 * <p>Does not check whether the URL actually exists; simply returns
	 * the URL that the given location would correspond to.
	 * @param resourceLocation the resource location to resolve: either a
	 * "classpath:" pseudo URL, a "file:" URL, or a plain file path
	 * @return a corresponding URL object
	 * @throws FileNotFoundException if the resource cannot be resolved to a URL
	 */
	public static URL getURL(String resourceLocation) throws FileNotFoundException {
		Assert.notNull(resourceLocation, "Resource location must not be null");
		if (resourceLocation.startsWith(URLs.CLASSPATH_URL_PREFIX)) {
			String path = resourceLocation.substring(URLs.CLASSPATH_URL_PREFIX.length());
			URL url = getClasspathURL(path);
			if (url == null) {
				String description = "class path resource [" + path + "]";
				throw new FileNotFoundException(description + " cannot be resolved to URL because it does not exist");
			}
			return url;
		}
		try {   // try URL
			return new URL(resourceLocation);
		} catch (MalformedURLException ex) {
			try {   // no URL -> treat as file path
				return new File(resourceLocation).toURI().toURL();
			} catch (MalformedURLException ex2) {
				throw new FileNotFoundException("Resource location [" + resourceLocation + "] is neither a URL not a well-formed file path");
			}
		}
	}
	
	/**
	 * 获得资源的URL<br>
	 * 路径用/分隔，例如:
	 *
	 * <pre>
	 * config/a/db.config
	 * spring/xml/test.xml
	 * </pre>
	 *
	 * @param resource 资源（相对Classpath的路径）
	 * @return 资源URL
	 */
	public static URL getClasspathURL(String resource) {
		return getClasspathURL(resource, null);
	}
	
	/**
	 * 获得资源相对路径对应的URL
	 *
	 * @param resource 资源相对路径
	 * @param baseClass 基准Class，获得的相对路径相对于此Class所在路径，如果为{@code null}则相对ClassPath
	 * @return {@link URL}
	 */
	public static URL getClasspathURL(String resource, Class<?> baseClass) {
		return (null != baseClass) ? baseClass.getResource(resource) : ClassUtil.getClassLoader().getResource(resource);
	}
	
	/**
	 * 获取指定文件位置的输入流，支持classpath和jar文件中的资源文件。
	 *
	 * @param resourceLocation 资源文件位置，支持classpath定义
	 * @return 返回指定文件的输入流文件
	 * @throws IOException
	 * @since 2.9.32
	 */
	public static InputStream getInputStream(String resourceLocation) throws IOException {
		return getURL(resourceLocation).openStream();
	}
	
	/**
	 * 使用静默方式获取指定的文件，如果查找不到指定的文件则返回null，详细用法请见{@link #getFile(String)}。
	 *
	 * @param resourceLocation the resource location to resolve: either a "classpath:" pseudo URL,
	 * 		a "file:" URL, or a plain file path
	 * @return 返回指定的文件对象，如果文件不存在则返回null
	 * @since 3.0.0
	 * @see #getFile(String)
	 */
	public static File getFileQuietly(String resourceLocation) {
		try {
			return getFile(resourceLocation);
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * Resolve the given resource location to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * <p>Does not check whether the fil actually exists; simply returns
	 * the File that the given location would correspond to.
	 *
	 * <p>本方法不支持jar文件中的资源文件，如果需要访问jar文件中的资源文件可以使用{@link #getURL(String)}方法。
	 *
	 * @param resourceLocation the resource location to resolve: either a
	 * "classpath:" pseudo URL, a "file:" URL, or a plain file path
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the resource cannot be resolved to
	 * a file in the file system
	 */
	public static File getFile(String resourceLocation) throws FileNotFoundException {
		Assert.notNull(resourceLocation, "Resource location must not be null");
		if (resourceLocation.startsWith(URLs.CLASSPATH_URL_PREFIX)) {
			String path = resourceLocation.substring(URLs.CLASSPATH_URL_PREFIX.length());
			String description = "class path resource [" + path + "]";
			URL url = ClassUtil.getClassLoader().getResource(path);
			if (url == null) {
				throw new FileNotFoundException(
					description + " cannot be resolved to absolute file path " +
						"because it does not reside in the file system");
			}
			return getFile(url, description);
		}
		try {
			// try URL
			return getFile(new URL(resourceLocation));
		}
		catch (MalformedURLException ex) {
			// no URL -> treat as file path
			return new File(resourceLocation);
		}
	}
	
	/**
	 * Resolve the given resource URL to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * @param resourceUrl the resource URL to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 */
	public static File getFile(URL resourceUrl) throws FileNotFoundException {
		return getFile(resourceUrl, "URL");
	}
	
	/**
	 * Resolve the given resource URL to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * @param resourceUrl the resource URL to resolve
	 * @param description a description of the original resource that
	 * the URL was created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 */
	public static File getFile(URL resourceUrl, String description) throws FileNotFoundException {
		Assert.notNull(resourceUrl, "Resource URL must not be null");
		if (!URLs.URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
			throw new FileNotFoundException(
				description + " cannot be resolved to absolute file path " +
					"because it does not reside in the file system: " + resourceUrl);
		}
		try {
			return new File(toURI(resourceUrl).getSchemeSpecificPart());
		}
		catch (URISyntaxException ex) {
			// Fallback for URLs that are not valid URLs (should hardly ever happen).
			return new File(resourceUrl.getFile());
		}
	}
	
	/**
	 * Resolve the given resource URI to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * @param resourceUri the resource URI to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 */
	public static File getFile(URI resourceUri) throws FileNotFoundException {
		return getFile(resourceUri, "URI");
	}
	
	/**
	 * Resolve the given resource URI to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * @param resourceUri the resource URI to resolve
	 * @param description a description of the original resource that
	 * the URI was created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 */
	public static File getFile(URI resourceUri, String description) throws FileNotFoundException {
		Assert.notNull(resourceUri, "Resource URI must not be null");
		if (!URLs.URL_PROTOCOL_FILE.equals(resourceUri.getScheme())) {
			throw new FileNotFoundException(
				description + " cannot be resolved to absolute file path " +
					"because it does not reside in the file system: " + resourceUri);
		}
		return new File(resourceUri.getSchemeSpecificPart());
	}
	
	/**
	 * Determine whether the given URL points to a resource in the file system,
	 * that is, has protocol "file" or "vfs".
	 * @param url the URL to check
	 * @return whether the URL has been identified as a file system URL
	 */
	public static boolean isFileURL(URL url) {
		String protocol = url.getProtocol();
		return (URLs.URL_PROTOCOL_FILE.equals(protocol) || protocol.startsWith(URLs.URL_PROTOCOL_VFS));
	}
	
	/**
	 * Determine whether the given URL points to a resource in a jar file,
	 * that is, has protocol "jar", "zip", "wsjar" or "code-source".
	 * <p>"zip" and "wsjar" are used by BEA WebLogic Server and IBM WebSphere, respectively,
	 * but can be treated like jar files. The same applies to "code-source" URLs on Oracle
	 * OC4J, provided that the path contains a jar separator.
	 * @param url the URL to check
	 * @return whether the URL has been identified as a JAR URL
	 */
	public static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return (URLs.URL_PROTOCOL_JAR.equals(protocol) ||
			URLs.URL_PROTOCOL_ZIP.equals(protocol) ||
			URLs.URL_PROTOCOL_WSJAR.equals(protocol) ||
			(URLs.URL_PROTOCOL_CODE_SOURCE.equals(protocol) && url.getPath().contains(URLs.JAR_URL_SEPARATOR)));
	}
	
	/**
	 * Extract the URL for the actual jar file from the given URL
	 * (which may point to a resource in a jar file or to a jar file itself).
	 * @param jarUrl the original URL
	 * @return the URL for the actual jar file
	 * @throws MalformedURLException if no valid jar file URL could be extracted
	 */
	public static URL extractJarFileURL(URL jarUrl) throws MalformedURLException {
		String urlFile = jarUrl.getFile();
		int separatorIndex = urlFile.indexOf(URLs.JAR_URL_SEPARATOR);
		if (separatorIndex != -1) {
			String jarFile = urlFile.substring(0, separatorIndex);
			try {
				return new URL(jarFile);
			}
			catch (MalformedURLException ex) {
				// Probably no protocol in original jar URL, like "jar:C:/mypath/myjar.jar".
				// This usually indicates that the jar file resides in the file system.
				if (!jarFile.startsWith("/")) {
					jarFile = "/" + jarFile;
				}
				return new URL(URLs.FILE_URL_PREFIX + jarFile);
			}
		}
		else {
			return jarUrl;
		}
	}
	
	/**
	 * Create a URI instance for the given URL,
	 * replacing spaces with "%20" quotes first.
	 * <p>Furthermore, this method works on JDK 1.4 as well,
	 * in contrast to the <code>URL.toURI()</code> method.
	 * @param url the URL to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the URL wasn't a valid URI
	 * @see java.net.URL#toURI()
	 */
	public static URI toURI(URL url) throws URISyntaxException {
		return toURI(url.toString());
	}
	
	/**
	 * Create a URI instance for the given location String,
	 * replacing spaces with "%20" quotes first.
	 * @param location the location String to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the location wasn't a valid URI
	 */
	public static URI toURI(String location) throws URISyntaxException {
		return new URI(Strings.replace(location, " ", "%20"));
	}
	
	/**
	 * 获取多个指定位置的文件对象，如果其中一个文件不存在则抛出异常。
	 *
	 * @param resources
	 * @return
	 * @throws FileNotFoundException
	 */
	public static File[] getFiles(String... resources) throws FileNotFoundException {
		Assert.notEmpty(resources);
		
		File[] files = new File[resources.length];
		for(int i = 0; i < resources.length; i ++) {
			files[i] = getFile(resources[i]);
		}
		
		return files;
	}
	
}
