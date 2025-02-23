package com.yupi.meta.enums;

/**
 * 模型类型枚举
 */
public enum ModelTypeEnum {

    STRING("字符串类型", "string"),
    BOOLEAN("布尔类型", "boolean");

    private final String text;

    private final String value;

    ModelTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    private String text() {
        return text;
    }

    public String value() {
        return value;
    }
}
