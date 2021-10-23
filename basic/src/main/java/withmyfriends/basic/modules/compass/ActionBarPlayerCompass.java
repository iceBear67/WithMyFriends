package withmyfriends.basic.modules.compass;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ActionBarPlayerCompass extends ActionBarLocationCompass {
    private final Player target;

    @Override
    public Location getDestination() {
        return target.getLocation();
    }

    @Override
    public String getFormat() {
        return "&6%d %d %d &b &b| &a" + target.getDisplayName() + " &b| Distance: &f%f";
    }
}
