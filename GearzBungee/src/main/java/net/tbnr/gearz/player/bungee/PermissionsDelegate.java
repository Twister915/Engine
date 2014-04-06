package net.tbnr.gearz.player.bungee;

import java.util.List;

/**
 * Delegate for GearzPermissions which
 * allows for the retrieval of player
 * metadata including prefixes, suffixes,
 * and other things required for permissions
 *
 * <p>
 * Latest Change: Create
 * <p>
 *
 * @author Jake
 * @since 2/12/2014
 */
public interface PermissionsDelegate {
    String getPrefix(String player);

    String getSuffix(String player);

    String getTabColor(String player);

    String getNameColor(String player);

    List<String> getValidPermissions(String player);

    List<String> getAllPermissions(String player);
}
