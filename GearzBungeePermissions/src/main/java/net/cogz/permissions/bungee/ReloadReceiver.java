package net.cogz.permissions.bungee;

import net.tbnr.gearz.command.NetCommandHandler;

import java.util.HashMap;

/**
 * Created by jake on 2/15/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class ReloadReceiver {
    @NetCommandHandler(args = {"reload"}, name = "permissions")
    public void permissions(HashMap<String, Object> args) {
        GearzBungeePermissions.getInstance().getPermsManager().reload();
    }
}
