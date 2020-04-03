package com.jyoryo.entityjdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于MapBean及子类，作用类似Vue中的计算属性。
 * <li>用于JavaBean的getter方法上</li>
 * 例如：
 * <pre>
 * public class Bean {
 *    private String param1;
 *    private boolean param2;
 *    
 *    public String getParam1() {
 *       return param1;
 *    }
 *    
 *    public void setParam1(String param1) {
 *       this.param1 = param1;
 *    }
 *    
 *    public boolean isParam2() {
 *       return param2;
 *    }
 *    
 *    public void setParam2(boolean param2) {
 *       this.param2 = param2;
 *    }
 *    
 *    <code>@ComputedMethod</code>
 *    public String getParam2Length() {
 *       return (null == param2) ? 0 : param2.length();
 *    }
 * }
 * </pre>
 * @author jyoryo
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ComputedMethod {

    public boolean value() default true;
}
