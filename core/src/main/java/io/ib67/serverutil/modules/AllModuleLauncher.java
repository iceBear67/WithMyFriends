package io.ib67.serverutil.modules;

import com.google.auto.service.AutoService;
import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import io.ib67.util.bukkit.Log;

@AutoService(IModule.class)
public class AllModuleLauncher implements IModule {
    @Override
    public String name() {
        return "all";
    }

    @Override
    public String description() {
        return "A module use to enable ALL modules discovered in the server. Won't do anything when disabling.";
    }

    @Override
    public IModule register() {
        return this;
    }

    @Override
    public void enable() {
        Log.info("Enabling all modules.");
        var m = WithMyFriends.getInstance().getModuleManager();
        m.getModules()
                .filter(e -> !"all".equals(e.getModule().name()))
                .forEach(e -> m.enableModule(e.getModule().name()));
    }

    @Override
    public void disable() {

    }
}
