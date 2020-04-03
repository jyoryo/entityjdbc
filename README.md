# Entity Jdbc
基于Spring Jdbc，使用JPA注解，实现类似Hibernate效果，采用模板技术sql脚本分离。

特点：
1、直接使用SpringJdbc，简单方便，而且支持类似Hibernate ORM框架保存实体至数据库；
2、通过hibernate-maven-plugin插件自动生成数据库Schema；
3、通过模板技术，实现SQL语句进行分离，动态生成SQL，解决Java中拼接SQL语句易错、不直观的问题；
4、使用AOP拦截器，支持控制台显示实际调用执行的SQL语句及执行时间，方便调试、调优。