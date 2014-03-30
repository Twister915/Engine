package net.tbnr.util;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import com.mojang.api.profiles.ProfileCriteria;

/**
 * Created by jake on 3/29/2014.
 *
 * Purpose Of File:
 *
 * Latest Change:
*/
public class UUIDUtil implements GUtility {
    private static String AGENT = "minecraft";
    private static final HttpProfileRepository repository = new HttpProfileRepository();

    public static String getUUID(String player) throws UUIDException {
        Profile[] profiles = repository.findProfilesByCriteria(new ProfileCriteria(player, AGENT));
        if (profiles.length == 1) {
            return profiles[0].getId();
        } else {
            throw new UUIDException("Failed to retrieve UUID");
        }
    }

    public static class UUIDException extends Exception {
        public UUIDException(String message) {
            super(message);
        }
    }
}
