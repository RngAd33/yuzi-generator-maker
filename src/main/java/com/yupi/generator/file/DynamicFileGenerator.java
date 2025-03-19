package com.yupi.generator.file;

import cn.hutool.core.io.FileUtil;
import com.yupi.model.DataModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 动态文件生成
 */
public class DynamicFileGenerator {

    /**
     * 方法封装：生成文件
     *
     * @param inputPath 模板文件输入路径
     * @param outputPath 模板文件输出路径
     * @param model 数据模型
     * @throws IOException
     * @throws TemplateException
     */
    public static void doGenerate(String inputPath, String outputPath, Object model) throws IOException, TemplateException {

        Template template = getTemplate(inputPath);

        // 文件不存在则创建文件和父目录
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }

        // 指定生成的文件
        Writer out = new FileWriter(outputPath);

        // 生成文件
        try {
            template.process(model, out);
        } catch (TemplateException e) {
            System.err.println("模板异常");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO异常");
            e.printStackTrace();
        }
        out.close();   // 生成文件后别忘了关闭哦
    }

    /**
     * 创建 FreeMarker 模板对象
     *
     * @param inputPath 模板文件输入路径
     * @throws IOException
     * @return
     */
    private static Template getTemplate(String inputPath) throws IOException {
        // 创建 FreeMarker 的 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        // - 设置模板文件使用的字符集
        configuration.setDefaultEncoding("UTF-8");
        configuration.setOutputEncoding("UTF-8");
        configuration.setNumberFormat("0.#######");

        // 指定模板文件所在的路径
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);

        // 创建模板对象，加载指定模板
        String templateName = new File(inputPath).getName();
        return configuration.getTemplate(templateName);
    }

    // 已废弃
    public static void main(String[] args) throws IOException, TemplateException {
        String projectPath = System.getProperty("user.dir");
        String inputPath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String outputPath = projectPath + File.separator + "MainTemplate.java";
        DataModel dataModel = new DataModel();
        dataModel.setLoop(false);
        dataModel.mainTemplate.setAuthor("RngAd33");
        dataModel.mainTemplate.setOutputText("输出结果：");
        doGenerate(inputPath, outputPath, dataModel);
    }
}