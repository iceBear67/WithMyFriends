package io.ib67.serverutil;

import com.google.gson.*;
import io.ib67.Util;
import io.ib67.util.Pair;
import io.ib67.util.bukkit.Log;
import io.ib67.util.serialization.SimpleConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class WithMyFriends extends JavaPlugin implements Listener {
    private SimpleConfig<Config> wrappedConfig;
    private SimpleConfig<ModuleConfig> wrappedModuleConfig;
    @Getter
    private ModuleManager moduleManager;
    private Map<String, Pair<IModule, CommandHolder>> commandMap = new HashMap<>();
    private Map<String, Pair<IModule, CommandHolder>> rootCommandMap = new HashMap<>();

    public static WithMyFriends getInstance() {
        return WithMyFriends.getPlugin(WithMyFriends.class); // stop using stupid instance=this.
    }

    @Override
    public void onEnable() {
        // Load Configuration
        getDataFolder().mkdirs();
        wrappedConfig = new SimpleConfig<>(getDataFolder(), Config.class);
        wrappedConfig.saveDefault();
        wrappedConfig.reloadConfig();

        wrappedModuleConfig = new SimpleConfig<>(getDataFolder(), ModuleConfig.class, new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeHierarchyAdapter(AbstractModuleConfig.class, new AbstractModuleConfig.Adapter())
                .create());
        wrappedModuleConfig.setConfigFileName("modules.json");
        wrappedModuleConfig.saveDefault();
        wrappedModuleConfig.reloadConfig();

        Objects.requireNonNull(getCommand("wmf")).setExecutor(new CommandExecutor());
        // Load modules
        moduleManager = new ModuleManager(getMainConfig().getEnabledModules());
        moduleManager.loadModules();

        wrappedModuleConfig.saveConfig();
        if (getMainConfig().isUpdateCheck()) runUpdateCheck();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        wrappedConfig.saveConfig();
        wrappedModuleConfig.saveConfig();
    }

    public ModuleConfig getModuleConfig() {
        return wrappedModuleConfig.get();
    }

    public void registerCommand(IModule module, String command, CommandHolder holder) {
        commandMap.put(command, Pair.of(module, holder));
    }

    public void registerRootCommand(IModule module, String command, CommandHolder holder) {
        rootCommandMap.put(command, Pair.of(module, holder));
    }

    public Optional<CommandHolder> getCommandHolder(String command) {
        var a = commandMap.get(command);
        if (a == null) {
            return Optional.empty();
        }
        if (!moduleManager.isModuleActive(a.key.name())) {
            return Optional.empty();
        }
        return Optional.of(a.value);
    }

    public Optional<CommandHolder> getRootCommandHolder(String command) {
        var a = rootCommandMap.get(command);
        if (a == null) {
            return Optional.empty();
        }
        if (!moduleManager.isModuleActive(a.key.name())) {
            return Optional.empty();
        }
        return Optional.of(a.value);
    }


    public Set<String> registeredModuleCommands() {
        return commandMap.entrySet().stream().filter(e -> moduleManager.isModuleActive(e.getValue().key.name())).map(e -> e.getKey()).collect(Collectors.toUnmodifiableSet());
    }

    public Config getMainConfig() {
        return wrappedConfig.get();
    }

    @EventHandler //todo we need a better way to do this.
    public void onChat(AsyncPlayerChatEvent chatEvent) {
        var msg = chatEvent.getMessage();
        var i = msg.indexOf(' ');
        if (i == -1) {
            return;
        }
        var prefix = msg.substring(i).replaceFirst("/", "").toLowerCase(Locale.ROOT);
        var args = new LinkedList<>(List.of(msg.substring(i, msg.length() - 1).split(" ")));
        WithMyFriends.getInstance().getRootCommandHolder(prefix).ifPresentOrElse(commandHolder -> commandHolder.getHandler().accept(args, chatEvent.getPlayer()), () -> {
            chatEvent.getPlayer().sendMessage(" &c&lError! &r&f未知命令.");
        });
        chatEvent.setCancelled(true);

    }

    private void runUpdateCheck() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            // Check update sry
            JsonParser parser = new JsonParser();
            try {
                URL update_url = new URI("https://api.github.com/repos/iceBear67/WithMyFriends/releases").toURL();
                try (InputStream is = update_url.openConnection().getInputStream()) {
                    JsonElement je = parser.parse(Util.readAll(is));
                    JsonArray updates = je.getAsJsonArray();
                    for (JsonElement jz : updates) {
                        JsonObject jo = jz.getAsJsonObject();
                        if ("main".equals(jo.get("target_commitish").getAsString())) {
                            String ver = jo.get("tag_name").getAsString();
                            Objects.requireNonNull(ver);
                            if (ver.equals("v" + getDescription().getVersion())) {
                                // latest version is me!
                                break;
                            } else {
                                String msg = "New version of WithMyFriends available! " + ChatColor.AQUA + ChatColor.UNDERLINE + "https://github.com/iceBear67/WithMyFriends/releases/tag/" + ver;
                                Bukkit.getOnlinePlayers().stream().filter(e -> e.isOp()).forEach(e -> e.sendMessage(ChatColor.GREEN + msg));
                                Log.warn(msg);
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.warn("Update check failed: " + e.getLocalizedMessage());
                }
            } catch (MalformedURLException | URISyntaxException e) {
                e.printStackTrace();
            }

        }, 0L, 600 * 20L);
    }
}
