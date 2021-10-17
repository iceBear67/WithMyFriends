package withmyfriends.basic;

import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import org.bukkit.plugin.java.JavaPlugin;
import withmyfriends.basic.modules.WelcomeToServer;

import java.util.ServiceLoader;

public class PluginInitializer extends JavaPlugin {
    @Override
    public void onEnable() {
        ServiceLoader.load(IModule.class, WelcomeToServer.class.getClassLoader())
                .stream()
                .map(ServiceLoader.Provider::get)
                .forEach(WithMyFriends.getInstance().getModuleManager()::registerModule);
    }
}
