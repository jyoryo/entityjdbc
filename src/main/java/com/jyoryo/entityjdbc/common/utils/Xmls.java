package com.jyoryo.entityjdbc.common.utils;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.jyoryo.entityjdbc.common.Assert;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.common.exception.FormatRuntimeException;
import com.jyoryo.entityjdbc.common.exception.UtilException;
import com.jyoryo.entityjdbc.common.io.Files;
import com.jyoryo.entityjdbc.common.io.IOs;
import com.jyoryo.entityjdbc.common.log.Logs;

/**
 * XML工具类<br>
 * 此工具使用w3c dom工具，不需要依赖第三方包。<br>
 * 工具类封装了XML文档的创建、读取、写出和部分XML操作
 * 
 * @author jyoryo
 *
 */
public class Xmls {
	/** 在XML中无效的字符 正则 */
	public final static String INVALID_REGEX = "[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]";

	/**
	 * 读取解析XML文件
	 * 
	 * @param file   XML文件
	 * @return   XML文档对象
	 * @throws IOException 
	 */
	public static Document of(File file) {
		Assert.notNull(file);
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(Files.openInputStream(file));
			return of(in);
		} catch (Exception e) {
			Logs.error("", e);
			return null;
		} finally {
			IOs.close(in);
		}
	}
	
	/**
	 * 读取解析XML文件
	 * @param file   待解析的XML文件
	 * @param addCDATA   是否对有以"des"开头的标签自动添加CDATA标签
	 * @return
	 */
	public static Document of(File file, boolean addCDATA) {
		return of(file, StandardCharsets.UTF_8, addCDATA);
	}
	
	/**
	 * 读取解析XML文件
	 * @param file   待解析的XML文件
	 * @param charset   解析字符集，不设置则用utf-8
	 * @param addCDATA   是否自动添加CDATA标签
	 * @return
	 */
	public static Document of(File file, Charset charset, boolean addCDATA) {
		if(!addCDATA) {
			return of(file);
		}
		if(null == charset) {
			charset = StandardCharsets.UTF_8;
		}
		try {
			String source = Files.readFileToString(file, charset);
			String content = addCDATA ? addCDATA(source) : source;
			return of(Strings.getReader(content));
		} catch (IOException e) {
			Logs.error("", e);
			return null;
		}
	}
	
	/**
	 * 读取解析XML文件
	 * 
	 * @param reader XML流
	 * @return XML文档对象
	 * @throws UtilException IO异常或转换异常
	 */
	public static Document of(InputStream inputStream) {
		return of(new InputSource(inputStream));
	}
	
	/**
	 * 读取解析XML文件
	 * 
	 * @param reader XML流
	 * @return XML文档对象
	 */
	public static Document of(Reader reader){
		return of(new InputSource(reader));
	}
	
	/**
	 * 读取解析XML文件<br>
	 * 如果给定内容以“&lt;”开头，表示这是一个XML内容，直接读取，否则按照路径处理<br>
	 * 路径可以为相对路径，也可以是绝对路径，相对路径相对于ClassPath
	 * 
	 * @param pathOrContent 内容或路径
	 * @return XML文档对象
	 * @throws IOException 
	 * @since 3.0.9
	 */
	public static Document of(String pathOrContent) {
		if (Strings.startsWith(pathOrContent, "<")) {
			return parseXml(pathOrContent);
		}
		return of(Files.getFile(pathOrContent));
	}
	
	/**
	 * 读取解析XML文件<br>
	 * 编码在XML中定义
	 * 
	 * @param source {@link InputSource}
	 * @return XML文档对象
	 * @since 3.0.9
	 */
	public static Document of(InputSource source) {
		final DocumentBuilder builder = createDocumentBuilder();
		try {
			return builder.parse(source);
		} catch (Exception e) {
			throw new FormatRuntimeException(e, "Parse XML from stream error!");
		}
	}
	
	/**
	 * 将String类型的XML转换为XML文档
	 * 
	 * @param xmlStr XML字符串
	 * @return XML文档
	 */
	public static Document parseXml(String xmlStr) {
		return parseXml(xmlStr, false);
	}
	
	/**
	 * 将String类型的XML转换为XML文档
	 * 
	 * @param xmlStr XML字符串
	 * @return XML文档
	 */
	public static Document parseXml(String xmlStr, boolean addCDATA ) {
		if(Strings.isBlank(xmlStr)) {
			throw new IllegalArgumentException("XML content string is empty !");
		}
		xmlStr = cleanInvalid(xmlStr);
		return of(new InputSource(Strings.getReader(addCDATA ? addCDATA(xmlStr) : xmlStr)));
	}
	
	private static Pattern ATTERN_CONTENT = Pattern.compile("(<[^>]+?des[^>]+?>)(.+?)(</[^#>]+?>)", Pattern.DOTALL);
	
	/**
	 * 将xml内容自动添加<![CDATA[{content}]]>标记
	 * @param xmlString
	 * @return
	 */
	public static String addCDATA(String xmlString) {
		xmlString = Strings.trim(xmlString);
		if(Strings.isBlank(xmlString)) {
			return null;
		}
		xmlString = Strings.remove(xmlString, "<![CDATA[");
		xmlString = Strings.remove(xmlString, "]]>"); 
		return ATTERN_CONTENT.matcher(xmlString).replaceAll("$1<![CDATA[$2]]>$3");
	}
	
	/**
	 * 从XML中读取对象 Reads serialized object from the XML file.
	 * 
	 * @param <T> 对象类型
	 * @param source XML文件
	 * @return 对象
	 * @throws IOException IO异常
	 */
	public static <T> T readObjectFromXml(File source) throws IOException {
		return readObjectFromXml(new InputSource(Files.getInputStream(source)));
	}

	/**
	 * 从XML中读取对象 Reads serialized object from the XML file.
	 * 
	 * @param <T> 对象类型
	 * @param xmlStr XML内容
	 * @return 对象
	 * @throws IOException IO异常
	 * @since 3.2.0
	 */
	public static <T> T readObjectFromXml(String xmlStr) throws IOException {
		return readObjectFromXml(new InputSource(Strings.getReader(xmlStr)));
	}

	/**
	 * 从XML中读取对象 Reads serialized object from the XML file.
	 * 
	 * @param <T> 对象类型
	 * @param source {@link InputSource}
	 * @return 对象
	 * @throws IOException IO异常
	 * @since 3.2.0
	 */
	@SuppressWarnings("unchecked")
	public static <T> T readObjectFromXml(InputSource source) throws IOException {
		Object result = null;
		XMLDecoder xmldec = null;
		try {
			xmldec = new XMLDecoder(source);
			result = xmldec.readObject();
		} finally {
			IOs.close(xmldec);
		}
		return (T) result;
	}
	
	/**
	 * 去除XML文本中的无效字符
	 * 
	 * @param xmlContent XML文本
	 * @return 当传入为null时返回null
	 */
	public static String cleanInvalid(String xmlContent) {
		if (xmlContent == null) {
			return null;
		}
		return xmlContent.replaceAll(INVALID_REGEX, "");
	}
	
	/**
	 * 创建 DocumentBuilder
	 * @return DocumentBuilder
	 */
	public static DocumentBuilder createDocumentBuilder() {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		disableXXE(dbf);
		DocumentBuilder builder = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (Exception e) {
			throw new FormatRuntimeException(e, "Create xml document error!");
		}
		return builder;
	}
	
	/**
	 * 关闭XXE，避免漏洞攻击<br>
	 * see: https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#JAXP_DocumentBuilderFactory.2C_SAXParserFactory_and_DOM4J
	 * @param dbf DocumentBuilderFactory
	 * @return DocumentBuilderFactory
	 */
	private static DocumentBuilderFactory disableXXE(DocumentBuilderFactory dbf) {
		String feature;
		try {
			// This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
			// Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
			feature = "http://apache.org/xml/features/disallow-doctype-decl";
			dbf.setFeature(feature, true);
			// If you can't completely disable DTDs, then at least do the following:
			// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
			// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
			// JDK7+ - http://xml.org/sax/features/external-general-entities
			feature = "http://xml.org/sax/features/external-general-entities";
			dbf.setFeature(feature, false);
			// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
			// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
			// JDK7+ - http://xml.org/sax/features/external-parameter-entities
			feature = "http://xml.org/sax/features/external-parameter-entities";
			dbf.setFeature(feature, false);
			// Disable external DTDs as well
			feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
			dbf.setFeature(feature, false);
			// and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
			dbf.setXIncludeAware(false);
			dbf.setExpandEntityReferences(false);
		} catch (ParserConfigurationException e) {
			// ignore
		}
		return dbf;
	}

	/**
	 * 将XML文档转换为String<br>
	 * 字符编码使用XML文档中的编码，获取不到则使用UTF-8
	 * 
	 * @param doc XML文档
	 * @return XML字符串
	 */
	public static String readToString(Document doc) {
		return readToString(doc, true);
	}
	
	/**
	 * 将XML文档转换为String<br>
	 * 字符编码使用XML文档中的编码，获取不到则使用UTF-8
	 * 
	 * @param doc   XML文档
	 * @param format   是否格式化输出
	 * @return   XML字符串
	 */
	public static String readToString(Document doc, boolean format) {
		final StringWriter writer = new StringWriter();
		try {
			write(doc, writer, format);
		} catch (Exception e) {
			throw new FormatRuntimeException(e, "Trans xml document to string error!");
		}
		return writer.toString();
	}
	
	/**
	 * 将XML文档转换为String<br>
	 * 字符编码使用XML文档中的编码，获取不到则使用UTF-8
	 * 
	 * @param doc XML文档
	 * @param charset 编码
	 * @param format 是否格式化输出
	 * @return XML字符串
	 */
	public static String readToString(Document doc, String charset, boolean format) {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			write(doc, out, charset, format);
		} catch (Exception e) {
			throw new FormatRuntimeException(e, "Trans xml document to string error!");
		}
		return out.toString();
	}
	
	/**
	 * 将XML文档写入到文件<br>
	 * 使用Document中的编码
	 * 
	 * @param doc XML文档
	 * @param absolutePath 文件绝对路径，不存在会自动创建
	 */
	public static void toFile(Document doc, String absolutePath) {
		toFile(doc, absolutePath, null);
	}

	/**
	 * 将XML文档写入到文件<br>
	 * 
	 * @param doc XML文档
	 * @param path 文件路径绝对路径，不存在会自动创建
	 * @param charset 自定义XML文件的编码，如果为{@code null} 读取XML文档中的编码，否则默认UTF-8
	 */
	public static void toFile(Document doc, String path, String charset) {
		if (Strings.isBlank(charset)) {
			charset = doc.getXmlEncoding();
		}
		if (Strings.isBlank(charset)) {
			charset = StandardCharsets.UTF_8.name();
		}

		BufferedWriter writer = null;
		try {
			writer = Files.getWriter(path, charset, false);
			write(doc, writer, true);
		} finally {
			IOs.close(writer);
		}
	}
	
	/**
	 * 将XML文档写出
	 * @param node   {@link Node} XML文档节点或文档本身
	 * @param writer   写出的Writer，Writer决定了输出XML的编码
	 * @param format   是否格式化输出
	 */
	public static void write(Node node, Writer writer, boolean format) {
		transform(new DOMSource(node), new StreamResult(writer), null, format);
	}
	
	/**
	 * 将XML文档写出
	 * 
	 * @param node {@link Node} XML文档节点或文档本身
	 * @param out 写出的Writer，Writer决定了输出XML的编码
	 * @param charset 编码
	 * @param isPretty 是否格式化输出
	 * @since 4.0.8
	 */
	public static void write(Node node, OutputStream out, String charset, boolean format) {
		transform(new DOMSource(node), new StreamResult(out), charset, format);
	}
	
	/**
	 * 将XML文档写出
	 * 
	 * @param source 源
	 * @param result 目标
	 * @param charset 编码
	 * @param isPretty 是否格式化输出
	 * @since 4.0.9
	 */
	public static void transform(Source source, Result result, String charset, boolean format) {
		final TransformerFactory factory = TransformerFactory.newInstance();
		try {
			final Transformer xformer = factory.newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, format ? "yes" : "no");
			if (Strings.isNotBlank(charset)) {
				xformer.setOutputProperty(OutputKeys.ENCODING, charset);
			}
			xformer.transform(source, result);
		} catch (Exception e) {
			throw new FormatRuntimeException(e, "Trans xml document to string error!");
		}
	}
	
	/**
	 * 创建XML文档<br>
	 * 创建的XML默认是utf8编码，修改编码的过程是在toStr和toFile方法里，既XML在转为文本的时候才定义编码
	 * 
	 * @return XML文档
	 * @since 4.0.8
	 */
	public static Document createXml() {
		return createDocumentBuilder().newDocument();
	}
	
	/**
	 * 创建XML文档<br>
	 * 创建的XML默认是utf8编码，修改编码的过程是在toStr和toFile方法里，既XML在转为文本的时候才定义编码
	 * 
	 * @param rootElementName 根节点名称
	 * @return XML文档
	 */
	public static Document createXml(String rootElementName) {
		final Document doc = createXml();
		doc.appendChild(doc.createElement(rootElementName));

		return doc;
	}

	// -------------------------------------------------------------------------------------- Function
	/**
	 * 获得XML文档根节点
	 * 
	 * @param doc {@link Document}
	 * @return 根节点
	 * @see Document#getDocumentElement()
	 * @since 3.0.8
	 */
	public static Element getRootElement(Document doc) {
		return (null == doc) ? null : doc.getDocumentElement();
	}
	
	/**
	 * 创建XPath<br>
	 * Xpath相关文章：https://www.ibm.com/developerworks/cn/xml/x-javaxpathapi.html
	 * @return {@link XPath}
	 */
	public static XPath createXPath() {
		return XPathFactory.newInstance().newXPath();
	}
	
	/**
	 * 通过XPath方式读取XML节点等信息<br>
	 * Xpath相关文章：https://www.ibm.com/developerworks/cn/xml/x-javaxpathapi.html
	 * 
	 * @param expression XPath表达式
	 * @param source 资源，可以是Docunent、Node节点等
	 * @return 匹配返回类型的值
	 */
	public static Node getNodeByXPath(String expression, Object source) {
		return (Node) getByXPath(expression, source, XPathConstants.NODE);
	}

	/**
	 * 通过XPath方式读取XML节点等信息<br>
	 * Xpath相关文章：https://www.ibm.com/developerworks/cn/xml/x-javaxpathapi.html
	 * 
	 * @param expression XPath表达式
	 * @param source 资源，可以是Docunent、Node节点等
	 * @param returnType 返回类型，{@link javax.xml.xpath.XPathConstants}
	 * @return 匹配返回类型的值
	 */
	public static Object getByXPath(String expression, Object source, QName returnType) {
		final XPath xPath = createXPath();
		try {
			if (source instanceof InputSource) {
				return xPath.evaluate(expression, (InputSource) source, returnType);
			} else {
				return xPath.evaluate(expression, source, returnType);
			}
		} catch (XPathExpressionException e) {
			throw new FormatRuntimeException(e);
		}
	}

}
