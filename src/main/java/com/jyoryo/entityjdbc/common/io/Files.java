package com.jyoryo.entityjdbc.common.io;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import com.jyoryo.entityjdbc.common.Chars;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.common.exception.FormatRuntimeException;
import com.jyoryo.entityjdbc.common.exception.IORuntimeException;

/**
 * 文件相关的工具类
 * <li>基于Apache FileUtils</li>
 * @author jyoryo
 *
 */
public class Files extends FileUtils {
	/** Class文件扩展名 */
	public static final String CLASS_EXT = ".class";
	/** Jar文件扩展名 */
	public static final String JAR_FILE_EXT = ".jar";
	/** 在Jar中的路径jar的扩展名形式 */
	public static final String JAR_PATH_EXT = ".jar!";

	/**
	 * 获取文件的输入流
	 * @param file
	 * @return
	 */
	public static BufferedInputStream getInputStream(File file) {
		try {
			return new BufferedInputStream(openInputStream(file));
		} catch (IOException e) {
			throw new FormatRuntimeException(e);
		}
	}
	
	/**
	 * 获取一个带Buffered的Writer
	 * <li>使用UTF-8字符集</li>
	 * @param pathname   文件路径名称
	 * @param append   是否追加
	 * @return
	 */
	public static BufferedWriter getWriter(String pathname, boolean append) {
		return getWriter(touch(pathname), StandardCharsets.UTF_8, append);
	}
	
	/**
	 * 获取一个带Buffered的Writer
	 * @param pathname   文件路径名称
	 * @param charset   字符集
	 * @param append   是否追加
	 * @return
	 */
	public static BufferedWriter getWriter(String pathname, String charset, boolean append) {
		return getWriter(touch(pathname), Charset.forName(charset), append);
	}
	
	/**
	 * 获取一个带Buffered的Writer
	 * @param pathname   文件路径名称
	 * @param charset   字符集
	 * @param append   是否追加
	 * @return
	 */
	public static BufferedWriter getWriter(String pathname, Charset charset, boolean append) {
		return getWriter(touch(pathname), charset, append);
	}
	
	/**
	 * 获取一个带Buffered的Writer
	 * <li>使用UTF-8字符集</li>
	 * @param file   输出文件
	 * @param append   是否追加
	 * @return
	 */
	public static BufferedWriter getWriter(File file, boolean append) {
		return getWriter(file, StandardCharsets.UTF_8, append);
	}
	
	/**
	 * 获取一个带Buffered的Writer
	 * @param file   输出文件
	 * @param charset   字符集
	 * @param append   是否追加
	 * @return
	 */
	public static BufferedWriter getWriter(File file, String charset, boolean append) {
		return getWriter(file, Charset.forName(charset), append);
	}
	
	/**
	 * 获取一个带Buffered的Writer
	 * @param file   输出文件
	 * @param charset   字符集
	 * @param append   是否追加
	 * @return
	 */
	public static BufferedWriter getWriter(File file, Charset charset, boolean append) {
		try {
			return new BufferedWriter(new OutputStreamWriter(openOutputStream(file, append), charset));
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}
	
	/**
	 * 创建所给文件或目录的父目录
	 * 
	 * @param file 文件或目录
	 * @return 父目录
	 */
	public static File mkParentDirs(File file) {
		final File parentFile = file.getParentFile();
		if (null != parentFile && false == parentFile.exists()) {
			parentFile.mkdirs();
		}
		return parentFile;
	}
	
	
	/**
	 * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
	 * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
	 * 
	 * @param pathname 文件的全路径，使用POSIX风格
	 * @return 文件，若路径为null，返回null
	 * @throws IORuntimeException IO异常
	 */
	public static File touch(String pathname) throws IORuntimeException {
		if (pathname == null) {
			return null;
		}
		File file = new File(pathname);
		if (false == file.exists()) {
			mkParentDirs(file);
			try {
				file.createNewFile();
			} catch (Exception e) {
				throw new IORuntimeException(e);
			}
		}
		return file;
	}
	
	/**
	 * 给定路径已经是绝对路径<br>
	 * 此方法并没有针对路径做标准化，建议先执行{@link org.apache.commons.io.FilenameUtils#normalize(String)}方法标准化路径后判断
	 *
	 * @param path 需要检查的Path
	 * @return 是否已经是绝对路径
	 */
	public static boolean isAbsolutePath(String path) {
		if (Strings.isEmpty(path)) {
			return false;
		}
		
		if (Chars.SLASH == path.charAt(0) || path.matches("^[a-zA-Z]:[/\\\\].*")) {
			// 给定的路径已经是绝对路径了
			return true;
		}
		return false;
	}
	
	/**
     * 获取指定位置的子路径部分，支持负数，例如index为-1表示从后数第一个节点位置
     * 
     * @param path 路径
     * @param index 路径节点位置，支持负数（负数从后向前计数）
     * @return 获取的子路径
     * @since 3.1.2
     */
    public static Path getPathEle(Path path, int index) {
        return subPath(path, index, index == -1 ? path.getNameCount() : index + 1);
    }

    /**
     * 获取指定位置的最后一个子路径部分
     * 
     * @param path 路径
     * @return 获取的最后一个子路径
     * @since 3.1.2
     */
    public static Path getLastPathEle(Path path) {
        return getPathEle(path, path.getNameCount() - 1);
    }

    /**
     * 获取指定位置的子路径部分，支持负数，例如起始为-1表示从后数第一个节点位置
     * 
     * @param path 路径
     * @param fromIndex 起始路径节点（包括）
     * @param toIndex 结束路径节点（不包括）
     * @return 获取的子路径
     * @since 3.1.2
     */
    public static Path subPath(Path path, int fromIndex, int toIndex) {
        if (null == path) {
            return null;
        }
        final int len = path.getNameCount();

        if (fromIndex < 0) {
            fromIndex = len + fromIndex;
            if (fromIndex < 0) {
                fromIndex = 0;
            }
        } else if (fromIndex > len) {
            fromIndex = len;
        }

        if (toIndex < 0) {
            toIndex = len + toIndex;
            if (toIndex < 0) {
                toIndex = len;
            }
        } else if (toIndex > len) {
            toIndex = len;
        }

        if (toIndex < fromIndex) {
            int tmp = fromIndex;
            fromIndex = toIndex;
            toIndex = tmp;
        }

        if (fromIndex == toIndex) {
            return null;
        }
        return path.subpath(fromIndex, toIndex);
    }
}
