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
import java.util.stream.Collectors;

/**
 * 元信息模板制作工具
 */
public class TemplateMaker {

    /**
     * 模板制作方法
     *
     * @param id 时间戳命名
     * @return
     */
    private static long makeTemplate(Long id) {

        // 没有id？生成一个！
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }

        // 〇、创建隔离工作空间以完成文件的生成和处理
        // 1. 指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = FileUtil.getAbsolutePath(new File(projectPath).getParentFile() + File.separator + "yuzi-generator-demo-projects/acm-template-pro");

        // 2. 复制目录（创建独立空间）
        id = IdUtil.getSnowflakeNextId();   // 时间戳命名，防重复
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
        // 要挖坑的项目目录
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        /* 请注意：在 Windows 系统下，需要对路径进行转义 */
        sourceRootPath = sourceRootPath.replace("\\\\", "/");

        // 要挖坑的文件
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

        // 文件配置信息（fileInfo）
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 三、生成配置文件
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        // 若 meta 文件已存在，即不是第一次制作，则在原有 meta 的基础上覆盖、追加元信息
        if (FileUtil.exist(metaOutputPath))
        {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            /* 追加配置 */
            // 1. 追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfoList =  oldMeta.getFileConfig().getFiles();
            fileInfoList.add(fileInfo);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = oldMeta.getModelConfig().getModels();
            modelInfoList.add(modelInfo);

            // 2. 更新元信息文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(oldMeta), metaOutputPath);

        } else{
            // 1. 构造配置参数
            Meta meta = new Meta();
            meta.setName(name);
            meta.setDescription(description);

            // (1) FileConfig
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            meta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.add(fileInfo);

            // (2) ModelConfig
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            meta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.add(modelInfo);

            // 2. 输出元信息文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), metaOutputPath);
        }
        return id;
    }

    /**
     * 文件去重
     *
     * @param fileInfoList
     * @return
     */
    public static List<Meta.FileConfig.FileInfo> distinctFile(List<Meta.FileConfig.FileInfo> fileInfoList) {
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(
                fileInfoList.stream()
                        .collect(
                                Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o ->o, (e, r) -> r)
                        ).values()   // (exist, replacement) -> replacement
        );
        return newFileInfoList;
    }

    /**
     * 模型去重
     *
     * @param modelInfoList
     * @return
     */
    public static List<Meta.ModelConfig.ModelInfo> distinctModel(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(
                modelInfoList.stream()
                        .collect(
                                Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)
                        ).values()
        );
        return newModelInfoList;
    }

    public static void main(String[] args) {
        TemplateMaker.makeTemplate(1897511829895282688L);   // 临时参数 id
    }
}