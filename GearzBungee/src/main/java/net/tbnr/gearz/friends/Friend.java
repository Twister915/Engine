package net.tbnr.gearz.friends;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by Jake on 1/27/14.
 *
 * Purpose Of File: Storage of friend related data
 *
 * Latest Change:
 */
@ToString
public class Friend {
    @Getter String player;
    @Getter boolean online;

    public Friend(String player, boolean online) {
        this.player = player;
        this.online = online;
    }

    public static class FriendRequestexception extends Exception {
        public FriendRequestexception(String s) {
            super(s);
        }
    }
}
