package com.yupi.meta.enums;

public enum FileTypeEnum {
    DIR("目录", "dir"),
        File("文件", "file");

    private final String text;

    private final String value;

    FileTypeEnum(String text, String value)
    {
        this.text = text;
        this.value = value;
    }

    private String text()
    {
        return text;
    }

    public String value()
    {
        return value;
    }
}