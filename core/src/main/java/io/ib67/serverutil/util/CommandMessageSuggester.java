package io.ib67.serverutil.util;

import io.ib67.util.bukkit.ColoredString;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class CommandMessageSuggester {
    private CommandMessageSuggester() {
    }

    public static BaseComponent[] from(String command, String description) {
        var cmd = command;
        var desc = description;
        var a = MessageHoverUtil.clickableCommandMessage(cmd, " &b" + cmd);
        var b = TextComponent.fromLegacyText(ColoredString.from(" &f--").append(desc).toString());
        var c = new ArrayList<>(List.of(a));
        c.addAll(List.of(b));
        return c.toArray(new BaseComponent[0]);
    }
}
