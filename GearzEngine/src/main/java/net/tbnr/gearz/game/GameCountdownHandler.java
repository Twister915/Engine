package net.tbnr.gearz.game;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/10/13
 * Time: 10:14 AM
 * To change this template use File | Settings | File Templates.
 */
public interface GameCountdownHandler {
    public void onCountdownStart(Integer max, GameCountdown countdown);

    public void onCountdownChange(Integer seconds, Integer max, GameCountdown countdown);

    public void onCountdownComplete(GameCountdown countdown);
}
