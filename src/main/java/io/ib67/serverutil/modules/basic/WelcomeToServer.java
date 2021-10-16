package io.ib67.serverutil.modules.basic;

import com.google.auto.service.AutoService;
import io.ib67.serverutil.AbstractModuleConfig;
import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AutoService(IModule.class)
public class WelcomeToServer implements IModule<WelcomeToServer.TestConfig>, Listener {
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
        System.out.println(getConfig());
        if (getConfig() == null) {
            saveConfig(new TestConfig());
        } else {
            System.out.println(getConfig().sb);
        }
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

    @Getter
    @ToString
    public static class TestConfig extends AbstractModuleConfig {
        int i = 1;
        String sb = "nc";
    }
}
