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
    public boolean needGit = true ;

    /**
     * 核心模板
     */
    public MainTemplate mainTemplate;

}