package com.yupi.generator;

import cn.hutool.extra.template.TemplateException;
import com.yupi.meta.Meta;
import com.yupi.meta.MetaManager;
import java.io.IOException;

public class MainGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        Meta meta = MetaManager.getMetaObject();
        System.out.println(meta);


    }

}