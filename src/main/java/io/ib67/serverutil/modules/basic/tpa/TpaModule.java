package io.ib67.serverutil.modules.basic.tpa;

import com.google.auto.service.AutoService;
import io.ib67.serverutil.CommandHolder;
import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import io.ib67.serverutil.util.CommandMessageSuggester;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@AutoService(IModule.class)
public class TpaModule implements IModule, Listener {
    private final List<Request> requestList = new ArrayList<>();
    private boolean enabled;

    @Override
    public String name() {
        return "tpa";
    }

    @Override
    public String description() {
        return "Support /tpa or /tpahere command.";
    }

    @Override
    public IModule register() {
        Bukkit.getServer().getPluginManager().registerEvents(this, WithMyFriends.getInstance());
        WithMyFriends.getInstance().registerCommand(this, "tpa", CommandHolder.builder()
                .description("tpa to player")
                .handler((args, sender) -> {
                    if (!(sender instanceof Player)) {
                        return;
                    }
                    if (args.size() == 0) {
                        sender.spigot().sendMessage(CommandMessageSuggester.from("&b/tpa <player> &f--Request a teleportation."));
                    }
                    if (args.size() == 1) {
                        String playerName = args.poll();
                        Player requesterPlayer = (Player) sender;
                        Player inviteePlayer = Bukkit.getPlayer(playerName);
                        if (inviteePlayer == null) {
                            sender.sendMessage(ChatColor.RED + "Player not found!");
                            return;
                        }
                        Request request = new Request(Request.Type.TPA, requesterPlayer.getUniqueId(), inviteePlayer.getUniqueId());
                        requestList.add(request);
                        requesterPlayer.sendMessage("TPA Request was sent.");
                        requesterPlayer.sendMessage(request.toString());
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
