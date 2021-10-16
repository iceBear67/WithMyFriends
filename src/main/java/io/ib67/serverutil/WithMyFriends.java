package io.ib67.serverutil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.ib67.Util;
import io.ib67.util.Pair;
import io.ib67.util.bukkit.Log;
import io.ib67.util.serialization.SimpleConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class WithMyFriends extends JavaPlugin {
    private SimpleConfig<Config> wrappedConfig;
    @Getter
    private ModuleManager moduleManager;
    private Map<String, Pair<IModule, CommandHolder>> commandMap = new HashMap<>();

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
        Objects.requireNonNull(getCommand("wmf")).setExecutor(new CommandExecutor());
        // Load modules
        moduleManager = new ModuleManager(getMainConfig().getEnabledModules());
        moduleManager.loadModules();
        wrappedConfig.saveConfig();
        if (getMainConfig().isUpdateCheck()) runUpdateCheck();
    }

    @Override
    public void onDisable() {
        wrappedConfig.saveConfig();
    }

    public void registerCommand(IModule module, String command, CommandHolder holder) {
        commandMap.put(command, Pair.of(module, holder));
    }

    public Optional<CommandHolder> getCommandHolder(String command) {
        return Optional.ofNullable(commandMap.get(command).value);
    }

    public Set<String> registeredModuleCommands() {
        return commandMap.entrySet().stream().filter(e -> moduleManager.isModuleActive(e.getValue().key.name())).map(e -> e.getKey()).collect(Collectors.toUnmodifiableSet());
    }

    public Config getMainConfig() {
        return wrappedConfig.get();
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
