package ${basePackage}.generator.file;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;

public class MainFileGenerator {
    public static void doGenerate(Object model) throws IOException, TemplateException {


        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;

        <#list fileConfig.files as fileInfo>
        inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath;
        outputPath = new File(outputRootPath, "${fileINfo.outputPath}").getAbsolutePath;

        <#if fileInfo.generateType == "static">
            // 复制静态文件
            StaticFileGenerator.copyFilesByHutool(inputPath, outputPath);
        <#else>
            // 动态生成文件
            DynamicFileGenerator.doGenerate(dynamicInputPath, dynamicOutputPath, model);
        </#if>
        </#list>
    }
}