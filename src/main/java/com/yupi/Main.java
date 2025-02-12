package com.yupi;

import com.yupi.cli.CommandExecutor;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

/**
 * 全局调用入口
 */
public class Main {
    public static void main(String[] args) {
        // args = new String[]{"generate", "-l", "-a", "-o"};  // 测试语句
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}