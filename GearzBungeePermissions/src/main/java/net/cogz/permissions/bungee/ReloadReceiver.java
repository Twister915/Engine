package net.cogz.permissions.bungee;

import net.tbnr.gearz.command.NetCommandHandler;

import java.util.HashMap;

/**
 * Manages permissions reloads for BungeeCord
 */
public class ReloadReceiver {
    @NetCommandHandler(args = {"reload"}, name = "permissions")
    public void permissions(HashMap<String, Object> args) {
        GearzBungeePermissions.getInstance().getPermsManager().reload();
    }
}
