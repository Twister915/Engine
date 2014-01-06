package net.tbnr.gearz.game;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import lombok.Getter;
import lombok.NonNull;
import net.tbnr.gearz.player.GearzPlayer;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

/**
 * Stores metrics on the games
 */
public class GearzMetrics {
    @Getter @NonNull
    private DBCollection metricsCollection;
    @Getter @NonNull
    private GearzGame game;
    @Getter
    private Long gameStart;
    @Getter
    private Long gameEnd;
    private Set<GearzPlayer> players;

    public static GearzMetrics beginTracking(GearzGame game) {
        DB mongoDB = game.getPlugin().getMongoDB();
        DBCollection metrics = mongoDB.getCollection("metrics");
        GearzMetrics gearzMetrics = new GearzMetrics();
        gearzMetrics.metricsCollection = metrics;
        gearzMetrics.game = game;
        return gearzMetrics;
    }

    public GearzMetrics startGame() {
        this.gameStart = Calendar.getInstance().getTimeInMillis();
        this.players = game.allPlayers();
        return this;
    }

    public GearzMetrics finishGame() {
        this.gameEnd = Calendar.getInstance().getTimeInMillis();
        return this;
    }

    public void done(Map<String, Object> data) {
        BasicDBObject object = new BasicDBObject();
        object.put("game", game.getGameMeta().key());
        object.put("game_start", gameStart);
        object.put("game_end", gameEnd);
        object.put("game_length", gameEnd - gameStart);
        object.put("arena", game.getArena().getId());
        BasicDBList players = new BasicDBList();
        for (GearzPlayer player : this.players) {
            players.add(player.getTPlayer().getPlayerDocument().get("_id"));
        }
        object.put("players", players);
        if (data != null) {
            for (Map.Entry<String, Object> stringObjectEntry : data.entrySet()) {
                object.put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
            }
        }
        this.metricsCollection.save(object);
    }

    public void done() {
        done(null);
    }
}
