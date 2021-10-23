package withmyfriends.basic.modules;

import com.google.auto.service.AutoService;
import io.ib67.serverutil.AbstractModule;
import io.ib67.serverutil.CommandHolder;
import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import io.ib67.util.bukkit.ColoredString;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Queue;

@AutoService(IModule.class)
public class Teleport extends AbstractModule {
    @Override
    public String name() {
        return "basic:tp";
    }

    @Override
    public String description() {
        return "Teleport to players directly.";
    }

    @Override
    public IModule register() {
        WithMyFriends.getInstance().registerRootCommand(this,
                "tp",
                CommandHolder.builder()
                        .description(description())
                        .handler(this::commandHandler)
                        .build());
        return this;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    private void commandHandler(Queue<String> args, CommandSender sender) {
        if (args.size() == 0) {
            sender.sendMessage(ColoredString.of(" &c Missing Arguments: &f player"));
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColoredString.of("You have to be a player first."));
            return;
        }
        var name = args.poll();
        var destination = Bukkit.getPlayer(name);
        if (destination == null) {
            sender.sendMessage(ColoredString.of(" &c&l" + name + "&r&c isn't online"));
            return;
        }
        ((Player) sender).teleport(destination);
        sender.sendMessage("&b Teleported!");
    }
}
