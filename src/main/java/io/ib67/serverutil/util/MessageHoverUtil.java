package io.ib67.serverutil.util;

import io.ib67.util.bukkit.ColoredString;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class MessageHoverUtil {
    private static final HoverEvent HOVER_TIP = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Click this to fill the box!"));

    private MessageHoverUtil() {
    }

    public static BaseComponent[] clickableCommandMessage(String command, String message) {
        BaseComponent[] it = TextComponent.fromLegacyText(ColoredString.of(message));
        ClickEvent event = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command.trim());
        for (BaseComponent baseComponent : it) {
            baseComponent.setHoverEvent(HOVER_TIP);
            baseComponent.setClickEvent(event);
        }
        return it;
    }

    public static BaseComponent[] messageHover(String message, String hoverText) {
        BaseComponent[] it = TextComponent.fromLegacyText(ColoredString.of(message));
        var hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ColoredString.of(hoverText)));
        ;
        for (BaseComponent baseComponent : it) {
            baseComponent.setHoverEvent(hover);
        }
        return it;
    }
}
