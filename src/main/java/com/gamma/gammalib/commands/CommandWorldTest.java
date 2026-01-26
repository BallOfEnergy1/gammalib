package com.gamma.gammalib.commands;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class CommandWorldTest extends CommandBase {

    @Override
    public String getCommandName() {
        return "world_test";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/world_test stage [stage_index]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return super.addTabCompletionOptions(sender, args);
    }
}
