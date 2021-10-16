package io.ib67.serverutil.modules.basic.tpamodule;

import com.google.auto.service.AutoService;
import io.ib67.serverutil.CommandHolder;
import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiConsumer;

@AutoService(IModule.class)
public class TpaModule implements IModule, Listener {
    private boolean enabled;
    private final List<Request> requestList = new ArrayList<>();

    @Override
    public String name() {
        return "TpaModule";
    }

    @Override
    public String description() {
        return "support /tpa or /tpahere command.";
    }

    @Override
    public IModule register() {
        Bukkit.getServer().getPluginManager().registerEvents(this, WithMyFriends.getInstance());
        WithMyFriends.getInstance().registerCommand(this, "tpa", CommandHolder.builder()
                .description("tpa to player")
                .handler(new BiConsumer<Queue<String>, CommandSender>() {
                    @Override
                    public void accept(Queue<String> args, CommandSender sender) {
                        if (!(sender instanceof Player)) {
                            return;
                        }
                        if (args.size() == 0) {
                            sender.sendMessage("/tpa <playerName>");
                        }
                        if (args.size() == 1) {
                            String playerName = args.remove();
                            Player requesterPlayer = (Player) sender;
                            Player inviteePlayer = Bukkit.getPlayer(playerName);
                            if (inviteePlayer == null) {
                                sender.sendMessage(ChatColor.RED + "player not found!");
                                return;
                            }
                            Request request = new Request(Request.Type.TPA, requesterPlayer.getUniqueId(), inviteePlayer.getUniqueId());
                            requestList.add(request);
                            requesterPlayer.sendMessage("Tpa 请求创建成功！");
                            requesterPlayer.sendMessage(request.toString());
                        }
                    }
                }).build());
        return this;
    }

    @Override
    public void enable() {
        this.enabled = true;
    }

    @Override
    public void disable() {
        this.enabled = false;
    }
}
