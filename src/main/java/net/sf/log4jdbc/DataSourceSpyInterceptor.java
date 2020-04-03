package net.sf.log4jdbc;

import org.aopalliance.intercept.Joinpoint;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.sql.Connection;

/**
 * DataSource用于打印执行sql语句的拦截器
 * @author jyoryo
 *
 */
public class DataSourceSpyInterceptor implements MethodInterceptor {
	private RdbmsSpecifics rdbmsSpecifics = null;
	
	private RdbmsSpecifics getRdbmsSpecifics(Connection conn) {
		if (rdbmsSpecifics == null) {
			rdbmsSpecifics = DriverSpy.getRdbmsSpecifics(conn);
		}
		return rdbmsSpecifics;
	}
	
	/**
	 * Implement this method to perform extra treatments before and
	 * after the invocation. Polite implementations would certainly
	 * like to invoke {@link Joinpoint#proceed()}.
	 *
	 * @param invocation the method invocation joinpoint
	 * @return the result of the call to {@link Joinpoint#proceed()};
	 * might be intercepted by the interceptor
	 * @throws Throwable if the interceptors or the target object
	 *                   throws an exception
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object result = invocation.proceed();
		if (SpyLogFactory.getSpyLogDelegator().isJdbcLoggingEnabled()) {
			if (result instanceof Connection) {
				Connection conn = (Connection) result;
				return new ConnectionSpy(conn, getRdbmsSpecifics(conn));
			}
		}
		return result;
	}
}
