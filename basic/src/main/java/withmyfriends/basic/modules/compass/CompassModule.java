package withmyfriends.basic.modules.compass;

import com.google.auto.service.AutoService;
import io.ib67.serverutil.*;
import io.ib67.util.bukkit.ColoredString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.Queue;

@AutoService(IModule.class)
public class CompassModule extends AbstractModule<CompassModule.CompassConfig> {
    @Override
    public String name() {
        return "basic:compass";
    }

    @Override
    public String description() {
        return "Compass for tracking location or players";
    }

    @Override
    public void enable() {
        if (getConfig() == null) {
            saveConfig(new CompassConfig());
        }
        super.enable();
    }

    @Override
    public IModule register() {
        WithMyFriends.getInstance().registerRootCommand(this,
                "track",
                CommandHolder.builder()
                        .description("Track other players or location")
                        .handler(this::commandHandler)
                        .build());
        CompassManager.INSTANCE.init();
        return this;
    }

    private void commandHandler(Queue<String> args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColoredString.of(" &c You have to be a player for this."));
            return;
        }
        var player = (Player) sender;
        if (args.size() == 0) {
            sender.sendMessage(ColoredString.of(" &cInvalid Args! Available args: &f<hud/item> <player/location(x y z)>"));
            return;
        }
        var type = args.poll();
        Location location = null;
        Player target = null;
        if (args.size() >= 3) {
            // location.
            var x = Double.valueOf(args.poll());
            var y = Double.valueOf(args.poll());
            var z = Double.valueOf(args.poll());
            World world = player.getWorld();
            if (args.size() != 0) {
                var w = Bukkit.getWorld(args.poll());
                if (w == null) {
                    sender.sendMessage(ColoredString.of(" &c Invalid World Name"));
                }
            }
            location = new Location(world, x, y, z);
        } else if (args.size() == 1) {
            target = Bukkit.getPlayerExact(args.poll());
            if (target == null) {
                sender.sendMessage(ColoredString.of(" &cPlayer not exists."));
                return;
            }
        } else {
            sender.sendMessage(ColoredString.of(" &cInvalid Args! Available args: &f<hud/item> <player/location(x y z)>"));
            return;
        }
        if (CompassManager.INSTANCE.hasPlayer(player)) {
            sender.sendMessage(ColoredString.of(" &cYou're already tracking sth!"));
        }
        ICompass compass = type.equals("hud")
                ? (location == null && getConfig().allowTrackingPlayer
                ? new ActionBarPlayerCompass(target)
                : new ActionBarLocationCompass(location))
                : new WaypointLocationCompass(location);
        var holder = new CompassHolder(player.getUniqueId(), compass);
        CompassManager.INSTANCE.addHolder(player.getUniqueId(), holder);
        sender.sendMessage(ColoredString.of(" &aWe're tracking now!"));
    }

    @ConfigSerializable
    public static class CompassConfig extends AbstractModuleConfig {
        @Comment("Can we track players?")
        private boolean allowTrackingPlayer = true;
    }
}
