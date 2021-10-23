package withmyfriends.basic.modules;

import com.google.auto.service.AutoService;
import io.ib67.serverutil.AbstractModule;
import io.ib67.serverutil.AbstractModuleConfig;
import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import io.ib67.util.bukkit.ColoredString;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.HashSet;
import java.util.regex.Pattern;

@AutoService(IModule.class)
public class At extends AbstractModule<At.AtConfig> implements Listener {
    private static final Pattern AT_REGEX = Pattern.compile("@[A-Za-z0-9_]+");

    @Override
    public String name() {
        return "basic:at";
    }

    @Override
    public String description() {
        return "@(AT) other players.";
    }

    @Override
    public IModule<AtConfig> register() {
        Bukkit.getPluginManager().registerEvents(this, WithMyFriends.getInstance());
        return this;
    }

    @Override
    public void enable() {
        if (getConfig() == null) {
            System.out.println("Save");
            saveConfig(new AtConfig());
        }
        System.out.println(getConfig());
        System.out.println(getConfig().hashCode());
        super.enable();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent chatEvent) {
        if (!isEnabled()) return;
        if (chatEvent.getMessage().startsWith("/")) {
            return;
        }
        var msg = chatEvent.getMessage();
        var match = AT_REGEX.matcher(msg);
        var atedPlayers = new HashSet<>(); // wtf is this name
        while (match.find()) {
            var at = match.group();
            var player = Bukkit.getPlayer(at.replaceFirst("@", ""));
            if (player == null) {
                continue;
            }
            if (!atedPlayers.contains(at)) {
                atedPlayers.add(at);
                if (getConfig().renderColor)
                    msg = msg.replaceAll(at, ColoredString.of(String.format(getConfig().AtFormat, player.getDisplayName())));
            }
            if (getConfig().sound) {
                player.playSound(player.getLocation(), getConfig().soundType, getConfig().soundVolume, getConfig().soundPitch);
            }
        }
        chatEvent.setMessage(msg);
    }

    @ConfigSerializable
    @ToString
    public static class AtConfig extends AbstractModuleConfig {
        @Comment("Should we play sound for the notified player?")
        private boolean sound = true;
        @Comment("Sound type.")
        private Sound soundType = Sound.valueOf("BLOCK_NOTE_BELL");
        @Comment("Sound volume")
        private float soundVolume = 5.0F;
        @Comment("Sound pitch")
        private float soundPitch = 10.0F;
        @Comment("Should we render color for names?")
        private boolean renderColor = true;
        @Comment("Colors for @ATs")
        private String AtFormat = "&b @%s &r";
    }
}
