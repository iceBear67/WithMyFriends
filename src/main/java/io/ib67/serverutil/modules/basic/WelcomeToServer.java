package io.ib67.serverutil.modules.basic;

import com.google.auto.service.AutoService;
import io.ib67.serverutil.AbstractModuleConfig;
import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AutoService(IModule.class)
public class WelcomeToServer implements IModule<AbstractModuleConfig>, Listener {
    private boolean enabled;

    @Override
    public String name() {
        return "welcome";
    }

    @Override
    public String description() {
        return "Welcome your new player";
    }

    @Override
    public IModule register() {
        Bukkit.getServer().getPluginManager().registerEvents(this, WithMyFriends.getInstance());
        return this;
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            event.setJoinMessage("&d A new player " + event.getPlayer().getDisplayName() + " has joined our server!");
        }
    }

}
