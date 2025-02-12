package com.yupi.cli.Command;

import cn.hutool.core.bean.BeanUtil;
import com.yupi.generator.file.MainFileGenerator;
import com.yupi.model.DataModel;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.lang.Override;
import java.util.concurrent.Callable;
import lombok.Data;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * 子命令模块
 */
@Data
@Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable {
    /**
     * 是否生成循环（开关）
     */
    @Option(names = {"-l", "--loop"}, arity = "0..1", description = "是否循环？", echo = true, interactive = true)
    private boolean loop;

    /**
     * 作者注释
     */
    @Option(names = {"-a", "--author"}, arity = "0..1", description = "定义作者名称", echo = true, interactive = true)
    private String author = "RngAd33";

    /**
     * 输出信息
     */
    @Option(names = {"-o", "--output"}, arity = "0..1", description = "输出结果", echo = true, interactive = true)
    private String outputText = "结果：";

    @Override
    public Integer call() throws Exception{
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);  // 复制属性
        System.out.println("配置信息：" + dataModel);

        try {
            MainFileGenerator.doGenerate(dataModel);  // 关键语句
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e){
            throw new RuntimeException(e);
        } return 0;
    }

}