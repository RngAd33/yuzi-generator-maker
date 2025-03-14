package com.yupi.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public class ScriptGenerator {
    public static void doGenerate(String outputPath, String jarPath) {

        StringBuilder stringBuilder = new StringBuilder();

        /* Windows 脚本
            @echo off
            java -jar yuzi-generator-basic-1.0-SNAPSHOT-jar-with-dependencies.jar %*
        */
        stringBuilder.append("@echo off").append("\n");
        stringBuilder.append(String.format("java -jar %s %%*", jarPath)).append('\n');
        FileUtil.writeBytes(stringBuilder.toString().getBytes(StandardCharsets.UTF_8), outputPath + ".bat");

        /* Linux 脚本
            #!/bin/bash
            java -jar yuzi-generator-basic-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"
        */
        stringBuilder = new StringBuilder();
        stringBuilder.append("#!/bin/bash").append('\n');
        stringBuilder.append(String.format("java -jar %s \"$@\"", jarPath)).append('\n');
        FileUtil.writeBytes(stringBuilder.toString().getBytes(StandardCharsets.UTF_8), outputPath);

        // 添加可执行权限（Linux）
        try {
            Set<PosixFilePermission> permissions =  PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(Paths.get(outputPath), permissions);
        } catch (Exception e) {}   // 不对异常做任何处理，防止Windows环境下报错
    }

    public static void main(String[] args) {
        String outputPath = System.getProperty("user.dir") + File.separator + "generator";
        doGenerate(outputPath, "");
    }
}