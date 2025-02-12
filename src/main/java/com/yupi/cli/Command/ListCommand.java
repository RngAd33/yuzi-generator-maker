package com.yupi.cli.Command;

import cn.hutool.core.io.FileUtil;
import java.io.File;
import java.util.List;
import picocli.CommandLine.Command;

@Command(name = "list", description = "查看文件列表", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {

    public void run()
    {
        String projectPath = System.getProperty("user.dir");

        // 获取根路径
        File parentFile = new File(projectPath).getParentFile();

        // 输入路径
        String inputPath = new File(parentFile, "/acm-template").getAbsolutePath();
        List<File> files = FileUtil.loopFiles(inputPath);

        for (File file : files) {
            System.out.println(file);
        }
    }

}