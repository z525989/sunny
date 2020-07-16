package com.zjh.sunny.core.util;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassUtil {

    public static List<Class<?>> getClassList(String packageName, boolean isScanChildPackage) {
        List<Class<?>> list = new ArrayList<>();

        // 包名对应的路径名称
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;

        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {

                URL url = dirs.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findClassInPackageByFile(packageName, filePath, isScanChildPackage, list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static void findClassInPackageByFile(String packageName, String filePath, boolean recursive, List<Class<?>> list) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles(file -> {
            // 接受dir目录
            boolean acceptDir = recursive && file.isDirectory();
            // 接受class文件
            boolean acceptClass = file.getName().endsWith("class");
            return acceptDir || acceptClass;
        });

        if (dirFiles == null) {
            return;
        }

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, list);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    list.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
