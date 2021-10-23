package withmyfriends.basic.modules.compass;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CompassHolder {
    private final UUID holdingPlayer;
    private final ICompass holdingType;

    public Optional<Player> getAsPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(holdingPlayer));
    }

    public void update() {
        holdingType.onUpdate(this);
    }
}
