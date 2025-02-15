package com.yupi.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.yupi.generator.file.DynamicFileGenerator;
import com.yupi.meta.Meta;
import com.yupi.meta.MetaManager;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;

/**
 * 调用测试，生成文件
 */
public class MainGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        // 生成模型
        Meta meta = MetaManager.getMetaObject();
        System.out.println(meta);

        // 输出的根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + "generated";   // -> ./generator
        if (FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // 读取resources目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        // Java包输出路径
        String outputBasePackage = meta.getBasePackage();
        String outputBasePackagePath = StrUtil.join("/", StrUtil.split(outputBasePackage, "."));   // '.'化'/'
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + outputBasePackagePath;   // 完整路径

        // 最终输出路径
        String inputFilePath = inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        String outputFilePath = outputBaseJavaPackagePath + "/model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
    }
}