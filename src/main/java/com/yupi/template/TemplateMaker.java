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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 元信息模板制作工具
 */
public class TemplateMaker {

    public static void main(String[] args) {
        // 构造配置参数
        Meta meta = new Meta();
        meta.setName("acm-template-pro-generator");
        meta.setDescription("ACM 示例模板生成器");

        // 指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "yuzi-generator-demo-projects/springboot-init";
        String fileInputPath1 = "src/main/java/com/yupi/project/common";
        String fileInputPath2 = "src/main/java/com/yupi/project/controller";
        List<String> fileInputPathList = Arrays.asList(fileInputPath1, fileInputPath2);

        /* 输入模型参数（第一次）
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("outputText");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("sum = ");
        */
        // 输入模型参数（第二次）
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("className");
        modelInfo.setType("String");

        // 替换变量（第一次）
        // String searchStr = "Sum = ";
        // 替换变量（第二次）
        String searchStr = "MainTemplate";

        long id = TemplateMaker.makeTemplate(meta, originProjectPath, fileInputPathList, modelInfo, searchStr, null);
        System.out.println(id);
    }

    /**
     * 模板制作
     *
     * @param newMeta
     * @param originProjectPath
     * @param fileInputPathList
     * @param modelInfo
     * @param searchStr
     * @param id
     * @return
     */
    private static long makeTemplate(Meta newMeta, String originProjectPath, List<String> fileInputPathList, Meta.ModelConfig.ModelInfo modelInfo, String searchStr, Long id) {

        // 没有id？生成一个！
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }

        // 复制目录（创建隔离工作空间以完成文件的生成和处理）
        String projectPath = System.getProperty("user.dir");
        String temDirPath = projectPath + File.separator + ".temp";
        String templatePath = temDirPath + File.separator + id;

        /* 目录不存在，即首次制作，创建目录 */
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }

        // 一、输入文件信息
        // 要挖坑的项目目录
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        /* 请注意：在 Windows 系统下，需要对路径进行转义 */
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");

        // 二、生成文件模板
        // 遍历输入的文件
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        for (String fileInputPath : fileInputPathList) {
            String fileInputAbsolutePath = sourceRootPath + File.separator + fileInputPath;
            // 如果是目录就遍历
            if (FileUtil.isDirectory(fileInputAbsolutePath)) {
                List<File> fileList =  FileUtil.loopFiles(fileInputAbsolutePath);
                for (File file : fileList) {
                    Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(modelInfo, searchStr, sourceRootPath, file);
                    newFileInfoList.add(fileInfo);
                }
            } else {
                // 输入的是文件
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(modelInfo, searchStr, sourceRootPath, new File(fileInputAbsolutePath));
                newFileInfoList.add(fileInfo);
            }
        }


        // 生成配置文件
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        // 若 meta 文件已存在，即不是第一次制作，则在原有 meta 的基础上覆盖、追加元信息
        if (FileUtil.exist(metaOutputPath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            /* 追加配置 */
            // 1. 追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfoList =  oldMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);

            List<Meta.ModelConfig.ModelInfo> modelInfoList = oldMeta.getModelConfig().getModels();
            modelInfoList.add(modelInfo);

            // 2. 调用方法，配置去重
            oldMeta.getFileConfig().setFiles(distinctFile(fileInfoList));
            oldMeta.getModelConfig().setModels(distinctModel(modelInfoList));

            // 3. 更新 meta 文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(oldMeta), metaOutputPath);

        } else {
            // 1. FileConfig
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.addAll(newFileInfoList);

            // 2. ModelConfig
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.add(modelInfo);

            // 输出元信息文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
        }
        return id;
    }

    /**
     * 制作模板文件
     *
     * @param modelInfo
     * @param searchStr
     * @param sourceRootPath
     * @param inputFile
     * @return
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(Meta.ModelConfig.ModelInfo modelInfo, String searchStr, String sourceRootPath, File inputFile) {
        // 要挖坑的文件的绝对路径
        /* 请注意：在 Windows 系统下，需要对路径进行转义 */
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 文件输入、输出的相对路径
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputPath + ".ftl";

        // 使用字符串替换，生成模板文件
        String fileContent;

        /* 若模板文件已存在，即不是第一次制作，则在原有模板的基础上再挖坑 */
        if (FileUtil.exist(fileOutputAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());

        // 如果和原来一致，即没有挖坑，则静态生成
        if (newFileContent.equals(fileContent)) {
            fileInfo.setOutputPath(fileInputPath);   // 输入路径 = 输出路径
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
        } else {
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }

        return fileInfo;
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
}