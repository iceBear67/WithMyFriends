package io.ib67.serverutil.modules;

import com.google.auto.service.AutoService;
import io.ib67.serverutil.AbstractModule;
import io.ib67.serverutil.CommandHolder;
import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import io.ib67.util.bukkit.ColoredString;

@AutoService(IModule.class)
public class Reloader extends AbstractModule {
    @Override
    public String name() {
        return "reloader";
    }

    @Override
    public String description() {
        return "Reload  WithMyFriends modules";
    }

    @Override
    public IModule register() {
        WithMyFriends.getInstance().registerCommand(this, "reload", CommandHolder.builder()
                .description("Reload All Configurations for Modules")
                .handler((arg, sender) -> {
                    if (arg.size() == 0) {
                        sender.sendMessage(ColoredString.of(" &cUnknown Operation. Usage: &f/util reload <all/config>"));
                        return;
                    }
                    var mode = arg.poll();
                    switch (mode) {
                        case "all":
                            WithMyFriends.getInstance().getModuleConfig().reload();
                            WithMyFriends.getInstance().getModuleManager().getModules()
                                    .filter(e -> !(e.getModule() instanceof AllModuleLauncher)).forEach(e -> {
                                        WithMyFriends.getInstance().getModuleManager().reloadModule(e.getModule().name());
                                    });
                            break;
                        case "config":
                            WithMyFriends.getInstance().getModuleConfig().reload();
                            break;
                        default:
                            sender.sendMessage(ColoredString.of(" &cUnknown Operation. Availables: &fall, config"));
                    }
                }).build()
        );
        return this;
    }
}
