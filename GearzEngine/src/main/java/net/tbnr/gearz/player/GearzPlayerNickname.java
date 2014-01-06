package net.tbnr.gearz.player;

import net.tbnr.util.player.TPlayerStorable;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/15/13
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class GearzPlayerNickname implements TPlayerStorable {
    private String value;

    public GearzPlayerNickname(String nick) {
        this.value = nick;
    }

    @Override
    public String getName() {
        return "nickname";
    }

    @Override
    public Object getValue() {
        return value;
    }
}
