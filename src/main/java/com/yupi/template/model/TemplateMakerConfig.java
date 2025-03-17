package com.yupi.template.model;

import com.yupi.meta.Meta;
import lombok.Data;

/**
 * 封装配置
 */
@Data
public class TemplateMakerConfig {

    private Long id;

    private Meta meta = new Meta();

    private String originProjectPath;

    private TemplateMakerFileConfig fileConfig;

    private TemplateMakerModelConfig modelConfig;

}