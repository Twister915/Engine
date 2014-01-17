package net.tbnr.gearz.game;

import lombok.Getter;
import net.tbnr.gearz.GearzPlugin;
import org.bukkit.Bukkit;

/**
 * Countdown Class, create an instance of this, and implement the GameCountdownHandler to utilize this.
 * You will recieve calls every second notifying you of a time update.
 */
public final class GameCountdown implements Runnable {
    /**
     * Number of seconds to countdown from.
     */
    private Integer seconds;
    /**
     * Number of seconds passed.
     */
    private Integer passed;
    /**
     * Handler storage. This will get calls.
     */
    private GameCountdownHandler handler;
    /**
     * Plugin to schedule the event against.
     */
    private GearzPlugin plugin;
    /**
     * Stores if the timer has started or not.
     */
    @Getter
    private boolean started;
    @Getter
    private boolean done;

    /**
     * The game countdown constructor. Pass seconds, handler, and a plugin.
     *
     * @param seconds The number of seconds to countdown from
     * @param handler The handler to send status updates to
     * @param game    The game this is for.
     */
    public GameCountdown(Integer seconds, GameCountdownHandler handler, GearzGame game) {
        this(seconds, handler, game.getPlugin());
    }

    /**
     * The game countdown constructor. Pass seconds, handler, and a plugin.
     *
     * @param seconds The number of seconds to countdown from
     * @param handler The handler to send status updates to
     * @param plugin  The game this is for.
     */
    public GameCountdown(Integer seconds, GameCountdownHandler handler, GearzPlugin plugin) {
        this.seconds = seconds;
        this.handler = handler;
        this.passed = 0;
        this.plugin = plugin;
        this.started = false;
    }

    /**
     * Starts the timer.
     */
    public void start() {
        if (this.started) {
            return;
        }
        this.done = false;
        this.started = true;
        try {
            this.handler.onCountdownStart(this.seconds, this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        schedule();
    }

    @Override
    /**
     * This is for implementing a Runnable for Bukkit :D
     */
    public void run() {
        if (!this.started) return;
        if (this.done) return;
        passed++;
        try {
            handler.onCountdownChange(seconds - passed, seconds, this);
            if (passed.equals(seconds)) {
                handler.onCountdownComplete(this);
                this.started = true;
            } else {
                schedule();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            schedule();
        }
    }

    /**
     * Schedules the countdown to do the next second.
     */
    private void schedule() {
        Bukkit.getScheduler().runTaskLater(this.plugin, this, 20);
    }

    /**
     * Gets the handler.
     *
     * @return The handler.
     */
    public GameCountdownHandler getHandler() {
        return handler;
    }

    /**
     * Get the amount of time passed so far.
     *
     * @return This will pas the amount of time that was passed so far.
     */
    @SuppressWarnings("unused")
    public Integer getPassed() {
        return passed;
    }

    /**
     * Get the number of seconds to count down from.
     *
     * @return Seconds :D
     */
    @SuppressWarnings("unused")
    public Integer getSeconds() {
        return seconds;
    }

    /**
     * Stop the countdown
     */
    public void stop() {
        this.started = false;
        this.done = true;
        this.handler.onCountdownComplete(this);
    }
}
