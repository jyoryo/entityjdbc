package com.jyoryo.entityjdbc.common;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

/**
 * String工具类
 * <li>基于Apache StringUtils</li>
 *
 * @auther jyoryo
 */
public class Strings extends StringUtils {
	public static final int INDEX_NOT_FOUND = -1;

	public static final String SPACE = " ";
	public static final String TAB = "	";
	public static final String DOT = ".";
	public static final String DOUBLE_DOT = "..";
	public static final String SLASH = "/";
	public static final String BACKSLASH = "\\";
	public static final String EMPTY = "";
	public static final String CR = "\r";
	public static final String LF = "\n";
	public static final String CRLF = "\r\n";
	public static final String UNDERLINE = "_";
	public static final String DASHED = "-";
	public static final String COMMA = ",";
	public static final String DELIM_START = "{";
	public static final String DELIM_END = "}";
	public static final String BRACKET_START = "[";
	public static final String BRACKET_END = "]";
	public static final String COLON = ":";

	public static final String HTML_NBSP = "&nbsp;";
	public static final String HTML_AMP = "&amp;";
	public static final String HTML_QUOTE = "&quot;";
	public static final String HTML_APOS = "&apos;";
	public static final String HTML_LT = "&lt;";
	public static final String HTML_GT = "&gt;";
	public static final String EMPTY_JSON = "{}";
	public static final String SLF4J_PAIR = "{}";
	
	/**
	 * 格式化文本, {} 表示占位符<br>
	 * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
	 * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
	 * 例：<br>
	 * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
	 * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
	 * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
	 * 
	 * <li>如果使用%s等占位符，使用JDK String自带format()方法</li>
	 * @param template 文本模板，被替换的部分用 {} 表示
	 * @param params 参数值
	 * @return 格式化后的文本
	 */
	public static String format(CharSequence template, Object... params) {
		if (null == template) {
			return null;
		}
		if (Arrays.isEmpty(params) || isBlank(template)) {
			return template.toString();
		}
		String strPattern = template.toString();
		final int strPatternLength = strPattern.length();

		//初始化定义好的长度以获得更好的性能
		StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

		//记录已经处理到的位置
		int handledPosition = 0;
		//占位符所在位置
		int delimIndex;
		for (int argIndex = 0; argIndex < params.length; argIndex++) {
			delimIndex = strPattern.indexOf(SLF4J_PAIR, handledPosition);
			//剩余部分无占位符
			if (delimIndex == -1) {
				//不带占位符的模板直接返回
				if (handledPosition == 0) {
					return strPattern;
				}
				//字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
				else {
					sbuf.append(strPattern, handledPosition, strPatternLength);
					return sbuf.toString();
				}
			} else {
				//转义符
				if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == Chars.BACKSLASH) {
					//双转义符
					if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == Chars.BACKSLASH) {
						//转义符之前还有一个转义符，占位符依旧有效
						sbuf.append(strPattern, handledPosition, delimIndex - 1);
						sbuf.append(toEncodedString(params[argIndex]));
						handledPosition = delimIndex + 2;
					} else {
						//占位符被转义
						argIndex--;
						sbuf.append(strPattern, handledPosition, delimIndex - 1);
						sbuf.append(Chars.DELIM_START);
						handledPosition = delimIndex + 1;
					}
				} else {//正常占位符
					sbuf.append(strPattern, handledPosition, delimIndex);
					sbuf.append(toEncodedString(params[argIndex]));
					handledPosition = delimIndex + 2;
				}
			}
		}
		// append the characters following the last {} pair.
		//加入最后一个占位符后所有的字符
		sbuf.append(strPattern, handledPosition, strPattern.length());
		
		return sbuf.toString();
	}
	
	/**
	 * 将对象按UTF-8转换为字符串
	 * @param object
	 * @return
	 */
	public static String toEncodedString(final Object object) {
		return toEncodedString(object, StandardCharsets.UTF_8);
	}

	/**
	 * 将对象按指定编码转换为字符串
	 * @param object
	 * @param charset
	 * @return
	 */
	public static String toEncodedString(final Object object, Charset charset) {
		if(null == object) {
			return null;
		}
		charset = (null != charset) ? charset : Charset.defaultCharset();
		
		if (object instanceof String) {
			return (String) object;
		} else if (object instanceof byte[]) {
			return toEncodedString((byte[]) object, charset);
		} else if (object instanceof Byte[]) {
			return toEncodedString((byte[]) object, charset);
		} else if (object instanceof ByteBuffer) {
			//return str((ByteBuffer) obj, charset);
			return charset.decode((ByteBuffer)object).toString();
		} else if (Arrays.isArray(object)) {
			return Arrays.toString(object);
		}
		return object.toString();
	}
	
	/**
	 * 将驼峰式命名的字符串转换为下划线方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。<br>
	 * 例如：
	 * 
	 * <pre>
	 * HelloWorld=》hello_world
	 * Hello_World=》hello_world
	 * HelloWorld_test=》hello_world_test
	 * </pre>
	 *
	 * @param str 转换前的驼峰式命名的字符串，也可以为下划线形式
	 * @return 转换后下划线方式命名的字符串
	 */
	public static String camelToSymbolCase(CharSequence str) {
		return camelToSymbolCase(str, Chars.UNDERLINE);
	}
	
	/**
	 * 将驼峰式命名的字符串转换为使用符号连接方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。<br>
	 *
	 * @param str 转换前的驼峰式命名的字符串，也可以为符号连接形式
	 * @param symbol 连接符
	 * @return 转换后符号连接方式命名的字符串
	 */
	public static String camelToSymbolCase(CharSequence str, char symbol) {
		if (str == null) {
			return null;
		}

		final int length = str.length();
		final StringBuilder sb = new StringBuilder();
		char c;
		for (int i = 0; i < length; i++) {
			c = str.charAt(i);
			final Character preChar = (i > 0) ? str.charAt(i - 1) : null;
			if (Character.isUpperCase(c)) {
				// 遇到大写字母处理
				final Character nextChar = (i < str.length() - 1) ? str.charAt(i + 1) : null;
				if (null != preChar && Character.isUpperCase(preChar)) {
					// 前一个字符为大写，则按照一个词对待
					sb.append(c);
				} else if (null != nextChar && Character.isUpperCase(nextChar)) {
					// 后一个为大写字母，按照一个词对待
					if (null != preChar && symbol != preChar) {
						// 前一个是非大写时按照新词对待，加连接符
						sb.append(symbol);
					}
					sb.append(c);
				} else {
					// 前后都为非大写按照新词对待
					if (null != preChar && symbol != preChar) {
						// 前一个非连接符，补充连接符
						sb.append(symbol);
					}
					sb.append(Character.toLowerCase(c));
				}
			} else {
				if (sb.length() > 0 && Character.isUpperCase(sb.charAt(sb.length() - 1)) && symbol != c) {
					// 当结果中前一个字母为大写，当前为小写，说明此字符为新词开始（连接符也表示新词）
					sb.append(symbol);
				}
				// 小写或符号
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 将下划线方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。<br>
	 * 例如：hello_world=》helloWorld
	 *
	 * @param name 转换前的下划线大写方式命名的字符串
	 * @return 转换后的驼峰式命名的字符串
	 */
	public static String toCamelCase(CharSequence name) {
		if (null == name) {
			return null;
		}

		String name2 = name.toString();
		if (name2.contains(UNDERLINE)) {
			final StringBuilder sb = new StringBuilder(name2.length());
			boolean upperCase = false;
			for (int i = 0; i < name2.length(); i++) {
				char c = name2.charAt(i);

				if (c == Chars.UNDERLINE) {
					upperCase = true;
				} else if (upperCase) {
					sb.append(Character.toUpperCase(c));
					upperCase = false;
				} else {
					sb.append(Character.toLowerCase(c));
				}
			}
			return sb.toString();
		} else {
			return name2;
		}
	}
	
	/**
     * 将字符串转换为“骆驼命名规则”，如果字符串不符合变量命名的规则则返回null
     * <pre>
     * Strings.camelCase(null) = null
     * Strings.camelCase("ab cd") = null
     * Strings.camelCase("2abcd") = null
     * Strings.camelCase("") = ""
     * Strings.camelCase("var") = var
     * Strings.camelCase("Var") = var
     * Strings.camelCase("MyVar") = myVar
     * Strings.camelCase("IOVar") = ioVar
     * Strings.camelCase("MyVAR") = myVAR
     * </pre>
     * 
     * @param str
     * @return 如果为空字符串则原样返回，如果字符串不符合变量命名方式则返回null，否则返回处理后的字符串
     */
    public static String camelCase(String str) {
        return camelCase(str, true);
    }
	
	/**
     * 将字符串转换为“骆驼命名规则”
     * 
     * <pre>
     * Strings.camelCase(null) = null
     * Strings.camelCase("ab cd") = variableRule ? null : "ab cd"
     * Strings.camelCase("2abcd") = variableRule ? null : "2abcd"
     * Strings.camelCase("") = ""
     * Strings.camelCase("var") = var
     * Strings.camelCase("Var") = var
     * Strings.camelCase("MyVar") = myVar
     * Strings.camelCase("IOVar") = ioVar
     * Strings.camelCase("MyVAR") = myVAR
     * </pre>
     * 
     * @param str
     * @param variableRule 使用变量规则，即：只能是字母和数字并且首字母不能是数字
     * @return 如果为空字符串则原样返回，如果设置为变量模式并且字符串不符合变量命名方式则返回null，
     *      否则返回处理后的字符串
     */
    public static String camelCase(String str, boolean variableRule) {
        if(isBlank(str)) {
            return str;
        }
        
        str = str.trim();
        if(variableRule && !isVariable(str)) {
            return null;
        }
        
        // 第一个字母小写的话直接返回原字符串
        char firstChar = str.charAt(0);
        if(Character.isLowerCase(firstChar)) {
            return str;
        }
        
        int length = str.length();
        // 第一个字母先小写然后放入builder中
        StringBuilder sbd = new StringBuilder(length);
        sbd.append(Character.toLowerCase(firstChar));
        // 是否遇到小写字母
        boolean haveLowerCaseChar = false;
        // 从第二个字母开始遍历
        for(int i = 1; i < length - 1; i ++) {
            char chr = str.charAt(i);
            if(Character.isLowerCase(chr)) {
                haveLowerCaseChar = true;
            }
            
            // 在没有遇到小写字母之前，当前字母的前、后和自己都是大写字母的话则修改成小写字母
            if(!haveLowerCaseChar && Character.isUpperCase(chr) 
                    && Character.isUpperCase(str.charAt(i - 1))
                    && Character.isUpperCase(str.charAt(i + 1))) {
                sbd.append(Character.toLowerCase(chr));
            } else {
                sbd.append(chr);
            }
        }
        sbd.append(str.charAt(length - 1));
        
        return sbd.toString();//CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, str);
    }
    
    /**
     * 字母、数字和下划线
     * 
     * @param str
     * @return
     */
    public static boolean isVariable(String str) {
        if(str == null || str.equals("")) {
            return false;
        }
        
        return str.matches("[a-zA-Z_$]([a-zA-Z0-9_]*)");
    }
	
	/**
	 * 获得StringReader
	 * 
	 * @param str 字符串
	 * @return StringReader
	 */
	public static StringReader getReader(CharSequence str) {
		if (null == str) {
			return null;
		}
		return new StringReader(str.toString());
	}
	
	/**
	 * 获取StringWriter
	 * @return
	 */
	public static StringWriter getWriter() {
		return new StringWriter();
	}
	
	/**
	 * 包装指定字符串
	 *
	 * @param s 被包装的字符串
	 * @param prefix 前缀
	 * @param suffix 后缀
	 * @return 包装后的字符串
	 */
	public static String wrap(CharSequence s, CharSequence prefix, CharSequence suffix) {
		return defaultString(prefix).concat(defaultString(s)).concat(defaultString(suffix));
	}
	
	/**
	 * 当给定字符串为null时，返回Empty，否则返回本身
	 * @param s
	 * @return
	 */
	public static String defaultString(CharSequence s) {
		return defaultString(s, EMPTY);
	}
	
	/**
	 * 当给定字符串为null时，返回给定的defaultStr，否则返回本身
	 * @param s
	 * @param defaultStr
	 * @return
	 */
	public static String defaultString(CharSequence s, String defaultStr) {
		return (null == s) ? defaultStr : s.toString();
	}
	
	/**
	 * 调用对象的toString方法，null会返回“null”
	 *
	 * @param obj 对象
	 * @return 字符串
	 */
	public static String toString(Object obj) {
		return null == obj ? "null" : obj.toString();
	}
	
	/**
	 * 清理空白字符
	 *
	 * @param str 被清理的字符串
	 * @return 清理后的字符串
	 */
	public static String cleanBlank(CharSequence str) {
		if (str == null) {
			return null;
		}
		
		int len = str.length();
		final StringBuilder sb = new StringBuilder(len);
		char c;
		for (int i = 0; i < len; i++) {
			c = str.charAt(i);
			if (false == Chars.isBlankChar(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 切割指定位置之前部分的字符串
	 *
	 * @param string 字符串
	 * @param toIndex 切割到的位置（不包括）
	 * @return 切割后的剩余的前半部分字符串
	 */
	public static String subPre(CharSequence string, int toIndex) {
		return substring(string.toString(), 0, toIndex);
	}
	
	/**
	 * 切割指定位置之后部分的字符串
	 *
	 * @param string 字符串
	 * @param fromIndex 切割开始的位置（包括）
	 * @return 切割后后剩余的后半部分字符串
	 */
	public static String subSuf(CharSequence string, int fromIndex) {
		if (isEmpty(string)) {
			return null;
		}
		return substring(string.toString(), fromIndex, string.length());
	}
	
	/**
	 * 转为字符串，null安全
	 * @param cs
	 * @return
	 */
	public static String toString(CharSequence cs) {
		return (null == cs) ? null : cs.toString();
	}
	
	/**
	 * 去掉指定前缀
	 *
	 * @param str 字符串
	 * @param prefix 前缀
	 * @return 切掉后的字符串，若前缀不是 preffix， 返回原字符串
	 */
	public static String removePrefix(CharSequence str, CharSequence prefix) {
		if (isEmpty(str) || isEmpty(prefix)) {
			return toString(str);
		}
		
		final String str2 = str.toString();
		if (str2.startsWith(prefix.toString())) {
			return subSuf(str2, prefix.length());// 截取后半段
		}
		return str2;
	}
	
	/**
	 * 忽略大小写去掉指定前缀
	 *
	 * @param str 字符串
	 * @param prefix 前缀
	 * @return 切掉后的字符串，若前缀不是 prefix， 返回原字符串
	 */
	public static String removePrefixIgnoreCase(CharSequence str, CharSequence prefix) {
		if (isEmpty(str) || isEmpty(prefix)) {
			return toString(str);
		}
		
		final String str2 = str.toString();
		if (str2.toLowerCase().startsWith(prefix.toString().toLowerCase())) {
			return subSuf(str2, prefix.length());// 截取后半段
		}
		return str2;
	}
	
	/**
     * 移除URL前缀"/"
     * <li>如果包含前缀"/"，返回移除前缀"/"的结果；否则不作处理。</li>
     * @param str
     * @return
     */
    public static String removeUrlPrefix(String str) {
        if(startsWith(str, "/")) {
            return substring(str, 1);
        }
        return str;
    }
    
    /**
     * 移除URL后缀"/"
     * <li>如果包含后缀"/"，返回移除前缀"/"的结果；否则不作处理。</li>
     * @param str
     * @return
     */
    public static String removeUrlSuffix(String str) {
        if(endsWith(str, "/")) {
            return substring(str, 0, str.length() - 1);
        }
        return str;
    }
}
