package withmyfriends.basic.modules.teleport;

import com.google.auto.service.AutoService;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.ib67.serverutil.AbstractModule;
import io.ib67.serverutil.CommandHolder;
import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;

@AutoService(IModule.class)
public class TeleportRequest extends AbstractModule {
    private BiMap<UUID, Request> requests = HashBiMap.create(Bukkit.getMaxPlayers());

    @Override
    public String name() {
        return "basic:tprequest";
    }

    @Override
    public String description() {
        return "Request a player to teleport to him or teleport them to ourselves.";
    }

    @Override
    public IModule register() {
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

    }

    private void handleTPHere(Queue<String> args, CommandSender sender) {

    }

    private void handleTPAccept(Queue<String> args, CommandSender sender) {

    }

    private void handleTPDeny(Queue<String> args, CommandSender sender) {

    }

    @Override
    public void enable() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(WithMyFriends.getInstance(), () -> {
            for (Map.Entry<UUID, Request> entry : requests.entrySet()) {

            }
        }, 0L, 1000L);
    }

    @Override
    public void disable() {
    }

    public class Request {
        public final UUID target;
        public final boolean isTPHere;
        public final long createTime;

        public Request(UUID targetIn, boolean isTPHereIn) {
            target = targetIn;
            isTPHere = isTPHereIn;

            createTime = System.currentTimeMillis();
        }
    }
}
