package com.jyoryo.entityjdbc.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.jyoryo.entityjdbc.JpaEntity;
import com.jyoryo.entityjdbc.builder.SqlBuilder;
import com.jyoryo.entityjdbc.common.Arrays;
import com.jyoryo.entityjdbc.common.Chars;
import com.jyoryo.entityjdbc.common.Reflects;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.common.collection.CollectionUtil;
import com.jyoryo.entityjdbc.metadata.ColumnMetaData;
import com.jyoryo.entityjdbc.support.Condition;
import com.jyoryo.entityjdbc.support.Page;
import com.jyoryo.entityjdbc.utils.Jdbcs;

/**
 * JdbcDao
 * <p>支持将sql内容放入外部的模板文件中，且支持热加载。
 * <br />默认的sql模板目录：classpath:sqlfiles;
 * <br />默认的sql模板文件的后缀：sqlt
 * <br />sql内容以前缀符"$"开头，则表示通过模板获取sql内容
 * <li>两个前缀符代表"包名.类名."</li>
 * <li>三个前缀符代表"包名.类名.方法名"</li>
 * </p>
 * @author jyoryo
 *
 */
public final class JdbcDao {
    /**
     * MySQL 获取记录行数的sql
     */
    private final static String COUNT_SQL = "SELECT FOUND_ROWS()";
    
	// datasource
	private DataSource dataSource;
	// jdbcTemplate
	/**
	 * 该属性对象是：Spring NamedParameterJdbcTemplate
	 */
	private NamedParameterJdbcTemplate jdbcTemplate;
	/**
	 * 该属性对象是：Spring JdbcTemplate
	 */
	private JdbcOperations classicJdbcTemplate;
	/**
	 * 构建sql语句的builder
	 */
	private SqlBuilder sqlBuilder;
	
	public JdbcDao() {
		super();
	}
	public JdbcDao(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.classicJdbcTemplate = this.jdbcTemplate.getJdbcOperations();
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.classicJdbcTemplate = this.jdbcTemplate.getJdbcOperations();
	}
	
	public void setSqlBuilder(SqlBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }
    /**
	 * 获取当前连接的数据源
	 * @return
	 */
	public DataSource getDataSource() {
		return dataSource;
	}
	/**
	 * 获取jdbcTemplate
	 * <li>该属性对象是：Spring NamedParameterJdbcTemplate</li>
	 * @return
	 */
	public NamedParameterJdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	/**
	 * 获取Spring JdbcTemplate
	 * <li>该属性对象是：Spring JdbcTemplate</li>
	 * @return
	 */
	public JdbcOperations getClassicJdbcTemplate() {
		return classicJdbcTemplate;
	}
	
	/**
	 * 获取SpringJdbcInsert
	 * @param tableName
	 * @param idColumnNames
	 * @return
	 */
	private SimpleJdbcInsert getInsert(String tableName, String... idColumnNames) {
	    SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(tableName);
	    // 存在id列表
		if(null != idColumnNames && idColumnNames.length > 0) {
			jdbcInsert.usingGeneratedKeyColumns(idColumnNames);
		}
		return jdbcInsert;
	}
	
	/**
	 * 通过SQL语句或SQL模板id和条件，返回对应的SQL语句
	 * @param sqlOrId   为空，则默认为三个前缀符。如：$$$
	 * @param condition
	 * @return
	 */
	protected String generateSql(String sqlOrId, Condition condition) {
	    return sqlBuilder.sql(sqlOrId, condition);
	}
	
	/**
	 * 保存目标对象
	 * 
	 * @param target
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	public <T> Serializable save(JpaEntity jpaEntity, T target) {
		//是否包含主键
		if(!jpaEntity.existId()) {
			saveNotReturnKey(jpaEntity, target);
			return null;
		}
		return saveReturnKey(jpaEntity, target);
	}
	
	/**
	 * 保存实体对象并返回主键值
	 * <li>该方法用于含有主键的情况，如果对应类无主键，会抛出异常</li>
	 * @param jpaEntity
	 * @param target
	 * @return
	 */
	public <T> Serializable saveReturnKey(JpaEntity jpaEntity, T target) {
		ColumnMetaData idColumn = jpaEntity.getIdColumn();
		if(!jpaEntity.existId()) {
			throw new UnsupportedOperationException(Strings.format("类{}未设置主键，不支持该操作！", target.getClass()));
		}
		final String tableName = jpaEntity.getTableName();
		//获取对象数据库字段及对应值
		Map<String, Object> parameters = jpaEntity.columnDataMapper(target, false);
		
		Number key = getInsert(tableName, idColumn.getColumnName()).executeAndReturnKey(parameters);
		//根据id类型进行转换
		Class<?> idType = idColumn.getType();
		if(idType == int.class || idType == Integer.class) {
			key = key.intValue();
		} else if(idType == long.class || idType == Long.class) {
			key = key.longValue();
		}
		Reflects.writeField(target, idColumn.getField(), key);
		return key;
	}
	
	/**
	 * 保存实体对象但不返回主键值，该方法用于不含有主键的情况
	 * @param target
	 * @return   返回影响数据的行数
	 */
	public <T> int saveNotReturnKey(JpaEntity jpaEntity, T target) {
		final String tableName = jpaEntity.getTableName();
		//获取对象数据库字段及对应值
		Map<String, Object> parameters = jpaEntity.columnDataMapper(target, false);
		return getInsert(tableName).execute(parameters);
	}
	
	/**
	 * 批量保存实体对象
	 * @param jpaEntity
	 * @param targets
	 * @return
	 */
	public <T> int [] batchSave(JpaEntity jpaEntity, T[] targets) {
	    if(Arrays.isEmpty(targets)) {
	        return new int[0];
	    }
	    int length = targets.length;
	    Condition[] batchParameters = new Condition[length];
	    for(int i = 0; i < length; i ++) {
	        batchParameters[i] = new Condition().addValues(jpaEntity.columnDataMapper(targets[i], false));
	    }
	    final String tableName = jpaEntity.getTableName();
	    return getInsert(tableName).executeBatch(batchParameters);
	}
	
	/**
	 * 批量保存实体对象，可以设置每次处理的数量
	 * @param jpaEntity
	 * @param targets
	 * @param buffer
	 * @return
	 */
	public <T> int [] batchSave(JpaEntity jpaEntity, T[] targets, int buffer) {
	    if(Arrays.isEmpty(targets)) {
	        return new int[0];
	    }
	    final int length = targets.length;
	    if(0 >= buffer || buffer >= length) {
	        return batchSave(jpaEntity, targets);
	    }
	    int[] retValues = new int[length];
	    for(int i = 0; i < length; i += buffer) {
	        T[] array = Arrays.subarray(targets, i, i + buffer);
	        int[] batchValues = batchSave(jpaEntity, array);
	        System.arraycopy(batchValues, 0, retValues, i, batchValues.length);
	    }
	    return retValues;
	}
	
	/**
	 * 调用插入、更新或删除的SQL语句
	 * @param sqlOrId   SQL语句或对应SQL模板中的id
	 * @param condition   动态条件
	 * @return
	 */
	public int update(String sqlOrId, Condition condition) {
	    return jdbcTemplate.update(generateSql(sqlOrId, condition), condition);
	}
	
	/**
	 * 调用插入、更新或删除的SQL语句
	 * @param sqlOrId   SQL语句或对应SQL模板中的id
	 * @param args
	 * @return
	 */
	public int update(String sqlOrId, Object... args) {
		return classicJdbcTemplate.update(generateSql(sqlOrId, null), args);
	}
	
	/**
	 * 更新实体
	 * @param jpaEntity
	 * @param target
	 * @param columns
	 * @return
	 */
	public <T> int update(JpaEntity jpaEntity, T target, String... columns) {
		ColumnMetaData idColumn = jpaEntity.getIdColumn();
		if(!jpaEntity.existId()) {
			throw new UnsupportedOperationException(Strings.format("类{}未设置主键，不支持该操作！", target.getClass()));
		}
		final String tableName = jpaEntity.getTableName(),
				idColumnName = idColumn.getColumnName();
		//获取对象数据库字段及对应值
		Map<String, Object> parameters = jpaEntity.columnDataMapper(target, true, columns);
		StringBuilder sqlBuilder = new StringBuilder()
				.append("UPDATE ")
				.append(tableName)
				.append(" SET ");
		for(String column : parameters.keySet()) {
			sqlBuilder.append(column).append("=:").append(column).append(Chars.COMMA);
		}
		sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
		sqlBuilder.append(" WHERE ")	.append(idColumnName).append("=:").append(idColumnName);
		// 添加主键
		parameters.put(idColumnName, jpaEntity.getIdValue(target));
		return jdbcTemplate.update(sqlBuilder.toString(), parameters);
	}
	
	/**
	 * 通过sqlOrId、结果类型、动态条件，获取单个结果
	 * @param sqlOrId   SQL语句或对应SQL模板中的id
	 * @param targetClass   目标类的类型
	 * @param condition   动态条件
	 * @return
	 */
	public <T> T queryForObject(String sqlOrId, Class<T> targetClass, Condition condition) {
	    return queryForObject(sqlOrId, Jdbcs.getRowMapperByClass(targetClass), condition);
	}
	
	/**
	 * 通过sqlOrId、结果类型、参数，获取单个结果
	 * @param sqlOrId   SQL语句或对应SQL模板中的id
	 * @param targetClass   目标类的类型
	 * @param args   参数
	 * @return
	 */
	public <T> T queryForObject(String sqlOrId, Class<T> targetClass, Object... args) {
	    return queryForObject(sqlOrId, Jdbcs.getRowMapperByClass(targetClass), args);
	}
	
	/**
	 * 通过sqlOrId、RowMapper、动态参数，获取单个结果
	 * @param sqlOrId   SQL语句或对应SQL模板中的id
	 * @param rowMapper
	 * @param condition
	 * @return
	 */
	public <T> T queryForObject(String sqlOrId, RowMapper<T> rowMapper, Condition condition) {
	    try {
	        return jdbcTemplate.queryForObject(generateSql(sqlOrId, condition), condition, rowMapper);
	    } catch(EmptyResultDataAccessException e) {
	        return null;
	    }
	    
	}
	
	/**
	 * 通过sqlOrId、RowMapper、参数，获取单个结果
	 * @param sqlOrId   SQL语句或对应SQL模板中的id
	 * @param rowMapper
	 * @param args
	 * @return 返回单个对象，如果查找不到数据则返回null
	 */
	public <T> T queryForObject(String sqlOrId, RowMapper<T> rowMapper, Object... args) {
	    try {
	        return classicJdbcTemplate.queryForObject(generateSql(sqlOrId, null), rowMapper, args);
	    } catch (EmptyResultDataAccessException e) {
	        return null;
        }
	}
	
	/**
     * 通过sqlOrId、结果类型、动态条件，获取结果列表
     * @param sqlOrId   SQL语句或对应SQL模板中的id
     * @param targetClass   目标类的类型
     * @param condition
     * @return
     */
    public <T> List<T> query(String sqlOrId, Class<T> targetClass, Condition condition) {
        return query(sqlOrId, Jdbcs.getRowMapperByClass(targetClass), condition);
    }
    
	/**
	 * 通过sqlOrId、结果类型、参数，获取结果列表
	 * @param sqlOrId   SQL语句或对应SQL模板中的id
	 * @param targetClass
	 * @param args
	 * @return
	 */
	public <T> List<T> query(String sqlOrId, Class<T> targetClass, Object... args) {
	    return query(sqlOrId, Jdbcs.getRowMapperByClass(targetClass), args);
	}
	
	/**
	 * 通过sqlOrId、结果类型、动态条件，获取结果列表
	 * @param sqlOrId
	 * @param rowMapper
	 * @param condition
	 * @return
	 */
	public <T> List<T> query(String sqlOrId, RowMapper<T> rowMapper, Condition condition) {
        return jdbcTemplate.query(generateSql(sqlOrId, condition), condition, rowMapper);
    }
	
	/**
	 * 通过sqlOrId、结果类型、参数，获取结果列表
	 * @param sqlOrId   SQL语句或对应SQL模板中的id
	 * @param rowMapper
	 * @param args
	 * @return
	 */
	public <T> List<T> query(String sqlOrId, RowMapper<T> rowMapper, Object... args) {
	    return classicJdbcTemplate.query(generateSql(sqlOrId, null), rowMapper, args);
	}
	
	/**
	 * 通过设置当前页和每页数量，获取分页结果
	 * @param sqlOrId   执行的sql或对应模板中的id
	 * @param targetClass   返回的目标类class
	 * @param flowMode 是否以流式进行分页(流式分页，不返回总记录数和总页数，仅能判断是否存在下一页。)
	 * @param pageIndex   分页的页码数
	 * @param pageSize   分页的每页记录数
	 * @param condition   查询条件的封装类
	 * @return
	 */
	public <T> Page<T> queryPageByIndex(String sqlOrId, Class<T> targetClass, boolean flowMode, int pageIndex, int pageSize, Condition condition) {
	    return queryPageByIndex(sqlOrId, Jdbcs.getRowMapperByClass(targetClass), flowMode, pageIndex, pageSize, condition);
	}
	
	/**
	 * 通过设置当前页和每页数量，获取分页结果
	 * @param sqlOrId   执行的sql或对应模板中的id
	 * @param targetClass   返回的目标类class
	 * @param flowMode   是否以流式进行分页(流式分页，不返回总记录数和总页数，仅能判断是否存在下一页。)
	 * @param pageIndex   分页的页码数
	 * @param pageSize   分页的每页记录数
	 * @param args   查询条件的参数列表
	 * @return
	 */
	public <T> Page<T> queryPageByIndex(String sqlOrId, Class<T> targetClass, boolean flowMode, int pageIndex, int pageSize, Object... args) {
	    return queryPageByIndex(sqlOrId, Jdbcs.getRowMapperByClass(targetClass), flowMode, pageIndex, pageSize, args);
	}
	
	/**
	 * 通过设置当前页和每页数量，获取分页结果
	 * @param sqlOrId   执行的sql或对应模板中的id
	 * @param rowMapper
	 * @param flowMode   是否以流式进行分页(流式分页，不返回总记录数和总页数，仅能判断是否存在下一页。)
	 * @param pageIndex   分页的页码数
	 * @param pageSize   分页的每页记录数
	 * @param condition   查询条件的封装类
	 * @return
	 */
	public <T> Page<T> queryPageByIndex(String sqlOrId, RowMapper<T> rowMapper, boolean flowMode, int pageIndex, int pageSize, Condition condition) {
	    pageIndex = pageIndex <= 0 ? 1 : pageIndex;
        pageSize = pageSize <= 0 ? Page.DEFAULT_PAGE_SIZE : pageSize;
        int start = (pageIndex - 1) * pageSize, limit = pageSize;
        return queryPage(sqlOrId, rowMapper, flowMode, start, limit, condition);
	}
	
	/**
	 * 通过设置当前页和每页数量，获取分页结果
	 * @param sqlOrId   执行的sql或对应模板中的id
	 * @param rowMapper
	 * @param flowMode   是否以流式进行分页(流式分页，不返回总记录数和总页数，仅能判断是否存在下一页。)
	 * @param pageIndex   分页的页码数
	 * @param pageSize   分页的每页记录数
	 * @param args   查询条件的参数列表
	 * @return
	 */
	public <T> Page<T> queryPageByIndex(String sqlOrId, RowMapper<T> rowMapper, boolean flowMode, int pageIndex, int pageSize, Object... args) {
	    pageIndex = pageIndex <= 0 ? 1 : pageIndex;
        pageSize = pageSize <= 0 ? Page.DEFAULT_PAGE_SIZE : pageSize;
        int start = (pageIndex - 1) * pageSize, limit = pageSize;
        return queryPage(sqlOrId, rowMapper, flowMode, start, limit, args);
	}
	
	/**
	 * 分页获取结果列表
	 * @param sqlOrId   执行的sql或对应模板中的id
	 * @param targetClass   返回的目标类class
	 * @param flowMode   是否以流式进行分页(流式分页，不返回总记录数和总页数，仅能判断是否存在下一页。)
	 * @param start   分页查询起始的偏移量
	 * @param limit   分页的每页数量
	 * @param condition   查询条件的封装类
	 * @return
	 */
	public <T> Page<T> queryPage(String sqlOrId, Class<T> targetClass, boolean flowMode, int start, int limit, Condition condition) {
	    return queryPage(sqlOrId, Jdbcs.getRowMapperByClass(targetClass), flowMode, start, limit, condition);
	}
	
	/**
	 * 分页获取结果列表
	 * @param sqlOrId   SQL语句或对应SQL模板中的id
	 * @param targetClass   返回的目标类class
	 * @param flowMode   是否以流式进行分页(流式分页，不返回总记录数和总页数，仅能判断是否存在下一页。)
	 * @param start   分页查询起始的偏移量
	 * @param limit   分页的每页数量
	 * @param args   查询条件的参数列表
	 * @return
	 */
	public <T> Page<T> queryPage(String sqlOrId, Class<T> targetClass, boolean flowMode, int start, int limit, Object... args) {
	    return queryPage(sqlOrId, Jdbcs.getRowMapperByClass(targetClass), flowMode, start, limit, args);
	}
	
	/**
	 * 分页获取结果列表
	 * @param sqlOrId   执行的sql或对应模板中的id
	 * @param rowMapper
	 * @param flowMode   是否以流式进行分页(流式分页，不返回总记录数和总页数，仅能判断是否存在下一页。)
	 * @param start   分页查询起始的偏移量
	 * @param limit   分页的每页数量
	 * @param condition   查询条件的封装类
	 * @return
	 */
	public <T> Page<T> queryPage(String sqlOrId, RowMapper<T> rowMapper, final boolean flowMode, int start, int limit, Condition condition) {
	    return _queryPage(generateSql(sqlOrId, condition), rowMapper, flowMode, start, limit, condition);
	}
	
	/**
	 * 分页获取结果列表
	 * @param sqlOrId   执行的sql或对应模板中的id
	 * @param rowMapper
	 * @param flowMode   是否以流式进行分页(流式分页，不返回总记录数和总页数，仅能判断是否存在下一页。)
	 * @param start   分页查询起始的偏移量
	 * @param limit   分页的每页数量
	 * @param args   查询条件的参数列表
	 * @return
	 */
	public <T> Page<T> queryPage(String sqlOrId, RowMapper<T> rowMapper, final boolean flowMode, int start, final int limit, Object... args) {
	    return _queryPage(generateSql(sqlOrId, null), rowMapper, flowMode, start, limit, args);
	}
	
	/**
	 * 内部实际调用查询sql返回结果
	 * @param sql   执行的sql内容
	 * @param rowMapper
	 * @param flowMode   是否以流式进行分页(流式分页，不返回总记录数和总页数，仅能判断是否存在下一页。)
	 * @param start   分页查询起始的偏移量
	 * @param limit   分页的每页数量
	 * @param arg   查询条件
	 * @return
	 */
	private <T> Page<T> _queryPage(String sql, RowMapper<T> rowMapper, final boolean flowMode, final int start, final int limit, Object arg) {
	    Page<T> page = new Page<T>(flowMode, start / limit + 1, limit);
	    if(null == arg) {
	        arg = new Object[0];
	    }
	    StringBuilder limitSqlBuilder =  new StringBuilder();
	    // sql 主体内容
	    if(flowMode) {
	        limitSqlBuilder.append(sql);
	    } else {
	        limitSqlBuilder.append("SELECT SQL_CALC_FOUND_ROWS ").append(Jdbcs.removeFirstSelect(sql));
	    }
	    // sql 分页限定
        if(start > 0 || limit > 0) {
            limitSqlBuilder.append(" LIMIT ");
            int sqlLimit = limit;
            if(flowMode) {
                sqlLimit ++;
            }
            if(start > 0) {
                limitSqlBuilder.append(start).append(",").append(sqlLimit);
            } else {
                limitSqlBuilder.append(sqlLimit);
            }
        }
        final String limitSql = limitSqlBuilder.toString();
        List<T> items;
        if(arg instanceof Condition) {
            items = jdbcTemplate.query(limitSql, (Condition)arg, rowMapper);
        } else {
            items = classicJdbcTemplate.query(limitSql, rowMapper, Arrays.wrap(arg));
        }
        if(flowMode) {
            int size = (null == items) ? 0 : items.size();
            page.setHasNext(size > limit);
            if(size > limit) {
                items.subList(limit, size).clear();
            }
            page.setItems(items);
        } else {
            final int total = classicJdbcTemplate.queryForObject(COUNT_SQL, Integer.class);
            page.setItems(CollectionUtil.emptyListIfNull(items));
            page.setTotalCount(total);
        }        
        return page;
	}
}
