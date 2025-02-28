package com.yupi.generator;

import java.io.*;

public class JarGenerator {

    public static void doGenerate(String projectDir) throws IOException, InterruptedException {

        // 清理以前的构建并打包
        // 注意：不同操作系统执行的命令不同
        String winMavenCommand = "mvn.cmd clean package -DskipTests=true";
        String otherMavenCommand = "mvn clean package -DskipTests=true";
        String mavenCommand = winMavenCommand;
        // String mavenCommand = otherMavenCommand;

        ProcessBuilder processBuilder = new ProcessBuilder(mavenCommand.split(" "));   // 用空格拆分命令
        processBuilder.directory(new File(projectDir));   // 指定路径

        Process process = processBuilder.start();

        // 读取命令的输出
        InputStream inputStream = process.getInputStream();   // 获取输入流
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));   // 缓冲区读取器
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // 等待命令执行完成并返回状态
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.out.println("————！！！错误发生，打包进程终止，请尽快排查！！！————");
        } else {
            System.out.println("————打包完成———>>>");
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        doGenerate("E:/document/资料/计算机/A小金库/Java/yuzi-generator/yuzi-generator-maker/generated");
    }
}