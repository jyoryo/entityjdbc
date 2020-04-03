package com.jyoryo.entityjdbc.builder.template.impl;

import java.io.StringReader;
import java.io.Writer;
import java.util.Map;

import com.jyoryo.entityjdbc.builder.template.AbstractTemplateParser;
import com.jyoryo.entityjdbc.builder.template.TemplateParser;
import com.jyoryo.entityjdbc.exception.TemplateParserException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * Freemarker模板
 * @author jyoryo
 *
 */
public class FreemarkerTemplateParser extends AbstractTemplateParser implements TemplateParser {
    private static Configuration cfg;
    private static final String DEFAULT_NAME = "freemarker_template_parser";
    static {
        cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setDefaultEncoding(encoding.name());
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
    }

    private String name;

    public FreemarkerTemplateParser() {
        this(DEFAULT_NAME);
    }

    /**
     * 设定模板名称构造Freemarker模板解析器
     * @param name
     */
    public FreemarkerTemplateParser(String name) {
        super();
        this.name = name;
    }

    @Override
    public void render(String tmplContext, Map<?, ?> model, Writer writer) {
        Template template;
        try {
            template = new Template(name, new StringReader(tmplContext), cfg);
            template.process(model, writer);
        } catch (Exception e) {
            throw new TemplateParserException("", e);
        }
    }

}
