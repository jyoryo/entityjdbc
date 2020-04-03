package com.jyoryo.entityjdbc.common;

import java.util.regex.Pattern;

/**
 * 校验器工具
 * @author jyoryo
 *
 */
public class Validates {
    /** 英文字母 、数字和下划线 */
    public final static Pattern PATTERN_GENERAL = Pattern.compile("^\\w+$");
    /** 数字 */
    public final static Pattern PATTERN_NUMBERS = Pattern.compile("\\d+");
    /** 字母 */
    public final static Pattern PATTERN_WORD = Pattern.compile("[a-zA-Z]+");
    /** 单个中文汉字 */
    public final static Pattern PATTERN_CHINESE = Pattern.compile("[\\u4E00-\\u9FFF]");
    /** 中文汉字 */
    public final static Pattern PATTERN_CHINESES = Pattern.compile("[\\u4E00-\\u9FFF]+");
    /** 分组 */
    public final static Pattern PATTERN_GROUP_VAR = Pattern.compile("\\$(\\d+)");
    /** IP v4 */
    public final static Pattern PATTERN_IPV4 = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
    /** IP v6 */
    public final static Pattern PATTERN_IPV6 = Pattern.compile("(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))");
    /** 货币 */
    public final static Pattern PATTERN_MONEY = Pattern.compile("^(\\d+(?:\\.\\d+)?)$");
    /** 邮件，符合RFC 5322规范，正则来自：http://emailregex.com/ */
    // public final static Pattern PATTERN_EMAIL = Pattern.compile("(\\w|.)+@\\w+(\\.\\w+){1,2}");
    public final static Pattern PATTERN_EMAIL = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", Pattern.CASE_INSENSITIVE);
    /** 移动电话 */
    public final static Pattern PATTERN_MOBILE = Pattern.compile("(?:0|86|\\+86)?1[3456789]\\d{9}");
    /** 18位身份证号码 */
    public final static Pattern PATTERN_CITIZEN_ID = Pattern.compile("[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(\\d|X|x)");
    /** 邮编 */
    public final static Pattern PATTERN_ZIP_CODE = Pattern.compile("[1-9]\\d{5}(?!\\d)");
    /** 生日 */
    public final static Pattern PATTERN_BIRTHDAY = Pattern.compile("^(\\d{2,4})([/\\-\\.年]?)(\\d{1,2})([/\\-\\.月]?)(\\d{1,2})日?$");
    /** URL */
    public final static Pattern PATTERN_URL = Pattern.compile("[a-zA-z]+://[^\\s]*");
    /** Http URL */
    public final static Pattern PATTERN_URL_HTTP = Pattern.compile("(https://|http://)?([\\w-]+\\.)+[\\w-]+(:\\d+)*(/[\\w- ./?%&=]*)?");
    /** 中文字、英文字母、数字和下划线 */
    public final static Pattern PATTERN_GENERAL_WITH_CHINESE = Pattern.compile("^[\u4E00-\u9FFF\\w]+$");
    /** UUID */
    public final static Pattern PATTERN_UUID = Pattern.compile("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$");
    /** 不带横线的UUID */
    public final static Pattern PATTERN_UUID_SIMPLE = Pattern.compile("^[0-9a-z]{32}$");
    /** 中国车牌号码 */
    public final static Pattern PATTERN_PLATE_NUMBER = Pattern.compile("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$");
    /** MAC地址正则 */
    public static final Pattern PATTERN_MAC_ADDRESS = Pattern.compile("((?:[A-F0-9]{1,2}[:-]){5}[A-F0-9]{1,2})|(?:0x)(\\d{12})(?:.+ETHER)", Pattern.CASE_INSENSITIVE);
    /** 16进制字符串 */
    public static final Pattern PATTERN_HEX = Pattern.compile("^[a-f0-9]+$", Pattern.CASE_INSENSITIVE);
    
    /**
     * 根据正则表达式和检测内容，进行匹配
     * @param pattern
     * @param content
     * @return   正则表达式或内容为null，直接返回false
     */
    private static boolean isMatch(Pattern pattern, CharSequence content) {
        if (content == null || pattern == null) {
            return false;
        }
        return pattern.matcher(content).matches();
    }
    
    /**
     * 验证是否是英文字母 、数字和下划线 
     * @param content
     * @return
     */
    public static boolean isGeneral(CharSequence content) {
        return isMatch(PATTERN_GENERAL, content);
    }
    
    /**
     * 验证内容是否是字母(包括大写字母和小写字母)
     * @param content
     * @return
     */
    public static boolean isWord(CharSequence content) {
        return isMatch(PATTERN_WORD, content);
    }
    
    /**
     * 验证是否为邮政编码（中国）
     * @param content
     * @return
     */
    public static boolean isZipCode(CharSequence content) {
        return isMatch(PATTERN_ZIP_CODE, content);
    }
    
    /**
     * 验证是否为可用邮箱地址
     * @param content
     * @return
     */
    public static boolean isEmail(CharSequence content) {
        return isMatch(PATTERN_EMAIL, content);
    }
    
    /**
     * 验证是否为手机号码（中国）
     * 
     * @param content 值
     * @return 是否为手机号码（中国）
     */
    public static boolean isMobile(CharSequence content) {
        return isMatch(PATTERN_MOBILE, content);
    }
    
    /**
     * 验证是否为身份证号码（18位中国）<br>
     * 出生日期只支持到到2999年
     * 
     * @param content 值
     * @return 是否为身份证号码（18位中国）
     */
    public static boolean isCitizenId(CharSequence content) {
        return isMatch(PATTERN_CITIZEN_ID, content);
    }
    
    /**
     * 验证是否为IPV4地址
     * 
     * @param content 值
     * @return 是否为IPV4地址
     */
    public static boolean isIpv4(CharSequence content) {
        return isMatch(PATTERN_IPV4, content);
    }
    
    /**
     * 验证是否为IPV6地址
     * 
     * @param content 值
     * @return 是否为IPV6地址
     */
    public static boolean isIpv6(CharSequence content) {
        return isMatch(PATTERN_IPV6, content);
    }
    
    /**
     * 验证是否为MAC地址
     * 
     * @param content 值
     * @return 是否为MAC地址
     */
    public static boolean isMac(CharSequence content) {
        return isMatch(PATTERN_MAC_ADDRESS, content);
    }
    
    /**
     * 验证是否为中国车牌号
     * 
     * @param content 值
     * @return 是否为中国车牌号
     */
    public static boolean isPlateNumber(CharSequence content) {
        return isMatch(PATTERN_PLATE_NUMBER, content);
    }
    
    /**
     * 验证是否为UUID<br>
     * 包括带横线标准格式和不带横线的简单模式
     * 
     * @param content 值
     * @return 是否为UUID
     */
    public static boolean isUUID(CharSequence content) {
        return isMatch(PATTERN_UUID, content) || isMatch(PATTERN_UUID_SIMPLE, content);
    }
}
