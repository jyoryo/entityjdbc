package com.jyoryo.entityjdbc.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.jyoryo.entityjdbc.common.io.IOs;

/**
 * 文件解压工具
 * @author jyoryo
 *
 */
public class FileExtracts {
    private static final int BUFFER_SIZE = 4096;
    
    /**
     * 解压zip文件，并移至目标文件夹下
     * @param input
     * @param targetDir
     * @return   返回压缩包中的具体文件列表，排除文件夹
     */
    public static List<String> extractZip(InputStream input, String targetDir) {
        List<String> fileNames = new ArrayList<>();
        ZipInputStream is = new ZipInputStream(new BufferedInputStream(input));
        ZipEntry entry;
        try {
            while ((entry = is.getNextEntry()) != null) {
                String name = entry.getName();
                if (entry.isDirectory()) {
                    mkDirs(targetDir, name);
                } else {
                    fileNames.add(name);
                    String dir = directoryPart(name);
                    if (dir != null) {
                        mkDirs(targetDir, dir);
                    }
                    extractFile(is, targetDir, name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOs.close(is);
        }
        return fileNames;
    }
    
    public static List<String> extractZip(File zipFile, String targetDir) throws FileNotFoundException {
        return extractZip(new FileInputStream(zipFile), targetDir);
    }
    
    public static List<String> extractZip(String zipFile, String targetDir) throws FileNotFoundException {
        return extractZip(new File(zipFile), targetDir);
    }
    
    private static void extractFile(InputStream inputStream, String targetDir, String name) throws IOException {
        int count = -1;
        byte buffer[] = new byte[BUFFER_SIZE];
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(targetDir, name)), BUFFER_SIZE);
        while ((count = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
            out.write(buffer, 0, count);
        }
        out.close();
    }

    private static void mkDirs(String parent, String child) {
        File d = new File(parent, child);
        if (!d.exists()) {
            d.mkdirs();
        }
    }
    
    private static String directoryPart(String name) {
        int s = name.lastIndexOf(File.separatorChar);
        return s == -1 ? null : name.substring(0, s);
    }
}
