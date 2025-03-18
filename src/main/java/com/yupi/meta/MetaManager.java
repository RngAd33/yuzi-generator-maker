package com.yupi.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * 以单例模式，创建、获取meta
 */
public class MetaManager {

    private static volatile Meta meta;  // volatile关键字确保多线程下的内存可见性（详见：并发编程）

    private MetaManager() {}  // 私有构造函数，防止外部实例化破坏单例模式

    // private final static Meta meta = initMeta();   // 饿汉式单例模式（本项目暂不采用）

    /**
     * 获取meta
     * @return meta
     */
    public static Meta getMetaObject() {
        // 双检锁机制
        if (meta == null) {
            synchronized (MetaManager.class) {
                if (meta == null) {
                    meta = initMeta();
                }
            }
        } return meta;
    }

    /**
     * 初始化meta
     * @return newMeta
     */
    private static Meta initMeta() {
        // String metaJson = ResourceUtil.readUtf8Str("meta.json");
        String metaJson = ResourceUtil.readUtf8Str("springBoot-init-meta.json");
        Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);
        MetaValidator.doValidAndFill(newMeta);   // 校验配置文件、处理默认值
        return newMeta;
    }
}