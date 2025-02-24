package com.yupi.generator.main;

import freemarker.template.TemplateException;
import java.io.IOException;
import java.lang.InterruptedException;

/**
 * 调用父类，生成文件
 */
public class MainGenerator extends GenerateTemplate {

    @Override
    protected void buildDist(String outputPath, String jarPath, String shellOutputFilePath, String sourceCopyDestPath) {
        System.out.println("不生成dist");
    }

    public static void main(String[] args) throws IOException, TemplateException, InterruptedException {
        MainGenerator mainGenerator = new MainGenerator();
        mainGenerator.doGenerate();   // GenerateTemplate.doGenerate()，实现所有
    }
}