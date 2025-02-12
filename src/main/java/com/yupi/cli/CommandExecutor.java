package com.yupi.cli;

import com.yupi.cli.Command.ConfigCommand;
import com.yupi.cli.Command.GenerateCommand;
import com.yupi.cli.Command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * 命令执行器
 */
@Command(name = "RngAd33", description = "", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ListCommand());
    }

    @Override
    public void run() {
        // 未输入命令时给出提示
        System.out.println("请输入具体命令，或者输入 --help 查看帮助手册");
    }

    /**
     * 执行命令
     * @param args
     * @return
     */
    public Integer doExecute(String[] args) {
        return commandLine.execute(args);
    }

}