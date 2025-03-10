package com.yupi.template.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
public class TemplateMakerFileConfig {

    private List<FileInfoConfig> files;

    @NoArgsConstructor
    @Data
    public static class FileInfoConfig {

        private String path;

        private List<FileFilterConfig> filterConfigList;

    }

    @NoArgsConstructor
    @Data
    public static class GroupConfig {

        private String groupKey;

        private String groupName;

        private String condition;

    }
}