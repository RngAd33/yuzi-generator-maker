package com.yupi.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.yupi.meta.Meta;
import com.yupi.meta.enums.FileGenerateTypeEnum;
import com.yupi.meta.enums.FileTypeEnum;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 元信息模板制作工具
 */
public class TemplateMaker {

    /**
     * 抽象方法
     *
     * @param id
     * @return
     */
    private static long makeTemplate(Long id) {

        // 没有id？生成一个！
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }

        return id;
    }

    public static void main(String[] args) {
        // 〇、创建隔离工作空间以完成文件的生成和处理
        // 1. 指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = FileUtil.getAbsolutePath(new File(projectPath).getParentFile() + File.separator + "yuzi-generator-demo-projects/acm-template-pro");

        // 2. 复制目录（创建独立空间）
        long id = IdUtil.getSnowflakeNextId();   // 时间戳命名，防重复
        String temDirPath = projectPath + File.separator + ".temp";
        String templatePath = temDirPath + File.separator + id;
        /* 目录不存在？创建目录！ */
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }
        FileUtil.copy(originProjectPath, templatePath, true);

        // 一、输入信息
        // 1. 项目基本信息
        String name = "acm-template-pro-generator";
        String description = "ACM 示例模板生成器";

        // 2. 输入文件信息
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        String fileInputPath = "src/com/yupi/acm/MainTemplate.java";
        String fileOutputPath = fileInputPath + ".ftl";

        // 3. 输入模型参数
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("outputText");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("sum = ");

        // 二、使用字符串替换，生成模板文件
        String fileInputAbsolutePath = sourceRootPath + File.separator + fileInputPath;
        String fileOutputAbsolutePath = sourceRootPath + File.separator + fileOutputPath;
        String fileContent;

        /* 若模板文件已存在，即不是第一次制作，则在原有模板的基础上再挖坑 */
        if (FileUtil.exist(fileOutputAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, "Sum = ", replacement);

        // 输出模板文件
        FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);

        // 三、生成配置文件
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        // 1. 构造配置参数
        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);
        // (1) fileConfig
        Meta.FileConfig fileConfig = new Meta.FileConfig();
        meta.setFileConfig(fileConfig);
        fileConfig.setSourceRootPath(sourceRootPath);
        List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
        fileConfig.setFiles(fileInfoList);
        // (2) fileInfo
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
        fileInfoList.add(fileInfo);
        // (3) modelConfig
        Meta.ModelConfig modelConfig = new Meta.ModelConfig();
        meta.setModelConfig(modelConfig);
        List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
        modelConfig.setModels(modelInfoList);
        modelInfoList.add(modelInfo);

        // 2. 输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), metaOutputPath);

    }

}