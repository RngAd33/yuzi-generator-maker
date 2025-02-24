package com.yupi.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.yupi.generator.JarGenerator;
import com.yupi.generator.ScriptGenerator;
import com.yupi.generator.file.DynamicFileGenerator;
import com.yupi.meta.Meta;
import com.yupi.meta.MetaManager;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;

/**
 * 定义程序流程
 */
public abstract class GenerateTemplate {

    /**
     * 抽取 MainGenerator 中的方法
     *
     * @throws TemplateException
     * @throws IOException
     * @throws InterruptedException
     */
    public static void doGenerate() throws TemplateException, IOException, InterruptedException {
        // 解析meta，生成模型
        Meta meta = MetaManager.getMetaObject();
        System.out.println(meta);

        // 输出的根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + "generated" + File.separator + meta.getName();
        if (FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // 1. 复制原始文件
        String sourceCopyDestPath = copySource(meta, outputPath);

        // 2. 代码生成
        generateCode(meta, outputPath);

        // 3. 构建Jar包
        buildJar(outputPath);

        // 4. 封装脚本
        // todo 不够优雅
        Result result = buildScript(outputPath, meta);

        // 5. 生成精简版程序
        buildDist(outputPath, result.jarPath, result.shellOutputFilePath, sourceCopyDestPath);
    }

    // 封装脚本
    private static Result buildScript(String outputPath, Meta meta) {
        String shellOutputFilePath = outputPath + File.separator + "generator";
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = "target/" + jarName;
        ScriptGenerator.doGenerate(shellOutputFilePath, jarPath);
        Result result = new Result(shellOutputFilePath, jarPath);
        return result;
    }

    private static class Result {
        public final String shellOutputFilePath;
        public final String jarPath;

        public Result(String shellOutputFilePath, String jarPath) {
            this.shellOutputFilePath = shellOutputFilePath;
            this.jarPath = jarPath;
        }
    }

    /**
     * 构建Jar包
     *
     * @param outputPath
     * @return jarPath
     * @throws IOException
     * @throws InterruptedException
     */
    private static void buildJar(String outputPath) throws IOException, InterruptedException {
        JarGenerator.doGenerate(outputPath);
        String jarName = ;
        String jarPath = "target" + jarName;
        return jarPath;
    }

    /**
     * 生成精简版程序（仅保留jar包和模板）
     *
     * @param outputPath
     * @param jarPath
     * @param shellOutputFilePath
     * @param sourceCopyDestPath
     */
    private static void buildDist(String outputPath, String jarPath, String shellOutputFilePath, String sourceCopyDestPath) {
        String distOutputPath = outputPath + "-dist";
        // - 拷贝jar包
        String targetAbsolutePath = distOutputPath + File.separator + "target";
        FileUtil.mkdir(targetAbsolutePath);
        String jarAbsolutePath = outputPath + File.separator + jarPath;
        FileUtil.copy(jarAbsolutePath, targetAbsolutePath, true);
        // - 拷贝脚本文件
        FileUtil.copy(shellOutputFilePath + ".bat", distOutputPath, true);
        FileUtil.copy(shellOutputFilePath, distOutputPath, true);
        // - 拷贝原始模板文件
        FileUtil.copy(sourceCopyDestPath, distOutputPath, true);
    }

    /**
     * 代码生成
     *
     * @param meta
     * @param outputPath
     * @throws IOException
     * @throws TemplateException
     */
    private static void generateCode(Meta meta, String outputPath) throws IOException, TemplateException {
        // 读取resources目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        // Java包输出路径
        String outputBasePackage = meta.getBasePackage();
        String outputBasePackagePath = StrUtil.join("/", StrUtil.split(outputBasePackage, "."));   // '.' -> '/'
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + outputBasePackagePath;   // 绝对路径

        // model.DataModel
        String inputFilePath = inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        String outputFilePath = outputBaseJavaPackagePath + "/model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ConfigCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.GenerateCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ListCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.CommandExecutor
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.MainGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/MainGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.DynamicGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/DynamicGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.StaticGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/StaticGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // Main
        inputFilePath = inputResourcePath + File.separator + "templates/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/Main.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // pom.xml
        inputFilePath = inputResourcePath + File.separator + "templates/pom.xml.ftl";
        outputFilePath = outputPath + "/pom.xml";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // README.md
        inputFilePath = inputResourcePath + File.separator + "templates/README.md.ftl";
        outputFilePath = outputPath + "/README.md";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
    }

    /**
     * 复制原始文件，从原始模板路径到生成的代码包中
     *
     * @param meta
     * @param outputPath
     * @return
     */
    private static String copySource(Meta meta, String outputPath) {
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, false);
        return sourceCopyDestPath;
    }

}