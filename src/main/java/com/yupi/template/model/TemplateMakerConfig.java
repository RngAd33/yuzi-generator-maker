package com.yupi.template.model;

import com.yupi.meta.Meta;
import lombok.Data;

/**
 * 模板配置文件
 */
@Data
public class TemplateMakerConfig {

    private Long id;

    private Meta meta = new Meta();

    private String originProjectPath;

    private TemplateMakerFileConfig templateMakerFileConfig;

    private TemplateMakerModelConfig templateMakerModelConfig;

}