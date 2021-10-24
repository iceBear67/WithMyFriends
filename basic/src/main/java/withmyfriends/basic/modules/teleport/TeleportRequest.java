package withmyfriends.basic.modules.teleport;

import com.google.auto.service.AutoService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.ib67.serverutil.*;
import io.ib67.util.bukkit.ColoredString;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@AutoService(IModule.class)
public class TeleportRequest extends AbstractModule<TeleportRequest.Config> {
    private Cache<UUID, Request> requests;

    @Override
    public String name() {
        return "basic:tprequest";
    }

    @Override
    public String description() {
        return "Request a player to teleport to him or teleport them to ourselves.";
    }

    @Override
    public IModule<TeleportRequest.Config> register() {
        WithMyFriends.getInstance().registerRootCommand(this, "tpa",
                CommandHolder.builder()
                        .description("Send a request of teleporting to them to a player.")
                        .handler(this::handleTPA)
                        .build());

        WithMyFriends.getInstance().registerRootCommand(this, "tphere",
                CommandHolder.builder()
                        .description("Send a request of teleporting them to your position to a player.")
                        .handler(this::handleTPHere)
                        .build());

        WithMyFriends.getInstance().registerRootCommand(this, "tpaccept",
                CommandHolder.builder()
                        .description("Allow the teleporting request.")
                        .handler(this::handleTPAccept)
                        .build());

        WithMyFriends.getInstance().registerRootCommand(this, "tpdeny",
                CommandHolder.builder()
                        .description("Deny the teleporting request.")
                        .handler(this::handleTPDeny)
                        .build());
        return this;
    }

    private void handleTPA(Queue<String> args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColoredString.of(getConfig().playerOnly));
            return;
        }

        if (args.size() != 1 || args.peek().isEmpty()) {
            sender.sendMessage(ColoredString.of(getConfig().noTarget));
            return;
        }

        String playerName = args.poll();
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(ColoredString.of(getConfig().notOnline));
            return;
        }

        Player senderPlayer = (Player) sender;
        requests.put(senderPlayer.getUniqueId(), new Request(player.getUniqueId(), false));

        player.sendMessage(ColoredString.of(String.format(getConfig().onTPRequest, senderPlayer.getName())));
        senderPlayer.sendMessage(ColoredString.of(String.format(getConfig().requestSent, playerName)));
    }

    private void handleTPHere(Queue<String> args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColoredString.of(getConfig().playerOnly));
            return;
        }

        if (args.size() != 1 || args.peek().isEmpty()) {
            sender.sendMessage(ColoredString.of(getConfig().noTarget));
            return;
        }

        String playerName = args.poll();
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(ColoredString.of(getConfig().notOnline));
            return;
        }

        Player senderPlayer = (Player) sender;
        requests.put(senderPlayer.getUniqueId(), new Request(player.getUniqueId(), true));

        player.sendMessage(ColoredString.of(String.format(getConfig().onTPHereRequest, senderPlayer.getName())));
        senderPlayer.sendMessage(ColoredString.of(String.format(getConfig().requestSent, playerName)));
    }

    private void handleTPAccept(Queue<String> args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColoredString.of(getConfig().playerOnly));
            return;
        }

        Player player = (Player) sender;

        for (Map.Entry<UUID, Request> entry : requests.asMap().entrySet()) {
            Request request = entry.getValue();

            if (request.target == player.getUniqueId()) {
                Player inviter = Bukkit.getPlayer(entry.getKey());

                if (inviter == null) {
                    continue;
                }

                player.sendMessage(ColoredString.of(getConfig().accepted));
                inviter.sendMessage(ColoredString.of(getConfig().successful));

                if (!request.isTPHere) {
                    player.teleport(inviter);
                } else {
                    inviter.teleport(player);
                }
                
                requests.invalidate(inviter.getUniqueId());
                return;
            }
        }

        sender.sendMessage(ColoredString.of(getConfig().noRequestAvailable));
    }

    private void handleTPDeny(Queue<String> args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColoredString.of(getConfig().playerOnly));
            return;
        }

        Player player = (Player) sender;

        for (Map.Entry<UUID, Request> entry : requests.asMap().entrySet()) {
            Request request = entry.getValue();

            if (request.target == player.getUniqueId()) {
                Player inviter = Bukkit.getPlayer(entry.getKey());

                if (inviter == null) {
                    continue;
                }

                player.sendMessage(ColoredString.of(getConfig().denied));
                inviter.sendMessage(ColoredString.of(getConfig().failed));
                requests.invalidate(inviter.getUniqueId());
                return;
            }
        }

        sender.sendMessage(ColoredString.of(getConfig().noRequestAvailable));
    }

    @Override
    public void enable() {
        if (getConfig() == null) {
            saveConfig(new Config());
        }

        requests = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .expireAfterWrite(getConfig().requestExpireSeconds, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void disable() {
    }

    @RequiredArgsConstructor
    public static class Request {
        private final UUID target;
        private final boolean isTPHere;
//        private long createTime = System.currentTimeMillis();
    }

    @ToString
    @ConfigSerializable
    public class Config extends AbstractModuleConfig {
        @Comment("When request expire. (Seconds)")
        private long requestExpireSeconds = 60;
        @Comment("Message for /tpa or /tphere have no tagert.")
        private String noTarget = "&cPlease select a target player.";
        @Comment("Message for target player is offline.")
        private String notOnline = "&cTarget player is offline.";
        @Comment("Message for command block or console runs command.")
        private String playerOnly = "&cOnly players can use this command.";
        @Comment("Message for receiving a tp request.")
        private String onTPRequest = "&ePlayer %d wants to teleport to your position. \n" +
                "You have %s seconds to key in /tpaccept to accept the request. \n" +
                "Or key in /tpdeny to cancel the request.";
        @Comment("Message for receiving a tp here request.")
        private String onTPHereRequest = "&ePlayer %d wants to teleport you to his position. \n" +
                "You have %s seconds to key in /tpaccept to accept the request. \n" +
                "Or key in /tpdeny to cancel the request.";
        @Comment("Message for sent request.")
        private String requestSent = "&eYour request have been sent to %d.";
        @Comment("Message for no request available.")
        private String noRequestAvailable = "&cYou have no unconfirmed teleport request.";
        @Comment("Message for accepted.")
        private String accepted = "&cYou just accepted the request. Teleporting, Woo~Ho.";
        @Comment("Message for denied.")
        private String denied = "&cYou just denied the request. Nothing will be take place.";
        @Comment("Message for invite successful.")
        private String successful = "&cYou just accepted the request. Teleporting, Woo~Ho.";
        @Comment("Message for invite failed.")
        private String failed = "&cYou just denied the request. Nothing will be take place.";

    }
}
