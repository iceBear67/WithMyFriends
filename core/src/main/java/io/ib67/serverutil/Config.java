package io.ib67.serverutil;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;

@Getter
@ConfigSerializable
public class Config {
    @Comment("Should we check for updates every 10 minutes?")
    private boolean updateCheck = true;
    @Comment("Modules to be enabled. Use `all` for enabling all modules.")
    private List<String> enabledModules = List.of("all");
}
