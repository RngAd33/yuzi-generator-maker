package com.yupi.meta;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * 元信息校验
 */
public class MetaValidator {
    public static void doValidAndFill(Meta meta) {

        /* 基础信息校验 */
        String name = meta.getName();
        if (StrUtil.isBlank(name)) {
            name = "acm-template-pro-generator";
            meta.setName(name);
        }

        String description = meta.getDescription();
        if (StrUtil.isEmpty(description)) {
            description = "ACM 示例模板生成器";
            meta.setDescription(description);
        }

        String basePackage = meta.getBasePackage();
        if (StrUtil.isBlank(basePackage)){
            basePackage = "com.yupi";
            meta.setBasePackage(basePackage);
        }

        String version = meta.getVersion();
        if (StrUtil.isEmpty(version)) {
            version = "1.0";
            meta.setVersion(version);
        }

        String author = meta.getAuthor();
        if (StrUtil.isEmpty(author)) {
            author = "RngAd33";
            meta.setAuthor(author);
        }

        String createTime = meta.getCreateTime();
        if (StrUtil.isEmpty(createTime)) {
            createTime = "2024-02-24";
            meta.setCreateTime(createTime);
        }

        /* fileConfig 校验 */
        Meta.FileConfig fileConfig = meta.getFileConfig();
        if (fileConfig == null) {

            // sourceRootPath（必填）
            String sourceRootPath = fileConfig.getSourceRootPath();
            if (StrUtil.isBlank(sourceRootPath))
            {
                throw new MetaException("——！未填写 sourceRootPath！——");
            }

            // inputRootPath（.source + sourceRootPath 的最后一个层级路径）
            String inputRootPath = fileConfig.getInputRootPath();
            String defaultInputPath = ".source" + File.separator +
                    FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString();
            if (StrUtil.isBlank(inputRootPath)) {
                fileConfig.setInputRootPath(defaultInputPath);
            }

            // outputPath（默认为当前路径下的 generated）
            String outputRootPath = fileConfig.getOutputRootPath();
            String defaultOutputPath = "generated";
            if (StrUtil.isEmpty(outputRootPath)) {
                fileConfig.setOutputRootPath(defaultOutputPath);
            }

            // fileConfigType（默认为 dir）
            String fileConfigType = fileConfig.getType();
            String defaultType = "dir";
            if (StrUtil.isEmpty(fileConfigType)) {
                fileConfig.setType(defaultType);
            }

            // fileInfo 默认值
            List<Meta.FileConfig.FileInfo> fileInfoList = fileConfig.getFiles();
            if (CollectionUtil.isNotEmpty(fileInfoList)) {
                for (Meta.FileConfig.FileInfo fileInfo : fileInfoList) {
                    // -inputPath（必填）
                    String inputPath = fileInfo.getInputPath();
                    if (StrUtil.isEmpty(inputPath)) {
                        throw new MetaException("——！未填写 inputPath！——");
                    }

                    // -outputPath（默认等于inputPath）
                    String outputPath = fileInfo.getOutputPath();
                    if (StrUtil.isEmpty(outputPath)) {
                        fileInfo.setOutputPath(inputPath);
                    }

                    // -type（默认 inputPath 有文件后缀为 file，否则为 dir）
                    String type = fileInfo.getType();
                    if (StrUtil.isBlank(type)) {
                        if (StrUtil.isBlank(FileUtil.getSuffix(inputPath))) {
                            fileInfo.setType("dir");   // 没有文件后缀
                        } else {
                            fileInfo.setType("file");   // 有文件后缀
                        }
                    }

                    // generateType（如果文件末尾扩展名不为 ftl，generateType 默认为 static，否则为 dynamic）
                    String generateType = fileInfo.getGenerateType();
                    if (StrUtil.isEmpty(generateType)) {
                        if (inputPath.endsWith(".ftl")) {
                            fileInfo.setGenerateType("dynamic");   // 动态模板
                        } else {
                            fileInfo.setGenerateType("static");   // 静态模板
                        }
                    }
                }
            }
        }

        /* modelConfig 校验 */
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if (modelConfig == null) {
            List<Meta.ModelConfig.ModelInfo> modelInfoList = modelConfig.getModels();
            if (CollectionUtil.isNotEmpty(modelInfoList)) {
                for (Meta.ModelConfig.ModelInfo modelInfo : modelInfoList) {
                    String fieldName = modelInfo.getFieldName();
                    if (StrUtil.isBlank(fieldName)) {
                        throw new MetaException("——！未填写 fieldName！——");
                    }

                    String modelInfoType = modelInfo.getType();
                    if (StrUtil.isEmpty(modelInfoType)) {
                        modelInfo.setType("String");
                    }
                }
            }
        }
    }
}