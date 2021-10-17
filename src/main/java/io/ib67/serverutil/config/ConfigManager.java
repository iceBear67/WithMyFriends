package io.ib67.serverutil.config;

import lombok.SneakyThrows;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;


public final class ConfigManager<T> {
    private CommentedConfigurationNode moduleConfigRoot;
    private HoconConfigurationLoader moduleConfigLoader;

    @SneakyThrows
    public ConfigManager(Path confFile) {
        this.moduleConfigLoader = HoconConfigurationLoader.builder()
                .path(confFile)
                .build();
        moduleConfigRoot = moduleConfigLoader.load();
    }

    @SneakyThrows
    public void save() {
        moduleConfigLoader.save(moduleConfigRoot);
    }

    @SneakyThrows
    public <A extends T> A getConfig(String name, Class<A> configClass) {
        var node = moduleConfigRoot.node(name.replaceAll(":", "_")).get(configClass);
        return node;
    }

    @SneakyThrows
    public void saveConfig(String name, T config) {
        moduleConfigRoot.node(name.replaceAll(":", "_")).set(config);
    }

}

