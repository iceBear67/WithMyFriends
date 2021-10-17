package io.ib67.serverutil;

import io.ib67.serverutil.util.CommandMessageSuggester;
import io.ib67.util.bukkit.ColoredString;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {
    private static boolean sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ColoredString.of(" &f&m-----&r &bWith My Friends &r&f&m-----"));
        sender.sendMessage(ColoredString.of(" &b/util &f--Show this"));
        for (String registeredModuleCommand : WithMyFriends.getInstance().registeredModuleCommands()) {
            CommandHolder holder = WithMyFriends.getInstance().getCommandHolder(registeredModuleCommand).orElseThrow(AssertionError::new);
            //sender.spigot().sendMessage(CommandMessageSuggester.from(" &b/util " + registeredModuleCommand + " &f--" + holder.getDescription()));
            sender.spigot().sendMessage(CommandMessageSuggester.from("/util " + registeredModuleCommand, holder.getDescription()));
        }
        sender.sendMessage(ColoredString.of(" &f&m-----&r by &aiceBear67 &f&m-----"));
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Queue<String> arg = new LinkedList<>(List.of(args));
        if (arg.size() == 0) {
            return sendHelpMessage(sender);
        }
        WithMyFriends.getInstance().getCommandHolder(arg.poll()).ifPresentOrElse(commandHolder -> commandHolder.getHandler().accept(arg, sender), () -> {
            sender.sendMessage(" &c&lError! &r&f未知命令.");
        });
        return true;
    }

}
