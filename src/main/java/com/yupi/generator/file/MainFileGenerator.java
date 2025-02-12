package com.yupi.generator.file;

import com.yupi.model.DataModel;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;

/**
 * 动静结合-生成完整代码
 */
public class MainFileGenerator {

    public static void doGenerate(Object model) throws IOException, TemplateException
    {
        /* 复制静态文件 */
        // 获取整个项目的根路径
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();

        // 输入路径：ACM 示例代码模板目录
        String inputPath = new File(parentFile, "yuzi-generator-demo-projects/acm-template").getAbsolutePath();

        // 输出路径：直接输出到项目的根目录
        String outputPath = projectPath;

        // 复制，生成静态文件
        StaticFileGenerator.copyFilesByHutool(inputPath, outputPath);


        /* 动态生成文件 */
        // 此处路径已写死，需要通过设置元信息优化
        String dynamicInputPath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicOutputPath = projectPath + File.separator + "acm-template/src/com/yupi/acm/MainTemplate.java";
        DynamicFileGenerator.doGenerate(dynamicInputPath, dynamicOutputPath, model);
    }

    public static void main(String[] args) throws TemplateException, IOException {

        // 生成数据模型
        DataModel dataModel = new DataModel();
        dataModel.setLoop(false);
        dataModel.setAuthor("RngAd33");
        dataModel.setOutputText("输出结果");

        doGenerate(dataModel);
    }

}