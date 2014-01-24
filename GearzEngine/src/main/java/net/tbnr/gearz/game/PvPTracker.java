package net.tbnr.gearz.game;

import com.mongodb.BasicDBList;
import lombok.*;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.player.GearzPlayer;
import net.tbnr.util.player.TPlayerStorable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to track kill stats, etc.
 */
@RequiredArgsConstructor
public final class PvPTracker {
    @NonNull
    private final GearzGame game;
    private HashMap<GearzPlayer, PvPPlayer> playerTrackers;

    void startGame() {
        this.playerTrackers = new HashMap<>();
        for (GearzPlayer player : game.getPlayers()) {
            this.playerTrackers.put(player, new PvPPlayer(player));
        }
    }

    void trackKill(GearzPlayer killer, GearzPlayer dead) {
        this.playerTrackers.get(killer).logKill(dead);
        this.playerTrackers.get(dead).logDeath(killer);
    }

    void saveKills() {
        for (final Map.Entry<GearzPlayer, PvPPlayer> entry : playerTrackers.entrySet()) {
            final GearzPlayer player = entry.getKey();
            final PvPPlayer playerTracker = entry.getValue();
            PlayerList deaths = PlayerList.loadPlayerList(PlayerListKey.Deaths, player);
            PlayerList kills = PlayerList.loadPlayerList(PlayerListKey.Kills, player);
            for (final GearzPlayer killer : playerTracker.getDeaths()) {
                deaths.addPlayer(killer);
            }
            for (final GearzPlayer slain : playerTracker.getKills()) {
                kills.addPlayer(slain);
            }
            player.getTPlayer().store(Gearz.getInstance(), deaths);
            player.getTPlayer().store(Gearz.getInstance(), kills);
            playerTracker.truncate();
        }
    }

    public Integer getKillstreakFor(GearzPlayer player) {
        return this.playerTrackers.get(player).getSequencedKills();
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @RequiredArgsConstructor
    @EqualsAndHashCode
    @ToString
    private static final class PvPPlayer {
        @NonNull private GearzPlayer player;
        @Getter private List<GearzPlayer> kills = new ArrayList<>();
        @Getter private List<GearzPlayer> deaths = new ArrayList<>();
        @Getter private Integer sequencedKills = 0;
        public void logKill(GearzPlayer killed) {
            this.sequencedKills++;
            this.kills.add(killed);
        }
        public void logDeath(GearzPlayer killer) {
            this.sequencedKills = 0;
            this.deaths.add(killer);
        }
        void truncate() {
            this.kills = new ArrayList<>();
            this.deaths = new ArrayList<>();
        }
    }

    @Data
    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public final static class PlayerList implements TPlayerStorable {
        @NonNull
        private final String key;
        @NonNull
        private final BasicDBList players;

        public static PlayerList loadPlayerList(PlayerListKey key, GearzPlayer player) {
            String key_string = (key == PlayerListKey.Kills) ? "kills" : "deaths";
            Object storable = player.getTPlayer().getStorable(Gearz.getInstance(), key_string);
            BasicDBList list = new BasicDBList();
            if (storable != null) {
                list = (BasicDBList) storable;
            }
            return new PlayerList(key_string, list);
        }

        public void addPlayer(GearzPlayer player) {
            players.add(player.getTPlayer().getPlayerDocument().get("_id"));
        }

        @Override
        public String getName() {
            return key;
        }

        @Override
        public Object getValue() {
            return players;
        }
    }

    public static enum PlayerListKey {
        Kills,
        Deaths
    }
}
