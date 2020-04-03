package com.jyoryo.entityjdbc.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jyoryo.entityjdbc.builder.template.TemplateParser;
import com.jyoryo.entityjdbc.common.Chars;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.common.exception.FormatRuntimeException;
import com.jyoryo.entityjdbc.common.io.Files;
import com.jyoryo.entityjdbc.common.io.watch.SimpleWatcher;
import com.jyoryo.entityjdbc.common.io.watch.WatchMonitor;
import com.jyoryo.entityjdbc.common.io.watch.Watchs;
import com.jyoryo.entityjdbc.common.io.watch.watchers.DelayWatcher;
import com.jyoryo.entityjdbc.common.log.Logs;
import com.jyoryo.entityjdbc.common.utils.Xmls;
import com.jyoryo.entityjdbc.dao.JdbcDao;
import com.jyoryo.entityjdbc.dao.JdbcEntityDao;
import com.jyoryo.entityjdbc.exception.SqlBuilderException;
import com.jyoryo.entityjdbc.support.Condition;

/**
 * 动态构建sql的默认实现
 * <li>默认的sql起始标志为：$</li>
 * <li>默认的sql模板文件路径为：classpath:sqlfiles</li>
 * <li>默认的sql模板文件扩展名为：sqlt</li>
 * @author jyoryo
 */
public abstract class AbstractSqlBuilder implements SqlBuilder {
	/**
	 * 标识sql ID的前缀符号
	 */
	protected char idPrefix = '$';
	
	/**
	 * sql模板文件路径
	 * <li>支持classpath</li>
	 */
	protected String sqlFilePath = "classpath:sqlfiles";
	
	/**
	 * sql模板文件扩展名
	 * <li>默认为<code>sqlt</code></li>
	 */
	protected String sqlFileExtension = "sqlt";
	
	/**
	 * sql模板文件路径下文件变动时，是否自动重新加载
	 */
	protected boolean autoReload = true;
	
	/**
	 * 存储sql的容器
	 */
	private Map<String, String> sqlContainer = new HashMap<>();
	/**
	 * sql模板文件路径监听器
	 */
	private WatchMonitor watchMonitor;
	/**
	 * sql模板文件存储的目录
	 */
	private File sqltFile;
	
	public AbstractSqlBuilder() {
        super();
        this.init();
    }

    @Override
    public String sql(String sqlOrId, Condition condition) {
        sqlOrId = Strings.defaultIfBlank(sqlOrId, defaultSqlId());
        final String sqlTemplate =  Strings.startsWithIgnoreCase(sqlOrId, String.valueOf(idPrefix)) ? getSqlTemplateById(sqlOrId) : sqlOrId;
        if(Strings.isBlank(sqlTemplate)) {
            throw new SqlBuilderException("Can not read sql from:{}. Please check the sqlOrId:{}", idPrefix, sqlOrId)  ;
        }
        TemplateParser templateParser = getTemplateParser();
        String sql = templateParser.render(sqlTemplate, condition);
        if(Strings.isBlank(sql)) {
            throw new SqlBuilderException("模板id:{}解析内容返回为空！", sqlOrId);
        }
        sql = sql.replaceAll("\\s+", " ");
        return sql;
    }

    @Override
	public void setIdPrefix(char idPrefix) {
		this.idPrefix = idPrefix;
	}

	@Override
	public void setSqlFilePath(String sqlFilePath) {
		this.sqlFilePath = sqlFilePath;
	}

	@Override
	public void setSqlFileExtension(String extension) {
		this.sqlFileExtension = extension;
	}
	
	@Override
    public void setAutoReload(boolean autoReload) {
	    this.autoReload = autoReload;
    }

    /**
	 * 返回实际执行的模板解析器
	 * @return
	 */
	protected abstract TemplateParser getTemplateParser();

	/**
	 * 初始化
	 */
	public void init() {
	    if(Strings.isBlank(sqlFilePath)) {
            throw new SqlBuilderException("sql模板文件所在路径不能为空！");
        }
	    // 初始sqlContainer
	    if(null == this.sqlContainer || this.sqlContainer.isEmpty()) {
	        sqlContainer = new HashMap<>();
	    }
	    // 如有监听，则关闭
	    if(null != this.watchMonitor) {
	        this.watchMonitor.close();
	    }
	    try {
            sqltFile = ResourceUtils.getFile(sqlFilePath);
            if(!sqltFile.isDirectory()) {
                throw new SqlBuilderException("sql模板文件设置路径必须是目录！");
            }
            // 设置自动重新加载模板文件
            if(autoReload) {
                this.watchMonitor = Watchs.createModify(sqltFile, new DelayWatcher(new SimpleWatcher() {
                    @Override
                    public void onModify(WatchEvent<?> event, Path currentPath) {
                        Logs.debug("----reloading sql template directory---");
                        load();
                    }
                }, 2000L));
                this.watchMonitor.start();
                Logs.debug("sql template listening directory:{}", sqltFile.getAbsolutePath());
            }
        } catch (FileNotFoundException e) {
            throw new SqlBuilderException(Strings.format("sql template directory[{}] does not exist！", sqlFilePath), e);
        }
	    load();
	}
	
	/**
	 * 加载模板文件的内容
	 */
	synchronized protected void load() {
	    sqlContainer.clear();
	    Collection<File> sqltFiles = Files.listFiles(sqltFile, new String[] {sqlFileExtension}, true);
        if(null == sqltFiles || sqltFiles.isEmpty()) {
            throw new FormatRuntimeException("Dir Path：{}, there was no sql template file！", sqltFile.getAbsolutePath());
        }
        for(File sqlFile : sqltFiles) {
            readSqlFromFile(sqlFile);
        }
	}
	
	/**
	 * 从sql模板文件读取内容
	 * @param file
	 */
	private void readSqlFromFile(File file) {
		Document document = Xmls.of(file, true);
		Element elRoot = Xmls.getRootElement(document);
		final String packageName = elRoot.getAttribute("package");
		NodeList classNodes = elRoot.getChildNodes();
		int classLength = classNodes.getLength();
		for(int i = 0; i < classLength; i ++) {
			Node classNode = classNodes.item(i);
			if(Node.ELEMENT_NODE != classNode.getNodeType()) {
				continue ;
			}
			final String className = classNode.getNodeName();
			NodeList methodNodes = classNode.getChildNodes();
			int methodLength = methodNodes.getLength();
			for(int j = 0; j < methodLength; j ++) {
				Node methodNode = methodNodes.item(j);
				if(Node.ELEMENT_NODE != methodNode.getNodeType()) {
					continue ;
				}
				String key = new StringBuilder().append(packageName)
				        .append(Chars.DOT).append(className)
				        .append(Chars.DOT).append(methodNode.getNodeName())
				        .toString();
				if(sqlContainer.containsKey(key)) {
					throw new FormatRuntimeException("Key:{}重复，请检查模板内容！", key);
				}
				sqlContainer.put(key, Strings.trim(methodNode.getTextContent()));
			}
		}
	}
	
	/**
	 * 通过sqlId获取对应SQL模板
	 * @param sqlId
	 * @return
	 */
	protected String getSqlTemplateById(String sqlId) {
	    return sqlContainer.get(parseTemplateId(sqlId));
	}
	
	/**
	 * sqlId为空，默认取值
	 * @return
	 */
	protected String defaultSqlId() {
	    return Strings.repeat(idPrefix, 3);
	}
	
	/**
	 * 根据sqlId解析对应模板中的ID
	 * <li>两个前缀符代表"包名.类名."</li>
	 * <li>三个前缀符代表"包名.类名.方法名"</li>
	 * @param sqlId
	 * @return
	 */
	protected String parseTemplateId(String sqlId) {
	    // 移除第一个idPrefix
	    String tmplId = Strings.substring(sqlId, 1);
	    if(!Strings.startsWithIgnoreCase(tmplId, String.valueOf(idPrefix))) {
	        return tmplId;
	    }
	    String doubleIdPrefix = Strings.repeat(idPrefix, 2);
	    StackTraceElement[] array = new Throwable().getStackTrace();
	    if(null == array || 2 > array.length) {
	        return tmplId;
	    }
	    String className = null;
	    StringBuilder tmplBuilder = new StringBuilder();
	    for(StackTraceElement ste : array) {
	        className = ste.getClassName();
	        if(Strings.equalsAnyIgnoreCase(className, AbstractSqlBuilder.class.getName(), JdbcDao.class.getName(), JdbcEntityDao.class.getName())) {
	            continue ;
	        }
	        tmplBuilder.append(className);
	        if(tmplId.startsWith(doubleIdPrefix)) {
	            tmplBuilder.append(Chars.DOT).append(ste.getMethodName()).append(Strings.removeStartIgnoreCase(tmplId, doubleIdPrefix));
	        } else {
	            tmplBuilder.append(Strings.removeStartIgnoreCase(tmplId, String.valueOf(idPrefix)));
	        }
	        return tmplBuilder.toString();
	    }
	    return tmplId;
    }
}
