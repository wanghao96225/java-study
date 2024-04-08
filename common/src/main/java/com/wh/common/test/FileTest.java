package com.wh.common.test;

import java.io.File;

/**
 * @author wanghao
 * @version 1.0.0
 * @ClassName FileTest.java
 * @createTime 2023年03月21日
 */
public class FileTest {

    public static void main(String[] args) {
        // 替换成你想要获取的路径
        String path = "/xx/";

        File directory = new File(path);
        if (directory.exists()) {

            listFilesRecursive(directory);
        }

//        if (directory.exists()) {
//            File[] files = directory.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    if (file.isDirectory()) {
//                        System.out.println("文件夹：" + file.getAbsolutePath());
//                        // 如果想要递归遍历子文件夹，可以调用方法 listFilesRecursive(file);
////                        listFilesRecursive(file);
//                    } else {
//                        System.out.println("文件：" + file.getAbsolutePath());
//                    }
//                }
//            }
//        }
    }

    /**
     * 递归遍历子文件夹的方法
     *
     * @param file java.io.File
     * @author: wh
     * @date: 2023/9/2 14:36
     * @description:
     * @return: void
     **/
    public static void listFilesRecursive(File file) {

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        System.out.println("文件夹：" + f.getAbsolutePath());
                        listFilesRecursive(f);
                    } else {
                        System.out.println("文件：" + f.getAbsolutePath());

                        // 源路径
                        String source = f.getAbsolutePath();
                        // 新路径/要删除的路径
                        String newFilePath = source.substring(0, (source.length() - 9));
                        // 新目标路径
                        String newDestination = source.substring(0, (source.length() - 31));
                        // 新文件名
                        String newFileName = source.substring((source.length() - 26), (source.length() - 9));
                        // 目标路径(包括新文件名)
                        String destination = newDestination + newFileName + ".txt";

                        moveFileOrRename(source, destination, newFilePath);
                    }
                }
            }
        }
    }

    /**
     * 移动文件并重新命名在删除
     *
     * @param source      java.lang.String
     * @param destination java.lang.String
     * @param delete      java.lang.String
     * @author: wh
     * @date: 2023/9/2 14:36
     * @description:
     * @return: void
     **/
    public static void moveFileOrRename(String source, String destination, String delete) {

        // 源文件路径和名称
        File sourceFile = new File(source);

        // 目标文件路径和名称，包括新的文件名
        File destFile = new File(destination);

        // 移动文件并重命名
        if (sourceFile.renameTo(destFile)) {
            System.out.println("文件移动成功！");
            deleteFolder(delete);
        } else {
            System.out.println("文件移动失败！");
        }
    }

    /**
     * 删除文件及文件夹
     *
     * @param folderPath java.lang.String
     * @author: wh
     * @date: 2023/9/2 14:36
     * @description:
     * @return: void
     **/
    public static void deleteFolder(String folderPath) {

        File folder = new File(folderPath);
        // 判断文件夹是否存在且是文件夹类型
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            // 遍历文件夹内的所有文件和子文件夹
            if (files != null) {
                for (File file : files) {
                    // 如果是文件夹，则递归调用本方法
                    if (file.isDirectory()) {
                        deleteFolder(file.getAbsolutePath());
                    } else {
                        // 如果是文件，则直接删除
                        file.delete();
                    }
                }
            }
            // 删除空文件夹
            folder.delete();
            System.out.println("删除文件夹成功！");
        }
    }
}
