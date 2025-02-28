package com.yupi.model;

import lombok.Data;

/**
 * 数据模型
 */
@Data
public class DataModel {

    /**
     * 是否生成循环（开关）
     */
    public boolean loop;

    /**
     * 是否生成 .gitignore 文件
     */
    public boolean needGit = true;

    /**
     * 核心模板
     */
    public MainTemplate mainTemplate = new MainTemplate();

    /**
     * 生成核心模板
     */
    @Data
    public static class MainTemplate {

        /**
         * 作者注释
         */
        public String author = "RngAd33";

        /**
         * 输出信息
         */
        public String outputText = "输出结果";

    }
}