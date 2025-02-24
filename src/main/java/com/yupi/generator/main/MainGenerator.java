package com.yupi.generator.main;

import freemarker.template.TemplateException;
import java.io.IOException;
import java.lang.InterruptedException;

/**
 * 调用父类，生成文件
 */
public class MainGenerator extends GenerateTemplate {
    public static void main(String[] args) throws IOException, TemplateException, InterruptedException {
        MainGenerator mainGenerator = new MainGenerator();
        mainGenerator.doGenerate();
    }
}