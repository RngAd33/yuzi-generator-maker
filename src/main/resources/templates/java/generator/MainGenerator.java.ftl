package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void doGenerate(Object model) throws IOException, TemplateException {

        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;
        <#list fileConfig.files as fileInfo>
            <#if fileInfo.condition??>
            if (${fileInfo.condition}) {
                inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
                outputPath = new File(outputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
                <#if fileInfo.generateType = "static">
                    StaticGenerator.copyFilesByHutool(inputPath, outputPath);
                <#else>
                    DynamicGenerator.doGenerate(inputPath, outputPath, model);
                </#if>
            }
            <#else>
                inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
                outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
                <#if fileInfo.generateType == "static">
                    StaticGenerator.copyFilesByHutool(inputPath, outputPath);
                <#else>
                    DynamicGenerator.doGenerate(inputPath, outputPath, model);
                </#if>
            </#if>
        </#list>
    }
}