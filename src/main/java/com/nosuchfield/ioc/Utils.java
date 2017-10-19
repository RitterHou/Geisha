package com.nosuchfield.ioc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hourui 2017/10/18 19:37
 */
public class Utils {

    private static String root = Utils.class.getClassLoader().getResource("").getPath();
    private static List<Class> classes;

    public static List<Class> getAllClass() {
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
                    String classPath = path.replaceAll(root, "").replaceAll("/", ".").replaceAll(".class", "");
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