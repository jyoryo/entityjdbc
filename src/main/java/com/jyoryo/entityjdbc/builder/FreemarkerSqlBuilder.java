package com.jyoryo.entityjdbc.builder;

import com.jyoryo.entityjdbc.builder.template.TemplateParser;
import com.jyoryo.entityjdbc.builder.template.impl.FreemarkerTemplateParser;

/**
 * 使用Freemarker构建sql
 * @author jyoryo
 *
 */
public class FreemarkerSqlBuilder extends AbstractSqlBuilder implements SqlBuilder {
    private static final TemplateParser PARSER = new FreemarkerTemplateParser("freemarker_sqlbuilder_parser");

    @Override
    protected synchronized TemplateParser getTemplateParser() {
        return PARSER;
    }
    
    

//	public FreemarkerSqlBuilder() {
//        super();
//    }
//
//    @Override
//    public String sql(String sqlOrId, Condition condition) {
//        if(Strings.isBlank(sqlOrId)) {
//            new SqlBuilderException("sqlOrId为空！");
//        }
//        if(!sqlOrId.startsWith(String.valueOf(idPrefix))) {
//            return sqlOrId;
//        }
//        return parseTemplateId(sqlOrId);
//    }

}
