package com.jyoryo.entityjdbc.builder.template;

import java.io.StringWriter;
import java.util.Map;

/**
 * 抽象模板，提供将模板融合后写出到文件、返回字符串等方法
 * @author jyoryo
 *
 */
public abstract class AbstractTemplateParser implements TemplateParser{
	
	@Override
	public String render(String tmplContext, Map<?, ?> model) {
		final StringWriter writer = new StringWriter();
		render(tmplContext, model, writer);
		return writer.toString();
	}
}
