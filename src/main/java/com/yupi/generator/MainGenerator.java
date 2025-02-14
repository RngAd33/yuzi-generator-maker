package com.yupi.generator;

import com.yupi.meta.Meta;
import com.yupi.meta.MetaManager;

public class MainGenerator {
    public static void main(String[] args) {
        Meta meta = MetaManager.getMetaObject();
        System.out.println(meta);
    }

}