package net.tbnr.util;

import java.util.List;

/**
 * Created by jake on 2/12/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public interface PermissionsDelegate {
    String getPrefix(String player);

    String getSuffix(String player);

    String getTabColor(String player);

    String getNameColor(String player);

    List<String> getValidPermissions(String player);

    List<String> getAllPermissions(String player);
}
