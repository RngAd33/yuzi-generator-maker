package com.yupi.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.yupi.meta.Meta;
import com.yupi.meta.enums.FileGenerateTypeEnum;
import com.yupi.meta.enums.FileTypeEnum;
import com.yupi.template.enums.FileFilterRangeEnum;
import com.yupi.template.enums.FileFilterRuleEnum;
import com.yupi.template.model.FileFilterConfig;
import com.yupi.template.model.TemplateMakerFileConfig;
import com.yupi.template.model.TemplateMakerModelConfig;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 元信息模板制作工具
 */
public class TemplateMaker {

    /**
     * 主函数，所有的配置方法均在此调用
     *
     * @param args
     */
    public static void main(String[] args) {

        // 构造 meta 配置参数
        Meta meta = new Meta();
        meta.setName("Spring-Boot-generator");
        meta.setDescription("SpringBoot 示例模板生成器");

        // 指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "yuzi-generator-demo-projects/springboot-init";
        String fileInputPath1 = "src/main/java/com/yupi/project/common";
        String fileInputPath2 = "src/main/resources/application.yml";

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        // - 模型配置
        // 1. url
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3309/yuzi-generator_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3309/yuzi-generator_db");
        // 2. username
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("RngAd33");
        // - 模型组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("MySQL");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);
        // - 将模型信息写入组
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        // 替换变量
        String searchStr = "BaseResponse";

        // 文件过滤
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        // - 文件配置
        // 1. common
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(fileInputPath1);
        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                // 此处目前过滤文件名带有"Base"的文件
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();
        fileFilterConfigList.add(fileFilterConfig);
        fileInfoConfig1.setFilterConfigList(fileFilterConfigList);
        // 2. application.yml
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(fileInputPath2);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1, fileInfoConfig2));
        // - 文件组配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setGroupKey("key");
        fileGroupConfig.setGroupName("测试分组");
        fileGroupConfig.setCondition("outputText");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);

        long id = makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, 1898351249921880064L);
        System.out.println(id);
    }

    /**
     * 制作模板
     *
     * @param newMeta
     * @param originProjectPath
     * @param templateMakerFileConfig
     * @param templateMakerModelConfig
     * @param id
     * @return
     */
    public static long makeTemplate(Meta newMeta, String originProjectPath, TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, Long id) {

        // 没有id？生成一个！
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }

        // 复制目录（创建隔离工作空间以完成文件的生成和处理）
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;

        /* 目录不存在，即首次制作，创建目录 */
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }

        // 一、输入文件信息
        // 要挖坑的项目目录（绝对路径）
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");   /* 请注意：在 Windows 系统下，需要对路径进行转义 */
        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList = templateMakerFileConfig.getFiles();

        // 二、生成文件模板
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileConfigInfoList) {
            String fileInputPath = fileInfoConfig.getPath();

            // 如果填的是相对路径，则转化为绝对路径后传入
            if (!fileInputPath.startsWith(sourceRootPath)) {
                fileInputPath = sourceRootPath + File.separator + fileInputPath;
            }

            // 获取过滤之后的文件列表（此处不会存在目录）
            List<File> fileList = FileFilter.doFilter(fileInputPath, fileInfoConfig.getFilterConfigList());
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(templateMakerModelConfig, sourceRootPath, file);
                newFileInfoList.add(fileInfo);
            }
        }

        /* 文件分组 */
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        // 如果是文件组
        if (fileGroupConfig != null) {
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();
            String condition = fileGroupConfig.getCondition();

            // - 新增分组配置
            Meta.FileConfig.FileInfo fileGroupInfo = new Meta.FileConfig.FileInfo();
            fileGroupInfo.setType(FileTypeEnum.GROUP.getValue());
            fileGroupInfo.setGroupKey(groupKey);
            fileGroupInfo.setGroupName(groupName);
            fileGroupInfo.setCondition(condition);

            // - 文件全部放到一个组内
            fileGroupInfo.setFiles(newFileInfoList);
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(fileGroupInfo);
        }

        /* 处理模型信息 */
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        // - 转化为可接受的 ModelInfo 对象
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
                    BeanUtil.copyProperties(modelInfoConfig, modelInfo);
                    return modelInfo;
        }).collect(Collectors.toList());

        // - 本次新增的模型配置列表
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();

        // - 如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {
            String condition = modelGroupConfig.getCondition();
            String groupKey = modelGroupConfig.getGroupKey();
            String groupName = modelGroupConfig.getGroupName();
            Meta.ModelConfig.ModelInfo modelGroupInfo = new Meta.ModelConfig.ModelInfo();
            modelGroupInfo.setCondition(condition);
            modelGroupInfo.setGroupKey(groupKey);
            modelGroupInfo.setGroupName(groupName);

            // - 模型全部放到一个组内
            modelGroupInfo.setModels(inputModelInfoList);
            newModelInfoList.add(modelGroupInfo);
        } else {
            // - 不分组，添加所有模型信息到列表
            newModelInfoList.addAll(inputModelInfoList);
        }

        // 三、生成配置文件
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";
        // - 若 meta 文件已存在，即不是第一次制作，则在原有 meta 的基础上覆盖、追加元信息
        if (FileUtil.exist(metaOutputPath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            // - 追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfoList =  newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);

            // - 配置去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));

        } else {
            // - 构造配置参数
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
            modelInfoList.addAll(newModelInfoList);
        }

        // 四、输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
        return id;
    }

    /**
     * 制作文件模板
     *
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @param inputFile
     * @return
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath, File inputFile) {

        // 要挖坑的文件的绝对路径
        /* 请注意：在 Windows 系统下，需要对路径进行转义 */
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");

        // 使用字符串替换，生成模板文件
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 文件输入、输出的相对路径
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputPath + ".ftl";

        // 若模板文件已存在，即不是第一次制作，则在原有模板的基础上再挖坑
        String fileContent;
        boolean hasTemplatFile = FileUtil.exist(fileOutputAbsolutePath);
        if (hasTemplatFile) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        // 支持多个模型：对同一个文件的内容，遍历模型进行多轮替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String newFileContent = fileContent;
        String replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            if (modelGroupConfig == null) {
                // - 不是分组
                replacement = String.format("${%s}", modelInfoConfig.getFieldName());
            } else {
                // - 是分组
                String groupKey = modelGroupConfig.getGroupKey();
                /* 请注意：挖坑要多一个层级 */
                replacement = String.format("${%s, %s}", groupKey, modelInfoConfig.getFieldName());
            }
            // - 多次替换
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 如果和原来一致，即没有挖坑，则静态生成
        boolean contentEquals = newFileContent.equals(fileContent);
        if (!hasTemplatFile) {
            if (contentEquals) {
                fileInfo.setOutputPath(fileInputPath);   // 输入路径 = 输出路径
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            } else {
                // - 已挖坑，动态生成
                FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
            }
        } else if (!contentEquals) {
            // - 有模板文件，且增加了新坑，生成模板文件‘
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
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {

        // 策略：同分组内文件 merge. 不同分组保留
        // 1. 有分组的，以组为单位划分
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey)
                );

        // 2. 同组内文件配置合并
        // 保存每个组对应的合并后对象 map
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergeFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();
            List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(
                    tempFileInfoList.stream()
                            .flatMap(fileInfo -> fileInfo.getFiles().stream())
                            .collect(
                                    Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o ->o, (e, r) -> r)
                            ).values());   // (exist, replacement) -> replacement

            // 使用新的 group 配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergeFileInfoMap.put(groupKey, newFileInfo);
        }

        // 3. 将文件分组添加到结果列表
        List<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(
                groupKeyMergeFileInfoMap.values()
        );

        // 4. 将未分组的文件添加到结果列表
        List<Meta.FileConfig.FileInfo> noGroupFileInfoList = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList()
                );
        resultList.addAll(new ArrayList<>(
                noGroupFileInfoList.stream()
                        .collect(
                                Collectors.toMap(Meta.FileConfig.FileInfo :: getInputPath, o -> o, (e, r) -> r)
                        ).values()));

        return resultList;
    }

    /**
     * 模型去重
     *
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {

        // 策略：同分组内文件 merge. 不同分组保留
        // 1. 有分组的，以组为单位划分
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList
                .stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey)
                );

        // 2. 同组内文件配置合并
        // 保存每个组对应的合并后对象 map
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergeModelInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfoListMap.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tempModelInfoList = entry.getValue();
            List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(
                    tempModelInfoList.stream()
                            .flatMap(modelInfo -> modelInfo.getModels().stream())
                            .collect(
                                    Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o ->o, (e, r) -> r)
                            ).values());   // (exist, replacement) -> replacement

            // 使用新的 group 配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergeModelInfoMap.put(groupKey, newModelInfo);
        }

        // 3. 将文件分组添加到结果列表
        List<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(
                groupKeyMergeModelInfoMap.values()
        );

        // 4. 将未分组的文件添加到结果列表
        List<Meta.ModelConfig.ModelInfo> noGroupModelInfoList = modelInfoList
                .stream()
                .filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toList()
                );
        resultList.addAll(new ArrayList<>(
                noGroupModelInfoList.stream()
                        .collect(
                                Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)
                        ).values()));

        return resultList;
    }
}