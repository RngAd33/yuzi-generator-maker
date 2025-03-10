package com.yupi.template.model;

import com.yupi.meta.Meta;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
public class TemplateMakerModelConfig {

    private List<Meta.ModelConfig.ModelInfo> models;

    private ModelGroupConfig modelGroupConfig;

    @NoArgsConstructor
    @Data
    public static class ModelInfoConfig {

        private String abbr;

        private String fieldName;

        private String type;

        private String description;

        private Object defaultValue;

        private String replaceText;   // 用于替换哪些文本

    }

    @Data
    public static class ModelGroupConfig {

        private String groupKey;

        private String groupName;

        private String condition;

    }
}
