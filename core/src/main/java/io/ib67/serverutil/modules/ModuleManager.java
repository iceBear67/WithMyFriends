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

import java.util.Arrays;
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
                commandSender.spigot().sendMessage(CommandMessageSuggester.from("/util manager", "Show this"));
                commandSender.spigot().sendMessage(CommandMessageSuggester.from("/util manager enable <module>", "Enable X Module"));
                commandSender.spigot().sendMessage(CommandMessageSuggester.from("/util manager disable <module>", "Disable X Module"));
                commandSender.spigot().sendMessage(CommandMessageSuggester.from("/util manager mods ", "List Modules"));
                return;
            }
            var rootArg = strings.poll();
            if (Arrays.stream(OPType.values()).anyMatch(e -> e.cmdArg.equalsIgnoreCase(rootArg))) {
                if (strings.size() != 1) {
                    commandSender.sendMessage(ColoredString.of(" &cNot enough arguments!"));
                    return;
                }
                var moduleName = strings.poll();
                var moduleManager = WithMyFriends.getInstance().getModuleManager();
                if (!moduleManager.isModuleAvailable(moduleName)) {
                    commandSender.sendMessage(ColoredString.of(" &cModule " + moduleName + " not exists!"));
                    return;
                }
                switch (OPType.valueOf(rootArg)) {
                    case ENABLE:
                        if (moduleManager.isModuleActive(moduleName)) {
                            commandSender.sendMessage(ColoredString.of(" &cModule " + moduleName + " is already enabled!"));
                            return;
                        }
                        moduleManager.enableModule(moduleName);
                        commandSender.sendMessage(ColoredString.of(" &f Module &b" + moduleName + "&f has enabled."));
                        break;
                    case DISABLE:
                        if (!moduleManager.isModuleActive(moduleName)) {
                            commandSender.sendMessage(ColoredString.of(" &cModule " + moduleName + " is already disabled!"));
                            return;
                        }
                        moduleManager.disableModule(moduleName);
                        commandSender.sendMessage(ColoredString.of(" &f Module &b" + moduleName + "&f has disabled."));
                    case RELOAD:
                        if (moduleManager.isModuleActive(moduleName)) {
                            moduleManager.disableModule(moduleName);
                            moduleManager.enableModule(moduleName);
                        } else {
                            moduleManager.enableModule(moduleName);
                            moduleManager.disableModule(moduleName);
                        }
                        commandSender.sendMessage(ColoredString.of(" &f Module &b" + moduleName + "&f has reloaded."));
                }
            }
            if (rootArg.equals("mods")) {
                commandSender.sendMessage(ColoredString.of(" &fAvailable Modules:"));
                var manager = WithMyFriends.getInstance().getModuleManager();
                manager.getModules().forEachOrdered(p -> {
                    ChatColor status = p.isEnabled() ? ChatColor.GREEN : ChatColor.RED;
                    var intro = ColoredString.from(" ")
                            .append(status.toString())
                            .append(POINT + "")
                            .append(" ").append(p.getModule().name())
                            .append(" &8").append(p.getModule().description()).toString();
                    commandSender.spigot().sendMessage(MessageHoverUtil.clickableCommandMessage("/util manager " + (p.isEnabled() ? "disable" : "enable") + " " + p.getModule().name(), intro));

                });
            }

        }

        private enum OPType {
            RELOAD("reload"), ENABLE("enable"), DISABLE("disable");
            String cmdArg;

            OPType(String cmdArg) {
                this.cmdArg = cmdArg;
            }
        }
    }
}
