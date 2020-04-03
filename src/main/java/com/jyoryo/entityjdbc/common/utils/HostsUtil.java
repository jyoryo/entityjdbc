package com.jyoryo.entityjdbc.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.common.Systems;
import com.jyoryo.entityjdbc.common.Validates;
import com.jyoryo.entityjdbc.common.io.Files;
import com.jyoryo.entityjdbc.common.io.IOs;
import com.jyoryo.entityjdbc.common.log.Logs;

/**
 * OS系统hosts文件操作工具类
 * 解析hosts文件参考：https://alvinalexander.com/java/jwarehouse/netty-4.1/resolver/src/main/java/io/netty/resolver/HostsFileParser.java.shtml
 * @author jyoryo
 *
 */
public class HostsUtil {
    private static final String WINDOWS_DEFAULT_SYSTEM_ROOT = "C:\\Windows";
    private static final String WINDOWS_HOSTS_FILE_RELATIVE_PATH = "\\system32\\drivers\\etc\\hosts";
    private static final String X_PLATFORMS_HOSTS_FILE_PATH = "/etc/hosts";
    
    private static final Pattern WHITESPACES = Pattern.compile("[ \t]+");
    
    private HostsUtil() {
    }
    
    /**
     * 定位获取hosts文件
     * @return
     */
    public static File locateHostsFile() {
        File hostsFile;
        if(Systems.IS_OS_WINDOWS) {
            hostsFile = new File(System.getenv("SystemRoot") + WINDOWS_HOSTS_FILE_RELATIVE_PATH);
            if (!hostsFile.exists()) {
                hostsFile = new File(WINDOWS_DEFAULT_SYSTEM_ROOT + WINDOWS_HOSTS_FILE_RELATIVE_PATH);
            }
        } else {
            hostsFile = new File(X_PLATFORMS_HOSTS_FILE_PATH);
        }
        return hostsFile;
    }
    
    /**
     * 解析获取hosts内容
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Map<String, InetAddress> parse() throws IOException {
        File file = locateHostsFile();
        return parse(new FileReader(file));
    }
    
    private static Map<String, InetAddress> parse(Reader reader) throws IOException {
        BufferedReader buff = new BufferedReader(reader);
        try {
            Map<String, InetAddress> entries = new LinkedHashMap<>();
            String line;
            while (null != (line = buff.readLine())) {
                // remove comment
                int commentPosition = line.indexOf('#');
                if (commentPosition != -1) {
                    line = line.substring(0, commentPosition);
                }
                // skip empty lines
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // split
                List<String> lineParts = new ArrayList<>();
                for (String s: WHITESPACES.split(line)) {
                    if (!s.isEmpty()) {
                        lineParts.add(s);
                    }
                }

                // a valid line should be [IP, hostname, alias*]
                if (lineParts.size() < 2) {
                    // skip invalid line
                    continue;
                }

                String ip = lineParts.get(0);

                if (!Validates.isIpv4(ip) && !Validates.isIpv6(ip)) {
                    // skip invalid IP
                    continue;
                }

                // loop over hostname and aliases
                for (int i = 1; i < lineParts.size(); i ++) {
                    String hostname = lineParts.get(i);
                    String hostnameLower = hostname.toLowerCase(Locale.ENGLISH);
                    if (!entries.containsKey(hostnameLower)) {
                        // trying to map a host to multiple IPs is wrong
                        // only the first entry is honored                    
                        entries.put(hostnameLower, InetAddress.getByName(ip));
                    }
                }
            }
            return entries;
        } finally {
            IOs.close(buff);
        }
        
    }
    
    /**
     * 向hosts文件添加本地记录
     * @param hostname
     * @param ip
     * @throws IOException 
     */
    public static void addHostname(String hostname, String ip) throws IOException {
        Map<String, String> hostnames = new HashMap<>();
        hostnames.put(hostname, ip);
        addHostname(hostnames);
    }
    
    /**
     * 向hosts文件添加本地记录的安全方法
     * @param hostname
     * @param ip
     */
    public static void addHostnameQuietly(String hostname, String ip) {
        try {
            addHostname(hostname, ip);
        } catch (Exception e) {
            Logs.error("", e);
        }
    }
    
    /**
     * 向hosts文件添加本地记录
     * @param hostnames   批量添加的host记录。Key:hostname Value:IP
     * @throws IOException 
     */
    public synchronized static void addHostname(Map<String, String> hostnames) throws IOException {
        if(null == hostnames || hostnames.isEmpty()) {
            return ;
        }
        // 处理数据
        Map<String, String> validHostnames = new HashMap<>();   // 有效的hostname。Key:hostname Value:ip
        for(String hostname : hostnames.keySet()) {
            String ip = hostnames.get(hostname);
            if(Strings.isBlank(hostname) || Strings.isBlank(ip) || (!Validates.isIpv4(ip) && !Validates.isIpv6(ip))) {
                continue ;
            }
            validHostnames.put(Strings.lowerCase(hostname.trim()), ip);
        }
        
        File hostsFile = locateHostsFile();
        List<String> lines = Files.readLines(hostsFile, StandardCharsets.UTF_8),
                newLines = new ArrayList<>();
        for(String line : lines) {
            int commentPosition = line.indexOf('#');
            String info = line;
            // 存在注释，获取注释前面的内容
            if(-1 != commentPosition) {
                info = line.substring(0, commentPosition);
            }
            info = Strings.trim(info);
            if(Strings.isBlank(info)) {
                newLines.add(line);
                continue ;
            }
            String [] infoParts = WHITESPACES.split(info);
            if(2 > infoParts.length) {
                newLines.add(line);
                continue ;
            }
            String infoIp = infoParts[0],
                    infoHostname = Strings.lowerCase(infoParts[1]);
            // 存在该hostname
            if(validHostnames.containsKey(infoHostname)) {
                String ip = validHostnames.get(infoHostname);
                // IP 不同，进行替换
                if(!Strings.equals(infoIp, ip)) {
                    line = Strings.replaceOnce(line, infoIp, ip);
                }
                newLines.add(line);
                validHostnames.remove(infoHostname);
            } else {
                newLines.add(line);
            }
        }   // end for lines
        
        // 添加新记录
        if(null != validHostnames && validHostnames.size() > 0) {
            for(String addHostname : validHostnames.keySet()) {
                newLines.add(new StringBuilder().append(validHostnames.get(addHostname)).append("    ").append(Strings.lowerCase(addHostname)).toString());
            }
            lines.add(System.lineSeparator());
        }
        // 写入文件
        Files.writeLines(hostsFile, newLines);
    }

    public static void addHostnameQuietly(Map<String, String> hostnames) {
        try {
            addHostname(hostnames);
        } catch (Exception e) {
            Logs.error("", e);
        }
    }
}
