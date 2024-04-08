package com.wh.common.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteFile {

    private static final Logger logger = LoggerFactory.getLogger(FileTest.class);

    public static void main(String[] args) {
        // 参数合法性检查
        if (args.length == 0) {
            logger.error("缺少参数：请提供要处理的目录路径。");
            return;
        }
        String path = args[0];

        File directory = new File(path);
        // 目录存在性检查
        if (!directory.exists()) {
            logger.error("目录不存在：{}" , path);
            return;
        }

        listFilesRecursive(directory);
    }

    /**
     * 递归遍历子文件夹的方法
     *
     * @param file java.io.File
     */
    public static void listFilesRecursive(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    logger.info("文件或文件夹：{}" , f.getAbsolutePath());
                    listFilesRecursive(f);
                    // 对文件进行处理
                    processFile(f);
                }
            }
        }
    }

    /**
     * 处理文件：移动并重命名
     *
     * @param file java.io.File
     */
    public static void processFile(File file) {
        if (file.isFile()) {
            Path sourcePath = file.toPath();
            Path destinationPath = sourcePath.getParent().resolve(sourcePath.getFileName() + ".txt");
            try {
                Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                logger.info("文件移动并重命名成功：{} -> {}" , file.getAbsolutePath() , destinationPath);
            } catch (IOException e) {
                logger.error("文件操作失败：{}" , e.getMessage());
            }
            deleteFolder(file.getParent()); // 假设该文件不再需要原始文件夹
        }
    }

    /**
     * 删除文件及文件夹
     *
     * @param folderPath java.lang.String
     */
    public static void deleteFolder(String folderPath) {
        File folder = new File(folderPath);
        // 判断文件夹是否存在且是文件夹类型
        if (folder.exists() && folder.isDirectory()) {
            try {
                Files.walk(folder.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                logger.info("删除文件夹成功：{}" , folderPath);
            } catch (IOException e) {
                logger.error("删除文件夹失败：{}" , e.getMessage());
            }
        }
    }
}
