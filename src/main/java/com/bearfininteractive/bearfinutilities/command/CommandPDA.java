package com.bearfininteractive.bearfinutilities.command;

import com.bearfininteractive.bearfinutilities.events.ChatEventHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CommandPDA extends CommandBase {

    @Override
    public String getName() {
        return "pda";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/pda mode";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP)) return;
        if (args.length == 0 || !args[0].equals("mode")) {
            sender.sendMessage(new TextComponentString("§cUsage: /pda mode"));
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;
        UUID uuid = player.getUniqueID();

        if (ChatEventHandler.PROXIMITY_MODE.contains(uuid)) {
            ChatEventHandler.PROXIMITY_MODE.remove(uuid);
            player.sendMessage(new TextComponentString("§bSwitched to §lglobal chat§r§b."));
        } else {
            ChatEventHandler.PROXIMITY_MODE.add(uuid);
            player.sendMessage(new TextComponentString("§aSwitched to §lproximity chat§r§a."));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "mode");
        }
        return Collections.emptyList();
    }
}
