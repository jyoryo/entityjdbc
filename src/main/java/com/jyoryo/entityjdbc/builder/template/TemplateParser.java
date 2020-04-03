package com.jyoryo.entityjdbc.builder.template;

import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 模板接口
 * @author jyoryo
 */
public interface TemplateParser {
    Charset encoding = StandardCharsets.UTF_8;
    
    /**
     * 将模板解析并将结果输出到Writer
     * 
     * @param tmplContext   模板内容
     * @param model 绑定的参数，此Map中的参数会替换模板中的变量
     * @param writer 输出
     */
    void render(String tmplContext, Map<?, ?> model, Writer writer);

    /**
     * 将模板解析并将结果返回为字符串
     * 
     * @param tmplContext   模板内容
     * @param model 绑定的参数，此Map中的参数会替换模板中的变量
     * @return 解析后的内容
     */
    String render(String tmplContext, Map<?, ?> model);
}
