package com.jyoryo.entityjdbc.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jyoryo.entityjdbc.EntityDao;
import com.jyoryo.entityjdbc.JpaEntity;
import com.jyoryo.entityjdbc.common.Reflects;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.support.Condition;
import com.jyoryo.entityjdbc.support.Page;

/**
 * 基于jdbc的DAO
 * @author jyoryo
 *
 * @param <T>
 */
public abstract class JdbcEntityDao<T> implements EntityDao<T> {
	private JdbcDao jdbcDao;
	/**
	 * jdbcDao别名
	 */
	protected JdbcDao dao;
	protected final Class<T> targetClass;
	protected final JpaEntity jpaEntity;

	public JdbcEntityDao() {
		super();
		this.targetClass = Reflects.getSuperClassGenricType(getClass());
		this.jpaEntity = new JpaEntity(targetClass);
	}

	/**
	 * 自动注入JdbcDao
	 * @param jdbcDao
	 */
	@Autowired
	public void setJdbcDao(JdbcDao jdbcDao) {
		this.jdbcDao = jdbcDao;
		this.dao = jdbcDao;
	}

	@Override
	public T get(Serializable id) {
		String sql = Strings.format("SELECT * FROM {} WHERE {} = ?",
				jpaEntity.getTableName(),
				jpaEntity.getIdColumnName());
		return queryForObject(sql, id);
	}
	
	/**
	 * 通过动态条件获取泛型对象
	 * @param sqlOrId
	 * @param condition
	 * @return
	 */
	protected T queryForObject(String sqlOrId, Condition condition) {
	    return jdbcDao.queryForObject(sqlOrId, targetClass, condition);
	}
	
	/**
	 * 通过可变参数条件获取泛型对象
	 * @param sql
	 * @param args
	 * @return
	 */
	protected T queryForObject(String sqlOrId, Object... args) {
		return jdbcDao.queryForObject(sqlOrId, targetClass, args);
	}
	
	/**
	 * 通过动态条件获取泛型对象列表
	 * @param sqlOrId
	 * @param condition
	 * @return
	 */
	protected List<T> query(String sqlOrId, Condition condition) {
	    return jdbcDao.query(sqlOrId, targetClass, condition);
	}
	
	/**
	 * 通过可变参数条件获取泛型对象列表
	 * @param sql
	 * @param args
	 * @return
	 */
	protected List<T> query(String sqlOrId, Object... args) {
		return jdbcDao.query(sqlOrId, targetClass, args);
	}
	
	/**
	 * 通过动态条件分页获取泛型对象列表
	 * @param sqlOrId   执行的sql或对应模板中的id
	 * @param flowMode   是否以流式进行分页(流式分页，不返回总记录数和总页数，仅能判断是否存在下一页。)
	 * @param start   分页查询起始的偏移量
	 * @param limit   分页的每页数量
	 * @param condition   查询条件的封装类
	 * @return
	 */
	protected Page<T> queryPage(String sqlOrId, boolean flowMode, int start, int limit, Condition condition) {
	    return jdbcDao.queryPage(sqlOrId, targetClass, flowMode, start, limit, condition);
	}
	
	/**
	 * 通过可变参数条件分页获取泛型对象列表
	 * @param sqlOrId   执行的sql或对应模板中的id
	 * @param flowMode   是否以流式进行分页(流式分页，不返回总记录数和总页数，仅能判断是否存在下一页。)
	 * @param start   分页查询起始的偏移量
	 * @param limit   分页的每页数量
	 * @param args   查询条件的参数列表
	 * @return
	 */
	protected Page<T> queryPage(String sqlOrId, boolean flowMode, int start, int limit, Object... args) {
	    return jdbcDao.queryPage(sqlOrId, targetClass, flowMode, start, limit, args);
	}

	@Override
	public List<T> getAll() {
		String sql = Strings.format("SELECT * FROM {}", jpaEntity.getTableName());
		return jdbcDao.query(sql, targetClass);
	}

	@Override
	public Serializable save(T target) {
		return jdbcDao.save(jpaEntity, target);
	}

	@Override
	public int saveNotReturnKey(T target) {
		return jdbcDao.saveNotReturnKey(jpaEntity, target);
	}

	/**
	 * 批量保存实体对象
	 * @param targets
	 * @return
	 */
	public int [] batchSave(T [] targets) {
	    return jdbcDao.batchSave(jpaEntity, targets);
	}
	
	/**
	 * 批量保存实体对象，可以设置每次处理的数量
	 * @param targets
	 * @param buffer
	 * @return
	 */
	public int [] batchSave(T [] targets, int buffer) {
	    return jdbcDao.batchSave(jpaEntity, targets, buffer);
	}

	@Override
	public int update(T target, String... columns) {
		return jdbcDao.update(jpaEntity, target, columns);
	}

	@Override
	public void saveOrUpdate(T target) {
		Object id = jpaEntity.getIdValue(target);
		if(null != id && null != get((Serializable)id)) {
			update(target);
			return ;
		}	
		save(target);
	}

	@Override
	public int delete(T target) {
		return deleteById((Serializable)jpaEntity.getIdValue(target));
	}

	@Override
	public int deleteById(Serializable id) {
		if(!jpaEntity.existId()) {
			throw new IllegalArgumentException(Strings.format("{}不存在主键，无法执行该操作！", targetClass));
		}
		String sql = Strings.format("DELETE FROM {} WHERE {} = ?",
				jpaEntity.getTableName(),
				jpaEntity.getIdColumn().getColumnName());
		return jdbcDao.update(sql, id);
	}
}
