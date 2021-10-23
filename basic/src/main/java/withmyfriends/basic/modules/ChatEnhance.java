/*
 * MIT License
 *
 * Copyright (c) 2021 SaltedFish Club
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package withmyfriends.basic.modules;

import io.ib67.serverutil.AbstractModule;
import io.ib67.serverutil.IModule;
import io.ib67.serverutil.WithMyFriends;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author EvanLuo42
 * @date 2021/10/23 4:17 下午
 */
public class ChatEnhance extends AbstractModule implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent chatEvent) {
        if (chatEvent.getMessage().startsWith("/")) {
            return;
        }

        TextComponent hoverAction = new TextComponent();
        hoverAction.setText(chatEvent.getPlayer().getDisplayName() + "\n" + chatEvent.getPlayer().getLocale());
        // TODO
        ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "menu");
        TextComponent message = new TextComponent();
        message.setText(chatEvent.getPlayer().getDisplayName() + ": ");
        message.setColor(ChatColor.WHITE);
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverAction.getHoverEvent().getContents()));
        message.setClickEvent(click);
        TextComponent content = new TextComponent();
        content.setText(chatEvent.getMessage());
        content.setColor(ChatColor.GRAY);
        message.addExtra(content);

        chatEvent.setCancelled(true);

        Bukkit.getServer().getOnlinePlayers().forEach(p -> {
            p.spigot().sendMessage(message);
        });
    }

    @Override
    public String name() {
        return "basic:chat_enhance";
    }

    @Override
    public String description() {
        return "Enhance the raw Minecraft chat function.";
    }

    @Override
    public IModule register() {
        Bukkit.getPluginManager().registerEvents(this, WithMyFriends.getInstance());
        return this;
    }
}
