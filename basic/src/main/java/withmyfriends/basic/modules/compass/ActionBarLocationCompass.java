package withmyfriends.basic.modules.compass;

import io.ib67.util.bukkit.ColoredString;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;

public class ActionBarLocationCompass implements ICompass {
    @Getter
    private Location destination;
    @Getter
    private String format = "&6%d %d %d &b| Distance: %f";

    /*
     * Empty constructor for subclassing.
     */
    public ActionBarLocationCompass() {

    }

    public ActionBarLocationCompass(Location loc) {
        this.destination = loc;
    }

    @Override
    public void onUpdate(CompassHolder holder) {
        holder.getAsPlayer().ifPresent(p -> {
            Location dest = getDestination();
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                    ColoredString.of(String.format(getFormat(), dest.getBlockX(), dest.getBlockY(), dest.getBlockZ(), Math.floor(p.getLocation().distance(dest)) + 0))
            ));
        });
    }
}
