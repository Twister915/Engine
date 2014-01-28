package net.tbnr.gearz.player;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.event.player.PlayerChangeDonorPointsEvent;
import net.tbnr.gearz.event.player.PlayerLevelChangeEvent;
import net.tbnr.gearz.event.player.PlayerPointChangeEvent;
import net.tbnr.gearz.event.player.PlayerXPChangeEvent;
import net.tbnr.gearz.game.GearzGame;
import net.tbnr.util.player.TPlayer;
import net.tbnr.util.player.TPlayerStorable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/24/13
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
@EqualsAndHashCode(of = {"username"}, doNotUseGetters = true)
@ToString(exclude = {"hideStats", "game"})
public final class GearzPlayer {
    private final TPlayer player;
    private final String username;
    private boolean hideStats;
    private static final Integer magic_number = 7;
    private GearzGame game;
    private static HashMap<TPlayer, GearzPlayer> players;
    private static boolean scoreboard;

    static {
        GearzPlayer.players = new HashMap<>();
        scoreboard = Gearz.getInstance().getConfig().getBoolean("scoreboard");
    }

    @SuppressWarnings("unused")
    public void toggleStats() {
        setHideStats(!hideStats);

    }

    public void setHideStats(boolean h) {
        if (!scoreboard) return;
        if (this.getPlayer() == null) {
            return;
        }
        this.hideStats = h;
        if (player == null) {
            return;
        }
        if (player.getPlayer() == null) {
            return;
        }
        if (!player.getPlayer().isOnline()) {
            return;
        }
        if (this.hideStats) {
            this.player.getPlayer().setExp(0);
            this.player.getPlayer().setLevel(0);
            this.player.getPlayer().setTotalExperience(0);
            this.player.resetScoreboard();
        } else {
            this.player.resetScoreboard();
            this.setupScoreboard();
            Bukkit.getScheduler().runTaskLater(Gearz.getInstance(), new Runnable() {
                @Override
                public void run() {
                    updateStats();
                }
            }, 5L);
        }
    }

    @SuppressWarnings("unused")
    public boolean areStatsHidden() {
        return this.hideStats;
    }

    private GearzPlayer(TPlayer player) {
        this.player = player;
        this.username = player.getPlayerName();
        GearzPlayer.players.put(player, this);
    }

    public void addXp(int xp) {
        Integer current_xp = getXP();
        Integer newXp = Math.max(0, current_xp + xp);
        this.player.store(Gearz.getInstance(), new GPlayerXP(newXp));
        Bukkit.getPluginManager().callEvent(new PlayerXPChangeEvent(current_xp, newXp, this));
        this.updateStats();
    }

    public void addPoints(int points) {
        Integer current_points = getPoints();
        Integer newPoints = Math.max(0, current_points + points);
        PlayerPointChangeEvent playerPointChangeEvent = new PlayerPointChangeEvent(this, current_points, newPoints, points);
        Bukkit.getPluginManager().callEvent(playerPointChangeEvent);
        if (playerPointChangeEvent.isCancelled()) {
            return;
        }
        if (playerPointChangeEvent.getPoints() != points) newPoints = Math.max(0, current_points + playerPointChangeEvent.getPoints());
        this.player.store(Gearz.getInstance(), new GPlayerPoints(newPoints));
        this.updateStats();
    }

    @SuppressWarnings("unused")
    public void addDonorPoint(int points) {
        Integer current_points = getDonorPoints();
        Integer newPoint = Math.max(0, current_points + points);
        this.player.store(Gearz.getInstance(), new GPlayerDonorPoints(newPoint));
        Bukkit.getPluginManager().callEvent(new PlayerChangeDonorPointsEvent(current_points, newPoint, this));
        this.updateStats();
    }

    private void setLevel(int level) {
        this.player.store(Gearz.getInstance(), new GPlayerLevel(level));
    }

    public Integer getLevel() {
        Integer storable = (Integer) this.player.getStorable(Gearz.getInstance(), "gearz-level");
        if (storable == null) {
            storable = 0;
        }
        return storable;
    }

    public Integer getPoints() {
        Integer storable = (Integer) this.player.getStorable(Gearz.getInstance(), "gearz-points");
        if (storable == null) {
            storable = 0;
        }
        return storable;
    }

    public Integer getXP() {
        Integer storable = (Integer) this.player.getStorable(Gearz.getInstance(), "gearz-xp");
        if (storable == null) {
            storable = 0;
        }
        return storable;
    }

    public Integer getDonorPoints() {
        Integer donorPoints = (Integer) this.player.getStorable(Gearz.getInstance(), "gearz-dpoints");
        if (donorPoints == null) {
            donorPoints = 0;
        }
        return donorPoints;
    }

    public void updateStats() {
        if (!scoreboard) return;
        Integer xp = this.getXP();
        int new_level = this.getLevelFromXP(xp);
        Integer level = this.getLevel();
        if (level != new_level) {
            Bukkit.getPluginManager().callEvent(new PlayerLevelChangeEvent(level, new_level, this));
            this.setLevel(new_level);
            level = new_level;
        }
        if (this.hideStats) {
            return;
        }
        if (this.player == null) {
            return;
        }
        if (this.player.getPlayer() == null) {
            return;
        }
        this.player.getPlayer().setLevel(level);
        this.player.getPlayer().setExp(this.getProgressTowardsLevel(xp));
        Gearz instance = Gearz.getInstance();
        this.player.setScoreboardSideTitle(instance.getFormat("formats.sidebar-title"));
        this.player.setScoreBoardSide(instance.getFormat("formats.xp-sidebar"), getXP());
        this.player.setScoreBoardSide(instance.getFormat("formats.donor-points-sidebar"), getDonorPoints());
        this.player.setScoreBoardSide(instance.getFormat("formats.points-sidebar"), getPoints());
        this.player.setScoreBoardSide(instance.getFormat("formats.level-sidebar"), getLevel());
    }

    public void setupScoreboard() {
        if (!scoreboard) return;
        Gearz instance = Gearz.getInstance();
        this.player.setScoreboardSideTitle(instance.getFormat("formats.sidebar-title-loading"));
        this.player.setScoreBoardSide(instance.getFormat("formats.xp-sidebar"), -1);
        this.player.setScoreBoardSide(instance.getFormat("formats.donor-points-sidebar"), -2);
        this.player.setScoreBoardSide(instance.getFormat("formats.points-sidebar"), -3);
        this.player.setScoreBoardSide(instance.getFormat("formats.level-sidebar"), -4);
    }

    private Integer getLevelFromXP(int xp) {
        if (xp < 0) {
            return 0;
        }
        return (int) Math.floor(Math.sqrt(xp) / GearzPlayer.magic_number);
    }

    private float getProgressTowardsLevel(int xp) {
        return (float) ((Math.sqrt(xp) / GearzPlayer.magic_number) % 1f);
    }

    public static GearzPlayer playerFromTPlayer(TPlayer player) {
        return GearzPlayer.players.containsKey(player) ? GearzPlayer.players.get(player) : new GearzPlayer(player);
    }

    public static GearzPlayer playerFromPlayer(Player player) {
        if (player == null) throw new NullPointerException("Cannot pass a null player!");
        return GearzPlayer.playerFromTPlayer(Gearz.getInstance().getPlayerManager().getPlayer(player));
    }

    public static void removePlayer(TPlayer player) {
        GearzPlayer.players.remove(player);
    }

    public GearzGame getGame() {
        return game;
    }

    public void setGame(GearzGame game) {
        this.game = game;
    }

    @SuppressWarnings("unused")
    public String getUsername() {
        return username;
    }

    public static class GPlayerXP implements TPlayerStorable {
        private final Integer xp;

        public GPlayerXP(Integer xp) {
            this.xp = xp;
        }

        @Override
        public String getName() {
            return "gearz-xp";
        }

        @Override
        public Object getValue() {
            return xp;
        }
    }

    public static class GPlayerLevel implements TPlayerStorable {
        private final Integer level;

        public GPlayerLevel(Integer level) {
            this.level = level;
        }

        @Override
        public String getName() {
            return "gearz-level";
        }

        @Override
        public Object getValue() {
            return level;
        }
    }

    public static class GPlayerPoints implements TPlayerStorable {
        private final Integer points;

        public GPlayerPoints(Integer points) {
            this.points = points;
        }

        @Override
        public String getName() {
            return "gearz-points";
        }

        @Override
        public Object getValue() {
            return points;
        }
    }

    public TPlayer getTPlayer() {
        return this.player;
    }

    public Player getPlayer() {
        return this.player.getPlayer();
    }

    public static class GPlayerDonorPoints implements TPlayerStorable {
        private final Integer points;

        public GPlayerDonorPoints(Integer newPoint) {
            points = newPoint;
        }

        @Override
        public String getName() {
            return "gearz-dpoints";
        }

        @Override
        public Object getValue() {
            return points;
        }
    }

    public void sendException(Throwable t) {
        getPlayer().sendMessage(ChatColor.RED + "Error: " + ChatColor.WHITE + t.getMessage());
    }

    public boolean isValid() {
        if (Gearz.getInstance().showDebug()) {
            Gearz.getInstance().getLogger().info("GEARZ DEBUG ---<GearzPlayer|279>--------< isValid has been CAUGHT for: " + this.username + " and it returned: " + this.player.getPlayer());
        }
        return this.player.getPlayer() != null && this.player.isOnline();
    }
}
