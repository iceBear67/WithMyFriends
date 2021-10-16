package io.ib67.serverutil.modules;

import com.google.auto.service.AutoService;
import io.ib67.serverutil.CommandHolder;
import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import io.ib67.serverutil.util.CommandMessageSuggester;
import io.ib67.serverutil.util.MessageHoverUtil;
import io.ib67.util.bukkit.ColoredString;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Queue;
import java.util.function.BiConsumer;

@AutoService(IModule.class)
public class ModuleManager implements IModule {
    private static final CommandHandler HANDLER = new CommandHandler();
    private static final char POINT = '\u25CF';

    @Override
    public String name() {
        return "manager";
    }

    @Override
    public String description() {
        return "Manager for modules";
    }

    @Override
    public IModule register() {
        WithMyFriends.getInstance().registerCommand(this, "manager", CommandHolder.builder()
                .description("Manage modules")
                .handler(HANDLER).build()
        );
        return this;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    private static class CommandHandler implements BiConsumer<Queue<String>, CommandSender> {

        @Override
        public void accept(Queue<String> strings, CommandSender commandSender) {
            if (strings.size() == 0) {
                // help menu again QAQ
                commandSender.spigot().sendMessage(CommandMessageSuggester.from(" &b/util manager &f--Show this"));
                commandSender.spigot().sendMessage(CommandMessageSuggester.from(" &b/util manager enable <module> &f--Enable X Module"));
                commandSender.spigot().sendMessage(CommandMessageSuggester.from(" &b/util manager disable <module> &f--Disable X Module"));
                commandSender.spigot().sendMessage(CommandMessageSuggester.from(" &b/util manager mods &f--List Modules"));
                return;
            }
            var rootArg = strings.poll();
            boolean enableCmd = rootArg.equalsIgnoreCase("enable");
            if (rootArg.equalsIgnoreCase("enable") || rootArg.equalsIgnoreCase("disable")) {
                if (strings.size() != 1) {
                    commandSender.sendMessage(" &cNot enough arguments!");
                    return;
                }
                var moduleName = strings.poll();
                var moduleManager = WithMyFriends.getInstance().getModuleManager();
                if (!moduleManager.isModuleAvailable(moduleName)) {
                    commandSender.sendMessage(" &cModule " + moduleName + " not exists!");
                    return;
                }
                if (enableCmd) {
                    if (moduleManager.isModuleActive(moduleName)) {
                        commandSender.sendMessage(" &cModule " + moduleName + " is already enabled!");
                        return;
                    }
                    moduleManager.enableModule(moduleName);
                } else {
                    if (!moduleManager.isModuleActive(moduleName)) {
                        commandSender.sendMessage(" &cModule " + moduleName + " is already disabled!");
                        return;
                    }
                    moduleManager.disableModule(moduleName);
                }
            }
            if (rootArg.equals("mods")) {
                commandSender.sendMessage(ColoredString.of(" &fAvailable Modules:"));
                var manager = WithMyFriends.getInstance().getModuleManager();
                manager.getModules().forEachOrdered(p -> {
                    ChatColor status = p.key ? ChatColor.GREEN : ChatColor.RED;
                    var intro = ColoredString.from(" ")
                            .append(status.toString())
                            .append(POINT + "")
                            .append(" ").append(p.value.name())
                            .append(" &8").append(p.value.description()).toString();
                    commandSender.spigot().sendMessage(MessageHoverUtil.clickableCommandMessage("/util manager " + (p.key ? "disable" : "enable") + " " + p.value.name(), intro));

                });
            }

        }
    }
}
