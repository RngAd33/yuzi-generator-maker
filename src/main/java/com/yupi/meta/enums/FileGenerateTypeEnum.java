package com.yupi.meta.enums;

public enum FileGenerateTypeEnum {

    DYNAMIC("动态", "dynamic"),
    STATIC("静态", "static");

    private final String text;

    private final String value;

    FileGenerateTypeEnum(String text, String value) {
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