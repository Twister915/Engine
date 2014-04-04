package net.tbnr.gearz.game.classes;

import lombok.Getter;
import net.tbnr.gearz.game.GearzGame;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class GearzAbstractClass<PlayerType extends GearzPlayer> implements Listener {
    @Getter private final PlayerType player;
    @Getter private final GearzGame game;
    @Getter private final GearzClassMeta meta;

    public GearzAbstractClass(PlayerType player, GearzGame game) {
        this.player = player;
        this.game = game;
        this.meta = getClass().getAnnotation(GearzClassMeta.class);
        onConstructor();
    }

    protected void onConstructor() {}
    public void onGameStart() {}
    public void onGameEndForPlayer() {}
    public void onPlayerActivate() {}
    public void onClassDeactivate() {}
    public void onClassActivate() {}

    public void registerClass() {
        game.getPlugin().registerEvents(this);
    }

    public void deregisterClass() {
        HandlerList.unregisterAll(this);
    }
}
