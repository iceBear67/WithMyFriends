package withmyfriends.basic.modules;

import com.google.auto.service.AutoService;
import io.ib67.serverutil.AbstractModule;
import io.ib67.serverutil.AbstractModuleConfig;
import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import lombok.Data;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@AutoService(IModule.class)
public class WelcomeToServer extends AbstractModule<WelcomeToServer.TestConfig> implements Listener {
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
        if (getConfig(TestConfig.class) == null) {
            saveConfig(new TestConfig());
        }
        System.out.println(getConfig(TestConfig.class));
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

    @ToString
    @ConfigSerializable
    @Data
    public static class TestConfig extends AbstractModuleConfig {
        @Comment("who is sb?")
        private String sb = "nc";
    }

}
