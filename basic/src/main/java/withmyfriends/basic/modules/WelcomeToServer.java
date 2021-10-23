package withmyfriends.basic.modules;

import com.google.auto.service.AutoService;
import io.ib67.serverutil.AbstractModule;
import io.ib67.serverutil.AbstractModuleConfig;
import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import io.ib67.util.bukkit.ColoredString;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@AutoService(IModule.class)
public class WelcomeToServer extends AbstractModule<WelcomeToServer.WelcomeConfig> implements Listener {
    private boolean enabled;

    @Override
    public String name() {
        return "basic:welcome";
    }

    @Override
    public String description() {
        return "Welcome your new player";
    }

    @Override
    public IModule<WelcomeConfig> register() {
        Bukkit.getServer().getPluginManager().registerEvents(this, WithMyFriends.getInstance());
        return this;
    }

    @Override
    public void enable() {
        if (getConfig() == null) {
            saveConfig(new WelcomeConfig());
        }
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enabled) return;
        if (!event.getPlayer().hasPlayedBefore()) {
            if (getConfig().newWelcome != null)
                event.setJoinMessage(ColoredString.of(String.format(getConfig().newWelcome, event.getPlayer().getDisplayName())));
            if (getConfig().newPMWelcome != null) {
                Bukkit.getScheduler().runTask(WithMyFriends.getInstance(), () -> event.getPlayer().sendMessage(ColoredString.of(getConfig().newPMWelcome)));
            }
            if (getConfig().overrideCommon) {
                return;
            }
        }
        if (getConfig().commonWelcome != null)
            Bukkit.broadcastMessage(ColoredString.of(String.format(getConfig().commonWelcome, event.getPlayer().getDisplayName())));
    }

    @ToString
    @ConfigSerializable
    public static class WelcomeConfig extends AbstractModuleConfig {
        @Comment("Message for new players ( Broadcast )")
        private String newWelcome = "&d&l Welcome! &r&fIt's the first time that %s have joined our server!";
        @Comment("Message for new players ( Private Message )")
        private String newPMWelcome = "&b Welcome! Make sure that you've checked our rules!";
        @Comment("Message for players")
        private String commonWelcome = "&a&l + &r&f%s";
        @Comment("Does new welcome overrides common?")
        private boolean overrideCommon = true;
    }

}
