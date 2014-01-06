package net.tbnr.gearz.game;

import net.tbnr.gearz.GearzException;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 11/19/13
 * Time: 10:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class GameStartException extends GearzException {
    private GameStartFailureCause cause;

    public GameStartException(String s, GameStartFailureCause cause) {
        super(s);
        this.cause = cause;
    }

    @SuppressWarnings("unused")
    public GameStartFailureCause getFailureCause() {
        return cause;
    }
}
