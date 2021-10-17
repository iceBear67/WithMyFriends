package io.ib67.serverutil;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.Queue;
import java.util.function.BiConsumer;

@Builder
@Getter
public class CommandHolder {
    private final BiConsumer<Queue<String>, CommandSender> handler;
    private final String description;
}
