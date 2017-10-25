package com.nosuchfield.geisha;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author hourui 2017/10/18 19:37
 */
public class Utils {

    private static String root = Utils.class.getClassLoader().getResource("").getPath(); // CLASSPATH
    private static String separator = File.separator; // 分隔符
    private static List<Class> classes;

    public static List<Class> getAllClass() {
        root = new File(root).getPath(); // 更换CLASSPATH的样式
        classes = new ArrayList<>();
        listFilesForFolder(new File(root));
        return classes;
    }

    private static void listFilesForFolder(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                listFilesForFolder(file);
            } else {
                String path = file.getPath();
                if (path.endsWith(".class")) {
                    String classPath = path.replaceAll(Matcher.quoteReplacement(root), "") // 去掉绝对路径
                            .replaceAll(Matcher.quoteReplacement(separator), ".") // 更换间隔符
                            .replaceAll(".class", ""); // 去掉后缀

                    if (classPath.startsWith(Matcher.quoteReplacement("."))) // 去掉开头的 .
                        classPath = classPath.replaceFirst(Matcher.quoteReplacement("."), "");
                    try {
                        classes.add(Class.forName(classPath));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Utils.getAllClass());
    }

}
