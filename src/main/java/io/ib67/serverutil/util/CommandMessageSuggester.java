package io.ib67.serverutil.util;

import io.ib67.util.bukkit.ColoredString;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class CommandMessageSuggester {
    private CommandMessageSuggester() {
    }

    public static BaseComponent[] from(String message) {
        String[] arr = message.split(" &f--");
        var cmd = ColoredString.stripColors(arr[0]);
        var desc = arr[1];
        var a = MessageHoverUtil.clickableCommandMessage(cmd, arr[0]);
        var b = TextComponent.fromLegacyText(ColoredString.from(" &f--").append(desc).toString());
        var c = new ArrayList<>(List.of(a));
        c.addAll(List.of(b));
        return c.toArray(new BaseComponent[0]);
    }
}
