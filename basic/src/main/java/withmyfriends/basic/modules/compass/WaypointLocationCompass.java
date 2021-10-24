package withmyfriends.basic.modules.compass;

import io.ib67.util.bukkit.ColoredString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

@RequiredArgsConstructor
public class WaypointLocationCompass implements ICompass {
    @Getter
    private final Location destination;
    private Chunk lastChunk;
    private Entity lastChicken;

    private static Location getIntersectPoint(Location p1, Location p2, Location p3, Location p4) {
        // line 1
        double x1 = p1.getX(), y1 = p1.getZ(), x2 = p2.getX(), y2 = p2.getZ();
        double a = (y1 - y2) / (x1 - x2);
        double b = (x1 * y2 - x2 * y1) / (x1 - x2);

        //line 2
        double x3 = p3.getX(), y3 = p3.getZ(), x4 = p4.getX(), y4 = p4.getZ();
        double c = (y3 - y4) / (x3 - x4);
        double d = (x3 * y4 - x4 * y3) / (x3 - x4);

        double x = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));

        double y = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));

        return new Location(p1.getWorld(), x, 0, y);
    }

    @Override
    public void onUpdate(CompassHolder holder) {
        var player = holder.getAsPlayer().orElse(null);
        if (player == null) {
            return;
        }
        if (lastChicken != null) {
            var newloc = lastChicken.getLocation();
            newloc.setY(player.getLocation().getY());
            lastChicken.teleport(newloc);
        }
        var chunk = player.getLocation().getChunk();
        if (lastChunk == chunk) {
            return;
        }
        lastChunk = chunk;
        var blockA = chunk.getBlock(0, 0, 0).getLocation();
        var blockB = chunk.getBlock(15, 0, 15).getLocation();
        var blockC = chunk.getBlock(0, 0, 15).getLocation();
        var blockD = chunk.getBlock(15, 0, 0).getLocation();

        var arr = new Point[]{
                new Point(blockA, destination.distance(blockA)),
                new Point(blockB, destination.distance(blockB)),
                new Point(blockC, destination.distance(blockC)),
                new Point(blockD, destination.distance(blockD))
        };
        Arrays.sort(arr, Comparator.naturalOrder());
        var pointA1 = arr[0];
        var pointA2 = arr[1];

        var pointB1 = player.getLocation();
        var pointB2 = destination;

        var loc = getIntersectPoint(Objects.requireNonNull(pointA1).loc, pointA2.loc, pointB1, pointB2);
        loc.setY(player.getLocation().getY());
        if (lastChicken == null) {
            var waypoint = player.getWorld().spawnEntity(loc, EntityType.CHICKEN);
            waypoint.setInvulnerable(true);
            waypoint.setGravity(false);
            waypoint.setGlowing(true);
            waypoint.setCustomNameVisible(true);
            waypoint.setCustomName(ColoredString.of("&bThe &lWaypoint"));
            lastChicken = waypoint;
        } else {
            lastChicken.teleport(loc);
        }

    }

    @AllArgsConstructor
    private static class Point implements Comparable<Point> {
        private Location loc;
        private double len;

        @Override
        public int compareTo(Point o) {
            return (int) (len - o.len);
        }
    }
}
