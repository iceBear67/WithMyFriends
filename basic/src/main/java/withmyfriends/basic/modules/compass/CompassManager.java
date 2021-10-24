package withmyfriends.basic.modules.compass;

import io.ib67.serverutil.WithMyFriends;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum CompassManager {
    INSTANCE;
    private Map<UUID, CompassHolder> holders = new ConcurrentHashMap<>(); // it will be used in async schedulers.

    void init() {
        Bukkit.getScheduler().runTaskTimer(WithMyFriends.getInstance().asPlugin(), new Task(), 0L, 5L);
    }

    // Shortcuts.
    public boolean hasPlayer(UUID uuid) {
        return holders.containsKey(uuid);
    }

    public boolean hasPlayer(Player player) {
        return hasPlayer(player.getUniqueId());
    }

    public boolean hasPlayer(String name) {
        return hasPlayer(Optional.ofNullable(Bukkit.getPlayerExact(name)).map(Player::getUniqueId).orElse(null));
    }

    public void addHolder(UUID uuid, CompassHolder holder) {
        holders.put(uuid, holder);
    }

    public CompassHolder getHolderByUUID(UUID id) {
        return holders.get(id);
    }

    public void removeHolder(UUID id) {
        holders.remove(id);
    }

    public Collection<? extends CompassHolder> getHolders() {
        return holders.values();
    }

    private class Task implements Runnable {
        @Override
        public void run() {
            for (Map.Entry<UUID, CompassHolder> entry : holders.entrySet()) {
                entry.getValue().update();
            }
        }
    }
}
