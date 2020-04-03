package com.jyoryo.entityjdbc.common.utils;

import javax.servlet.http.HttpServletRequest;

import com.jyoryo.entityjdbc.common.Strings;

/**
 * Servlet 工具类
 * @author jyoryo
 *
 */
public class Servlets {
    
    /**
     * 获取客户端IP
     * @param request
     * @param otherHeaderNames
     * @return
     */
    public static String getClientIp(HttpServletRequest request) {
        String[] headers = { "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR" };
        String ip;
        for (String header : headers) {
            ip = request.getHeader(header);
            if (false == isUnknow(ip)) {
                return getMultistageReverseProxyIp(ip);
            }
        }

        ip = request.getRemoteAddr();
        return getMultistageReverseProxyIp(ip);
    }
    
    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关
     * @param checkString
     * @return
     */
    private static boolean isUnknow(String checkString) {
        return Strings.isBlank(checkString) || Strings.equalsIgnoreCase("unknown", checkString);
    }
    
    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     * 
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    private static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(",") > 0) {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips) {
                if (false == isUnknow(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }
}
