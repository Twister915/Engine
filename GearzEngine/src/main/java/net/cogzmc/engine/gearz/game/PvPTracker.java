/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogzmc.engine.gearz.game;

import com.mongodb.BasicDBList;
import lombok.*;
import net.cogzmc.engine.gearz.Gearz;
import net.cogzmc.engine.gearz.player.GearzPlayer;
import net.cogzmc.engine.util.player.TPlayerStorable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to track kill stats, etc.
 */
@RequiredArgsConstructor
public final class PvPTracker<PlayerType extends GearzPlayer> {
    @NonNull
    private final GearzGame<PlayerType, ?> game;
    private HashMap<PlayerType, PvPPlayer<PlayerType>> playerTrackers;

    void startGame() {
        this.playerTrackers = new HashMap<>();
        for (PlayerType player : game.getPlayers()) {
            this.playerTrackers.put(player, new PvPPlayer<PlayerType>(player));
        }
    }

    void trackKill(PlayerType killer, PlayerType dead) {
        this.playerTrackers.get(killer).logKill(dead);
        this.playerTrackers.get(dead).logDeath(killer);
    }

    void saveKills() {
        for (final Map.Entry<PlayerType, PvPPlayer<PlayerType>> entry : playerTrackers.entrySet()) {
            final PlayerType player = entry.getKey();
            final PvPPlayer<PlayerType> playerTracker = entry.getValue();
            PlayerList<PlayerType> deaths = PlayerList.loadPlayerList(PlayerListKey.Deaths, player);
            PlayerList<PlayerType> kills = PlayerList.loadPlayerList(PlayerListKey.Kills, player);
            for (final PlayerType killer : playerTracker.getDeaths()) {
                deaths.addPlayer(killer);
            }
            for (final PlayerType slain : playerTracker.getKills()) {
                kills.addPlayer(slain);
            }
            player.getTPlayer().store(Gearz.getInstance(), deaths);
            player.getTPlayer().store(Gearz.getInstance(), kills);
            playerTracker.truncate();
        }
    }

    public Integer getKillstreakFor(PlayerType player) {
        return this.playerTrackers.get(player).getSequencedKills();
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @RequiredArgsConstructor
    @EqualsAndHashCode
    @ToString
    private static final class PvPPlayer<PlayerType2 extends GearzPlayer> {
        @NonNull private GearzPlayer player;
        @Getter private List<PlayerType2> kills = new ArrayList<>();
        @Getter private List<PlayerType2> deaths = new ArrayList<>();
        @Getter private Integer sequencedKills = 0;
        public void logKill(PlayerType2 killed) {
            this.sequencedKills++;
            this.kills.add(killed);
        }
        public void logDeath(PlayerType2 killer) {
            this.sequencedKills = 0;
            this.deaths.add(killer);
        }
        void truncate() {
            this.kills = new ArrayList<>();
            this.deaths = new ArrayList<>();
        }
    }

    @RequiredArgsConstructor
    @Data
    public static final class PlayerList<PlayerType2 extends GearzPlayer> implements TPlayerStorable {
        @NonNull
        private final String key;
        @NonNull
        private final BasicDBList players;

        public static <T extends GearzPlayer> PlayerList<T> loadPlayerList(PlayerListKey key, T player) {
            String key_string = (key == PlayerListKey.Kills) ? "kills" : "deaths";
            Object storable = player.getTPlayer().getStorable(Gearz.getInstance(), key_string);
            BasicDBList list = new BasicDBList();
            if (storable != null) {
                list = (BasicDBList) storable;
            }
            return new PlayerList<>(key_string, list);
        }

        public void addPlayer(PlayerType2 player) {
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
